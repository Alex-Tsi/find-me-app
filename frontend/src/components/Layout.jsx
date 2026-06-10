import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';

// Общий каркас: шапка + место для текущей страницы (<Outlet/> — куда роутер
// подставляет компонент активного маршрута).
export default function Layout() {
  return (
    <>
      <Navbar />
      <main className="container">
        <Outlet />
      </main>
    </>
  );
}
