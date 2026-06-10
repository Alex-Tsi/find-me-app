import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function RegisterPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await register(username, password);
      navigate('/');
    } catch (err) {
      // 409 от бэкенда = пользователь занят; иначе общий текст.
      setError(err.response?.status === 409 ? 'Такой пользователь уже существует' : 'Ошибка регистрации');
    }
  };

  return (
    <div className="card" style={{ maxWidth: 380, margin: '40px auto' }}>
      <h3>Регистрация</h3>
      <form onSubmit={handleSubmit}>
        <label>Логин</label>
        <input value={username} onChange={(e) => setUsername(e.target.value)} required />
        <label>Пароль (мин. 4 символа)</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} minLength={4} required />
        {error && <div className="error">{error}</div>}
        <button type="submit">Зарегистрироваться</button>
      </form>
      <p className="muted">Уже есть аккаунт? <Link to="/login">Вход</Link></p>
    </div>
  );
}
