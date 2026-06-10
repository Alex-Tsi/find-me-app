import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { publicationsApi } from '../api/publications';
import { useAuth } from '../auth/AuthContext';

export default function PublicationsPage() {
  const [items, setItems] = useState([]);
  const [tags, setTags] = useState('');
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  // Загрузка данных при открытии страницы (useEffect с [] = один раз при монтировании).
  const load = () => {
    setLoading(true);
    const request = tags.trim() ? publicationsApi.filter(tags.trim()) : publicationsApi.list();
    request.then(setItems).finally(() => setLoading(false));
  };
  useEffect(() => { load(); }, []); // eslint-disable-line

  const handleFilter = (e) => {
    e.preventDefault();
    load();
  };

  return (
    <>
      <div className="row" style={{ alignItems: 'center' }}>
        <h2>Публикации</h2>
        {user && <Link to="/publications/new"><button>Создать</button></Link>}
      </div>

      <form onSubmit={handleFilter} className="row" style={{ alignItems: 'flex-end' }}>
        <div>
          <label>Поиск по тегам (через пробел)</label>
          <input value={tags} onChange={(e) => setTags(e.target.value)} placeholder="java spring" />
        </div>
        <button type="submit" style={{ maxWidth: 120 }}>Найти</button>
      </form>

      {loading ? (
        <p className="muted">Загрузка…</p>
      ) : items.length === 0 ? (
        <p className="muted">Ничего не найдено</p>
      ) : (
        items.map((p) => (
          <div className="card" key={p.id}>
            <h3><Link to={`/publications/${p.id}`}>{p.title || 'Без названия'}</Link></h3>
            <div className="tags">{p.tags}</div>
            <p>{p.description}</p>
            <div className="muted">
              автор: <Link to={`/profile/${p.author?.id}`}>{p.author?.username}</Link> · {p.date}
            </div>
          </div>
        ))
      )}
    </>
  );
}
