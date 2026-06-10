import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { publicationsApi } from '../api/publications';
import { uploadApi } from '../api/upload';

// Одна страница и для создания (/publications/new), и для редактирования
// (/publications/:id/edit) — отличаются наличием :id.
export default function PublicationEditPage() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();

  const [form, setForm] = useState({
    title: '', description: '', tags: '', motivations: '', rewards: '', whoNeed: '', fileName: null,
  });
  const [file, setFile] = useState(null);
  const [saving, setSaving] = useState(false);

  // В режиме редактирования подгружаем текущие значения.
  useEffect(() => {
    if (isEdit) {
      publicationsApi.get(id).then((p) =>
        setForm({
          title: p.title || '', description: p.description || '', tags: p.tags || '',
          motivations: p.motivations || '', rewards: p.rewards || '', whoNeed: p.whoNeed || '',
          fileName: p.fileName || null,
        })
      );
    }
  }, [id, isEdit]);

  // Универсальный onChange для всех текстовых полей.
  const upd = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      let body = { ...form };
      // Если выбран новый файл — сначала загружаем его, получаем имя, кладём в body.
      if (file) {
        const { fileName } = await uploadApi.upload(file);
        body.fileName = fileName;
      }
      const saved = isEdit
        ? await publicationsApi.update(id, body)
        : await publicationsApi.create(body);
      navigate(`/publications/${saved.id}`);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <h2>{isEdit ? 'Редактирование' : 'Новая публикация'}</h2>
      <form onSubmit={handleSubmit}>
        <label>Заголовок *</label>
        <input value={form.title} onChange={upd('title')} required />
        <label>Теги (через пробел)</label>
        <input value={form.tags} onChange={upd('tags')} placeholder="java spring" />
        <label>Описание</label>
        <textarea value={form.description} onChange={upd('description')} />
        <label>Мотивация</label>
        <input value={form.motivations} onChange={upd('motivations')} />
        <label>Награда</label>
        <input value={form.rewards} onChange={upd('rewards')} />
        <label>Кто нужен</label>
        <input value={form.whoNeed} onChange={upd('whoNeed')} />
        <label>Картинка</label>
        <input type="file" onChange={(e) => setFile(e.target.files[0])} />
        {form.fileName && !file && <p className="muted">Текущий файл: {form.fileName}</p>}
        <button type="submit" disabled={saving}>{saving ? 'Сохранение…' : 'Сохранить'}</button>
      </form>
    </div>
  );
}
