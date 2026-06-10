import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function LoginPage() {
  // useState = поле компонента; при setX происходит перерисовка.
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault(); // не перезагружаем страницу (это SPA)
    setError(null);
    try {
      await login(username, password);
      navigate('/'); // успех → на список публикаций
    } catch {
      setError('Неверный логин или пароль');
    }
  };

  return (
    <div className="card" style={{ maxWidth: 380, margin: '40px auto' }}>
      <h3>Вход</h3>
      <form onSubmit={handleSubmit}>
        <label>Логин</label>
        <input value={username} onChange={(e) => setUsername(e.target.value)} required />
        <label>Пароль</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        {error && <div className="error">{error}</div>}
        <button type="submit">Войти</button>
      </form>
      <p className="muted">Нет аккаунта? <Link to="/register">Регистрация</Link></p>
    </div>
  );
}
