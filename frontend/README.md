# Find Me — фронтенд (React SPA)

Эта папка — **независимый UI-модуль**. Он не знает про Java/Maven, общается с бэкендом
только по HTTP (`/api/**`) и WebSocket (`/messenger`). Собирается через npm, не через Maven.

> Документ написан под backend-разработчика: ниже всё объясняется через привычные аналогии.

---

## 1. Как запустить

Нужен **Node.js 18+** (его сейчас в системе нет — поставь с https://nodejs.org или `winget install OpenJS.NodeJS.LTS`).

```bash
cd frontend
npm install        # скачать зависимости (аналог mvn install для зависимостей)
npm run dev        # dev-сервер с hot-reload → http://localhost:5173
```

Бэкенд при этом должен быть поднят на `:8080` (`mvn spring-boot:run -pl assembly`) и Postgres в Docker.
Vite сам проксирует `/api`, `/img`, `/messenger` на `:8080` — поэтому браузер видит один origin и **CORS не нужен**.

Продакшен-сборка: `npm run build` → статика в `dist/`, её раздаёт nginx (см. корневой план, раздел 5).

---

## 2. Аналогии «фронт ↔ бэкенд»

| Фронтенд | Что это | Бэкенд-аналог |
|---|---|---|
| `src/api/*.js` | слой HTTP-вызовов | Repository / Feign-клиент |
| `src/api/client.js` | настроенный axios + интерсепторы | сконфигурированный `RestTemplate`/`WebClient` + фильтры |
| request-интерсептор | дописывает `Authorization` | сервлет-фильтр, добавляющий заголовок |
| response-интерсептор | ловит 401 → refresh → повтор | retry/refresh-обвязка клиента |
| `AuthContext` | глобальное состояние входа | session-scoped бин / `SecurityContextHolder` |
| `ProtectedRoute` | пускает только залогиненных | security-фильтр `authenticated()` |
| `App.jsx` (`<Routes>`) | URL → компонент | `@RequestMapping` (но в браузере) |
| страница в `pages/` | экран = бывший `.ftlh` | `@Controller` + view |
| `useState` | поле с авто-перерисовкой | поле + ручной refresh UI |
| `useEffect(fn, [])` | код при открытии экрана | `@PostConstruct` / init |
| `useChat` (hook) | переиспользуемая логика сокета | утильный `@Service`-метод |

---

## 3. Структура

```
src/
├── main.jsx            точка входа: монтирует Router + AuthProvider + App
├── App.jsx             карта маршрутов (какой URL → какая страница)
├── index.css           минимальные стили
│
├── api/                ВЕСЬ HTTP здесь (компоненты сеть напрямую не дёргают)
│   ├── client.js       axios + Bearer + авто-refresh на 401  ← самое важное
│   ├── auth.js         login/register/refresh/logout
│   ├── publications.js, profile.js, chat.js, upload.js
│
├── auth/
│   ├── AuthContext.jsx состояние пользователя + login/logout, тихий refresh при старте
│   └── ProtectedRoute.jsx  редирект на /login, если не вошёл
│
├── ws/
│   └── useChat.js      STOMP/SockJS: JWT на CONNECT, подписка на /user/messages
│
├── components/         переиспользуемый UI (Navbar, Layout)
└── pages/              экраны (Login, Register, Publications, Detail, Edit, Profile, Chat)
```

**Правило:** новый запрос к API добавляется только в `src/api/*`, а не внутри компонента.
Так HTTP-слой остаётся единственным местом правды (как Repository).

---

## 4. Как работает аутентификация (ключевое)

Два токена (см. бэкенд, раздел 3.7 плана):

- **access** — короткий JWT (15 мин), хранится **в памяти JS** (`client.js`), шлётся в `Authorization: Bearer`.
- **refresh** — длинный (7 дней), в **httpOnly cookie** (JS его не видит → защита от XSS), уходит только на `/api/auth/*`.

### Вход
```
LoginPage → AuthContext.login() → POST /api/auth/login
   ← { accessToken } в теле + Set-Cookie: refreshToken (httpOnly)
   accessToken кладём в память, user — в состояние.
```

### Каждый запрос
```
request-интерсептор добавляет  Authorization: Bearer <access>
```

### Протух access (401) — бесшовно
```
response-интерсептор ловит 401
   → POST /api/auth/refresh (refresh-cookie уходит сама)
   → новый access в память → ПОВТОР исходного запроса
(если несколько 401 разом — refresh делается один раз, «single-flight»)
```

### Перезагрузка страницы (F5)
```
access в памяти потерян → AuthContext при старте делает «тихий» refresh по cookie
   → успех: пользователь снова внутри; провал: аноним → /login
```
Поэтому **ребут вкладки/сервера не разлогинивает** — refresh-cookie жива, секрет JWT фиксирован.

### Выход / кража токена
- `logout` → `POST /api/auth/logout` отзывает refresh на сервере + чистит cookie.
- refresh **ротируется** при каждом обновлении; предъявление старого = сигнал кражи → сервер отзывает все токены пользователя.

---

## 5. Чат (WebSocket)

- История — обычным REST: `GET /api/chat/dialogs`, `GET /api/chat/rooms/{id}/messages`.
- Live — STOMP поверх SockJS (`useChat.js`):
  - JWT идёт в STOMP-кадре **CONNECT** (handshake заголовок принять не может);
  - подписка на `/user/messages` — личная очередь (Spring доставляет по `Principal`);
  - отправка в `/app/send`; `senderId` сервер ставит сам из `Principal` (клиенту не доверяем).

---

## 6. Известные упрощения (TODO)

- Старт нового диалога «с нуля» (написать тому, с кем ещё нет комнаты) пока не сделан —
  показываются только существующие диалоги. Можно добавить кнопку «Написать» на профиле.
- Нет глобального тост-уведомления об ошибках — ошибки показываются точечно на формах.
- Стили минимальные (без UI-кита).
