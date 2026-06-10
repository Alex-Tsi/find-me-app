import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

// Обёртка для страниц, требующих входа. Аналог security-фильтра:
// нет пользователя → редирект на /login; идёт проверка сессии → показываем «загрузку».
export default function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="container">Загрузка…</div>;
  }
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
}
