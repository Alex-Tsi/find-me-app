import { api } from './client';

// Эндпоинты аутентификации. Возвращают чистые данные (тело ответа),
// чтобы вызывающий код не возился с объектом ответа axios.
export const authApi = {
  login: (username, password) =>
    api.post('/api/auth/login', { username, password }).then((r) => r.data),

  register: (username, password) =>
    api.post('/api/auth/register', { username, password }).then((r) => r.data),

  // refresh/logout полагаются на httpOnly refresh-cookie — тело не нужно.
  refresh: () => api.post('/api/auth/refresh').then((r) => r.data),
  logout: () => api.post('/api/auth/logout').then((r) => r.data),
};
