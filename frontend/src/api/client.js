import axios from 'axios';

// =============================================================================
//  HTTP-КЛИЕНТ. Аналог сконфигурированного RestTemplate/WebClient с фильтрами.
//  Здесь сосредоточена ВСЯ низкоуровневая работа с сетью: базовый URL, проброс
//  токена и автоматическое обновление по 401. Компоненты сюда не лезут.
// =============================================================================

const API_BASE = import.meta.env.VITE_API_BASE || ''; // '' → относительные пути (проксирует Vite/nginx)

// --- Хранилище access-токена ---------------------------------------------------
// Токен лежит В ПАМЯТИ модуля (обычная переменная), НЕ в localStorage.
// Почему: localStorage читается любым JS на странице → уязвим к XSS.
// Минус памяти: при перезагрузке страницы токен теряется — но мы восстановим
// сессию «тихим» refresh'ом по httpOnly-cookie (см. AuthContext).
let accessToken = null;
export function setAccessToken(token) {
  accessToken = token;
}
export function getAccessToken() {
  return accessToken;
}

export const api = axios.create({
  baseURL: API_BASE,
  withCredentials: true, // разрешаем браузеру слать/принимать httpOnly refresh-cookie
});

// --- REQUEST-интерсептор -------------------------------------------------------
// Срабатывает перед КАЖДЫМ запросом. Аналог сервлет-фильтра, дописывающего заголовок.
// Подставляет Authorization: Bearer <accessToken>, если токен есть.
api.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

// --- RESPONSE-интерсептор: авто-refresh на 401 ---------------------------------
// Если access-токен протух (15 мин), бэкенд вернёт 401. Тогда мы ОДИН раз
// дёргаем /api/auth/refresh (refresh-cookie уйдёт автоматически), получаем новый
// access и ПОВТОРЯЕМ исходный запрос. Для пользователя всё бесшовно.
//
// refreshPromise — «single-flight»: если 401 прилетели сразу на нескольких
// запросах, рефреш выполнится один раз, остальные дождутся того же промиса
// (иначе устроили бы шторм одновременных refresh и переотозвали бы токены).
let refreshPromise = null;

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    const status = error.response?.status;

    // Не зацикливаемся: не рефрешим сами auth-эндпоинты и не повторяем дважды.
    const isAuthCall = original?.url?.includes('/api/auth/');

    if (status === 401 && original && !original._retry && !isAuthCall) {
      original._retry = true;
      try {
        if (!refreshPromise) {
          refreshPromise = axios
            .post(`${API_BASE}/api/auth/refresh`, null, { withCredentials: true })
            .finally(() => {
              refreshPromise = null;
            });
        }
        const { data } = await refreshPromise;
        setAccessToken(data.accessToken);
        original.headers.Authorization = `Bearer ${data.accessToken}`;
        return api(original); // повторяем исходный запрос с новым токеном
      } catch (refreshError) {
        // Refresh не удался (cookie нет/протухла/отозвана) → разлогиниваем.
        setAccessToken(null);
        // Событие ловит AuthContext и чистит пользователя → ProtectedRoute уводит на /login.
        window.dispatchEvent(new Event('auth:logout'));
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);
