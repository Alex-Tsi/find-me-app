import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Vite — это сборщик и dev-сервер фронта (аналог Maven + локального Tomcat для статики).
// `npm run dev`  → поднимает сервер на http://localhost:5173 с hot-reload.
// `npm run build`→ кладёт готовую статику в dist/ (её потом раздаёт nginx).
export default defineConfig({
  plugins: [react()],

  // SockJS внутри использует переменную global (из мира Node), в браузере её нет —
  // подменяем на globalThis, иначе WebSocket-клиент падает.
  define: {
    global: 'globalThis',
  },

  server: {
    // host: true → слушаем и IPv4, и IPv6 (а не только ::1), чтобы localhost
    // открывался любым клиентом/браузером без сюрпризов.
    host: true,
    port: 5173,
    strictPort: true, // не «уезжать» на 5174, если 5173 занят, а честно упасть
    open: true,       // автоматически открыть браузер при `npm run dev`

    // DEV-PROXY: фронт всегда обращается к относительным путям (/api, /img, /messenger),
    // а Vite молча перенаправляет их на бэкенд :8080. Благодаря этому браузер видит
    // ОДИН origin (localhost:5173) → CORS не нужен, а httpOnly refresh-cookie спокойно ходит.
    // В проде ту же роль играет nginx.
    // ВАЖНО: цель — 127.0.0.1, а НЕ localhost. В Node 18+ `localhost` резолвится
    // сначала в IPv6 (::1), а Spring/Tomcat по умолчанию слушает IPv4 → прокси
    // упёрся бы в ECONNREFUSED и ВСЕ запросы к /api падали бы. 127.0.0.1 это чинит.
    proxy: {
      '/api': 'http://127.0.0.1:8080',
      '/img': 'http://127.0.0.1:8080',
      '/messenger': { target: 'http://127.0.0.1:8080', ws: true },
    },
  },
});
