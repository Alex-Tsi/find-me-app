import { Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './auth/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import PublicationsPage from './pages/PublicationsPage';
import PublicationDetailPage from './pages/PublicationDetailPage';
import PublicationEditPage from './pages/PublicationEditPage';
import ProfilePage from './pages/ProfilePage';
import ChatPage from './pages/ChatPage';

// Карта маршрутов. Аналог @RequestMapping, только на стороне браузера:
// при смене URL React рендерит соответствующую страницу без перезагрузки.
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Публичные */}
        <Route path="/" element={<PublicationsPage />} />
        <Route path="/publications/:id" element={<PublicationDetailPage />} />
        <Route path="/profile/:userId" element={<ProfilePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Требуют входа — обёрнуты в ProtectedRoute */}
        <Route
          path="/publications/new"
          element={<ProtectedRoute><PublicationEditPage /></ProtectedRoute>}
        />
        <Route
          path="/publications/:id/edit"
          element={<ProtectedRoute><PublicationEditPage /></ProtectedRoute>}
        />
        <Route
          path="/profile"
          element={<ProtectedRoute><ProfilePage /></ProtectedRoute>}
        />
        <Route
          path="/chat"
          element={<ProtectedRoute><ChatPage /></ProtectedRoute>}
        />

        <Route path="*" element={<div className="container">Страница не найдена</div>} />
      </Route>
    </Routes>
  );
}
