import { api } from './client';

export const profileApi = {
  me: () => api.get('/api/profile/me').then((r) => r.data),
  get: (userId) => api.get(`/api/profile/${userId}`).then((r) => r.data),
  update: (body) => api.put('/api/profile/me', body).then((r) => r.data),
};
