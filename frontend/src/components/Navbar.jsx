import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <Link to="/"><b>Find Me</b></Link>
      <Link to="/">Публикации</Link>
      {user && <Link to="/chat">Чат</Link>}
      {user && <Link to="/profile">Мой профиль</Link>}
      <span className="spacer" />
      {user ? (
        <>
          <span>Привет, {user.username}</span>
          <button className="secondary" onClick={handleLogout}>Выйти</button>
        </>
      ) : (
        <>
          <Link to="/login">Вход</Link>
          <Link to="/register">Регистрация</Link>
        </>
      )}
    </nav>
  );
}
