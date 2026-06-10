import { api } from './client';

// История чата по REST (диалоги + сообщения комнаты).
// Live-доставка новых сообщений идёт отдельно по WebSocket (см. ws/useChat.js).
export const chatApi = {
  dialogs: () => api.get('/api/chat/dialogs').then((r) => r.data),
  messages: (roomId) => api.get(`/api/chat/rooms/${roomId}/messages`).then((r) => r.data),
  // Все пользователи (кроме себя) — для старта нового диалога.
  users: () => api.get('/api/chat/users').then((r) => r.data),
};
