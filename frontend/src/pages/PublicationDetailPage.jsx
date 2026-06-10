import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { publicationsApi } from '../api/publications';
import { useAuth } from '../auth/AuthContext';

export default function PublicationDetailPage() {
  const { id } = useParams();           // :id из URL
  const navigate = useNavigate();
  const { user } = useAuth();
  const [pub, setPub] = useState(null);
  const [commentText, setCommentText] = useState('');
  const [error, setError] = useState(null);

  const load = () => publicationsApi.get(id).then(setPub).catch(() => setError('Публикация не найдена'));
  useEffect(() => { load(); }, [id]); // eslint-disable-line

  const isOwner = user && pub && pub.author && user.userId === pub.author.id;

  const addComment = async (e) => {
    e.preventDefault();
    await publicationsApi.addComment(id, commentText);
    setCommentText('');
    load(); // перечитываем, чтобы показать новый комментарий
  };

  const remove = async () => {
    if (!confirm('Удалить публикацию?')) return;
    await publicationsApi.remove(id);
    navigate('/');
  };

  if (error) return <p className="error">{error}</p>;
  if (!pub) return <p className="muted">Загрузка…</p>;

  return (
    <>
      <div className="card">
        <h2>{pub.title}</h2>
        <div className="tags">{pub.tags}</div>
        {pub.fileName && (
          // Файлы лежат на бэкенде и отдаются по /img/{fileName} (проксирует Vite/nginx).
          <img src={`/img/${pub.fileName}`} alt="" style={{ maxWidth: '100%', borderRadius: 8 }} />
        )}
        <p>{pub.description}</p>
        {pub.motivations && <p><b>Мотивация:</b> {pub.motivations}</p>}
        {pub.rewards && <p><b>Награда:</b> {pub.rewards}</p>}
        {pub.whoNeed && <p><b>Кто нужен:</b> {pub.whoNeed}</p>}
        <div className="muted">
          автор: <Link to={`/profile/${pub.author?.id}`}>{pub.author?.username}</Link> · {pub.date}
        </div>
        {isOwner && (
          <div className="row" style={{ marginTop: 12 }}>
            <Link to={`/publications/${id}/edit`}><button className="secondary">Редактировать</button></Link>
            <button className="danger" onClick={remove}>Удалить</button>
          </div>
        )}
      </div>

      <div className="card">
        <h3>Комментарии</h3>
        {(pub.comments || []).map((c) => (
          <div key={c.id} style={{ borderTop: '1px solid #eee', padding: '6px 0' }}>
            <b>{c.user?.username}: </b>{c.text}
          </div>
        ))}
        {pub.comments?.length === 0 && <p className="muted">Пока нет комментариев</p>}

        {user ? (
          <form onSubmit={addComment} style={{ marginTop: 12 }}>
            <textarea value={commentText} onChange={(e) => setCommentText(e.target.value)}
              placeholder="Ваш комментарий" required />
            <button type="submit">Отправить</button>
          </form>
        ) : (
          <p className="muted"><Link to="/login">Войдите</Link>, чтобы комментировать</p>
        )}
      </div>
    </>
  );
}
