import { api } from './client';

// Слой доступа к публикациям — аналог Repository/Feign-клиента.
export const publicationsApi = {
  list: () => api.get('/api/publications').then((r) => r.data),
  get: (id) => api.get(`/api/publications/${id}`).then((r) => r.data),
  byUser: (userId) => api.get(`/api/publications/user/${userId}`).then((r) => r.data),
  filter: (tags) => api.get('/api/publications/filter', { params: { tags } }).then((r) => r.data),

  create: (body) => api.post('/api/publications', body).then((r) => r.data),
  update: (id, body) => api.put(`/api/publications/${id}`, body).then((r) => r.data),
  remove: (id) => api.delete(`/api/publications/${id}`).then((r) => r.data),

  addComment: (id, text) =>
    api.post(`/api/publications/${id}/comments`, { text }).then((r) => r.data),
};
