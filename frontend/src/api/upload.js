import { api } from './client';

// Загрузка файла multipart/form-data. Бэкенд вернёт { fileName },
// которое потом кладём в publication.fileName / profile.avatar.
export const uploadApi = {
  upload: (file) => {
    const form = new FormData();
    form.append('file', file);
    return api.post('/api/upload', form).then((r) => r.data);
  },
};
