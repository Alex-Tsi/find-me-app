import { createContext, useContext, useEffect, useState } from 'react';
import { authApi } from '../api/auth';
import { setAccessToken } from '../api/client';

// =============================================================================
//  ГЛОБАЛЬНОЕ СОСТОЯНИЕ АУТЕНТИФИКАЦИИ.
//  React Context — это как application/session-scoped бин, доступный любому
//  компоненту без «прокидывания» через пропсы. Здесь храним текущего
//  пользователя и операции login/register/logout.
// =============================================================================

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);     // { userId, username } | null
  const [loading, setLoading] = useState(true); // true, пока проверяем «не залогинен ли уже»

  // useEffect c [] = код, выполняемый ОДИН раз при старте приложения (аналог @PostConstruct).
  useEffect(() => {
    // Access-токен живёт в памяти и теряется при перезагрузке страницы.
    // Но refresh-cookie (httpOnly) пережила перезагрузку — пробуем «тихо» войти по ней.
    authApi
      .refresh()
      .then((data) => {
        setAccessToken(data.accessToken);
        setUser({ userId: data.userId, username: data.username });
      })
      .catch(() => {
        setAccessToken(null);
        setUser(null);
      })
      .finally(() => setLoading(false));

    // Если refresh где-то упал (см. client.js), он шлёт это событие — чистим пользователя.
    const onLogout = () => {
      setAccessToken(null);
      setUser(null);
    };
    window.addEventListener('auth:logout', onLogout);
    return () => window.removeEventListener('auth:logout', onLogout);
  }, []);

  const login = async (username, password) => {
    const data = await authApi.login(username, password);
    setAccessToken(data.accessToken);
    setUser({ userId: data.userId, username: data.username });
  };

  const register = async (username, password) => {
    const data = await authApi.register(username, password);
    setAccessToken(data.accessToken);
    setUser({ userId: data.userId, username: data.username });
  };

  const logout = async () => {
    try {
      await authApi.logout(); // отзываем refresh-токен на сервере
    } finally {
      setAccessToken(null);
      setUser(null);
    }
  };

  const value = { user, loading, login, register, logout };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// Хук-обёртка: useAuth() в любом компоненте даёт доступ к состоянию/операциям.
export function useAuth() {
  return useContext(AuthContext);
}
