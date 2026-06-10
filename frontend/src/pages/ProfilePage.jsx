import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { profileApi } from '../api/profile';
import { uploadApi } from '../api/upload';
import { useAuth } from '../auth/AuthContext';

// /profile          → мой профиль (редактируемый)
// /profile/:userId  → чужой профиль (только просмотр)
export default function ProfilePage() {
  const { userId } = useParams();
  const { user } = useAuth();
  const isMine = !userId; // нет userId в URL → это /profile (мой)

  const [profile, setProfile] = useState(null);
  const [edit, setEdit] = useState(false);
  const [file, setFile] = useState(null);

  useEffect(() => {
    const request = isMine ? profileApi.me() : profileApi.get(userId);
    request.then(setProfile).catch(() => setProfile(null));
  }, [userId, isMine]);

  const upd = (key) => (e) => setProfile({ ...profile, [key]: e.target.value });

  const save = async (e) => {
    e.preventDefault();
    let body = { ...profile };
    if (file) {
      const { fileName } = await uploadApi.upload(file);
      body.avatar = fileName;
    }
    const saved = await profileApi.update(body);
    setProfile(saved);
    setEdit(false);
  };

  if (!profile) return <p className="muted">Профиль не заполнен или не найден.</p>;

  if (isMine && edit) {
    return (
      <div className="card">
        <h2>Редактирование профиля</h2>
        <form onSubmit={save}>
          <div className="row">
            <div><label>Имя</label><input value={profile.firstName || ''} onChange={upd('firstName')} /></div>
            <div><label>Фамилия</label><input value={profile.lastName || ''} onChange={upd('lastName')} /></div>
          </div>
          <div className="row">
            <div><label>Страна</label><input value={profile.country || ''} onChange={upd('country')} /></div>
            <div><label>Город</label><input value={profile.city || ''} onChange={upd('city')} /></div>
          </div>
          <label>Навыки</label>
          <input value={profile.skills || ''} onChange={upd('skills')} />
          <label>О себе</label>
          <textarea value={profile.description || ''} onChange={upd('description')} />
          <label>Аватар</label>
          <input type="file" onChange={(e) => setFile(e.target.files[0])} />
          <div className="row">
            <button type="submit">Сохранить</button>
            <button type="button" className="secondary" onClick={() => setEdit(false)}>Отмена</button>
          </div>
        </form>
      </div>
    );
  }

  return (
    <div className="card">
      <h2>{profile.firstName} {profile.lastName}</h2>
      {profile.avatar && <img src={`/img/${profile.avatar}`} alt="" style={{ width: 120, borderRadius: 8 }} />}
      <p><b>Город:</b> {profile.city} {profile.country && `(${profile.country})`}</p>
      <p><b>Навыки:</b> {profile.skills}</p>
      <p>{profile.description}</p>
      {isMine && user && <button onClick={() => setEdit(true)}>Редактировать</button>}
    </div>
  );
}
