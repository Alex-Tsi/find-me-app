import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { AuthProvider } from './auth/AuthContext';
import './index.css';

// Точка входа (аналог main()). Монтируем дерево компонентов в <div id="root">.
// BrowserRouter — клиентский роутер (URL ↔ компонент), AuthProvider — глобальное
// состояние аутентификации поверх всего приложения.
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
