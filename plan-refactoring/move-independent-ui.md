# План миграции: FreeMarker → React SPA (независимый UI-модуль) + Nginx

## Контекст

Проект Find Me использует FreeMarker-шаблоны (.ftlh), встроенные в Spring MVC.
Это тесный coupling: бизнес-логика и рендеринг находятся в одном процессе, невозможно
разрабатывать UI и API независимо. Цель — вынести UI в **независимый модуль**
(React SPA), который на время остаётся в этом же репозитории (монорепозиторий),
а Spring Boot превращается в чистый **REST + WebSocket API**.

> Ветка работы: `feature/move-ui-to-independent-module`.
> Подход — «big bang на ветке»: пока миграция не завершена, мерджить в `master` нельзя.

---

## 0. Изменения после ревизии плана (что исправлено и почему)

Этот раздел фиксирует расхождения исходного плана с фактическим кодом и принятые решения.
Проверено по состоянию репозитория на ветке `feature/move-ui-to-independent-module`.

| # | Было в плане | Факт в коде / проблема | Решение |
|---|---|---|---|
| 1 | Подразумевался `NoOpPasswordEncoder` (по CLAUDE.md) | В `WebSecurityConfigrer` и `RegistrationController` уже `BCryptPasswordEncoder` | JWT-логин строим поверх существующего бина `authenticationManager()` — пароли уже хешируются, ничего менять не нужно |
| 2 | Шаг 6.3: «удалить `spring-session-jdbc`» | Зависимости `spring-session-jdbc` в проекте **нет** (ни в pom, ни в конфигах) | Шаг удалён. CLAUDE.md в этой части устарел |
| 3 | Cleanup убирает только `spring-boot-starter-freemarker` | В `dependencies/pom.xml` также висят `spring-boot-starter-thymeleaf`, `thymeleaf-extras-springsecurity6` и webjars (`sockjs-client`, `stomp-websocket`, `bootstrap`, `jquery`, `webjars-locator-core`) | Cleanup расширен: убрать thymeleaf и webjars тоже |
| 4 | Раздел про WebSocket отсутствовал | Чат (`MessageController.convertAndSendToUser(recipientId, ...)`) идентифицирует пользователя; при stateless JWT это ломается | Добавлен раздел **3.6 — JWT-аутентификация WebSocket** (ChannelInterceptor + Principal) |
| 5 | `MvcConfigurer` не упоминался | Регистрирует view-контроллеры (`/login`, `/security/login`) и resource handlers (`/img/**`, `/static/**`) | View-контроллеры удалить; `/img/**` handler **сохранить** (его проксирует nginx). См. 3.5 |
| 6 | «Удалить все 15 @Controller» | Среди них нет `MessageController` (он `@RestController` + `@MessageMapping`) | Явно: `MessageController` **остаётся**; удаляются именно MVC-view-контроллеры |
| 7 | DTO в модуле `core` | `core` — доменное ядро; REST-DTO это деталь входящего адаптера | DTO размещаем в `ui-adapter` (пакет `ru.find.me.api.dto`), ядро не трогаем |
| 8 | CORS как обязательная сложная настройка | В проде nginx отдаёт фронт и API на одном origin (порт 80) — CORS не нужен | CORS включаем только для удобной разработки; рекомендуется **dev-proxy Vite** вместо CORS (см. раздел 4) |
| 9 | Смешаны `/api/` и `/api/v1/` | — | Стандартизируем на `/api/` без версии; версионирование вынесено в «опционально» |
| 10 | `@ControllerAdvice` | Для REST нужен JSON, а не view | Используем `@RestControllerAdvice` + `ProblemDetail` |
| 11 | Маппинг шаблонов неполный | Есть мусорные `publ/browsePublication.ftlh` и `security/user.ftlh`; статика `static/js/chat.js`, `static/css/chat.css`, `static/style.css` | Учтены в cleanup (раздел 7, шаг 6) |

---

## 1. Целевая архитектура

```
┌─────────────────────────────────────────────────────────────────┐
│  Браузер                                                        │
└─────────────────────────┬───────────────────────────────────────┘
                          │ HTTP / WebSocket  (всё на одном origin :80)
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  Nginx (localhost:80)                                           │
│                                                                 │
│  location /          → dist/ (React build, статика)            │
│  location /api/      → proxy → Spring Boot :8080               │
│  location /img/      → proxy → Spring Boot :8080               │
│  location /messenger → proxy → Spring Boot :8080 (WebSocket)   │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  Spring Boot (localhost:8080) — только API, без шаблонов        │
│                                                                 │
│  /api/auth/**         — аутентификация (JWT)                   │
│  /api/publications/** — публикации + комментарии              │
│  /api/profile/**      — профили                                │
│  /api/chat/**         — чат (история, список диалогов)         │
│  /api/upload          — загрузка файлов                        │
│  /img/**              — отдача загруженных изображений          │
│  /messenger           — WebSocket STOMP endpoint               │
└─────────────────────────┬───────────────────────────────────────┘
                          │ JDBC
                          ▼
                   PostgreSQL :5432
```

### Почему JWT, а не сессии?

Сессии привязывают фронтенд к серверу и требуют настройки cookie (SameSite/CORS).
JWT — stateless токен в заголовке `Authorization: Bearer <token>`, не требует
хранилища сессий, легко тестируется curl/Postman, стандарт для SPA.

> Замечание: `spring-session-jdbc` в проекте сейчас **не используется** (вопреки CLAUDE.md),
> так что «отключать сессии» не придётся. Достаточно сделать `SessionCreationPolicy.STATELESS`.

---

## 2. Структура фронтенда (React + Vite)

### Что такое Vite (для backend-разработчика)

Vite — аналог Maven для фронтенда: собирает проект, управляет зависимостями (через npm).
`npm run dev` = локальный сервер с hot reload (по умолчанию порт 5173).
`npm run build` = production-сборка в папку `dist/` (статические файлы: HTML, JS, CSS).
Nginx затем просто раздаёт эту папку как статику.

### Где живёт фронтенд

Каталог `frontend/` в корне репозитория — это и есть независимый UI-модуль.
Он **не** является Maven-модулем (сборка через npm, а не через Maven).
На время миграции остаётся в монорепозитории; позже его можно вынести в отдельный репозиторий
без изменений в backend.

> Опционально: чтобы `mvn clean install` собирал и фронт, можно подключить
> `frontend-maven-plugin` в отдельном profile. На текущем этапе — необязательно,
> билды фронта и бэка держим раздельно.

### Структура директорий

```
frontend/                        ← создаём командой: npm create vite@latest
├── public/
│   └── favicon.ico
├── src/
│   ├── main.jsx                 ← точка входа (аналог main() в Java)
│   ├── App.jsx                  ← корневой компонент с роутером
│   │
│   ├── api/                     ← весь HTTP-слой (аналог DAO/Repository)
│   │   ├── client.js            ← axios instance с baseURL и JWT-заголовком
│   │   ├── auth.js              ← login(), register()
│   │   ├── publications.js      ← getAll(), getById(), create(), update(), delete()
│   │   ├── profile.js           ← getProfile(), updateProfile()
│   │   ├── chat.js              ← getDialogs(), getRoomHistory()
│   │   └── upload.js            ← uploadFile()
│   │
│   ├── context/                 ← глобальное состояние (аналог scope=session бинов)
│   │   └── AuthContext.jsx      ← текущий пользователь, токен, login/logout
│   │
│   ├── hooks/
│   │   ├── useAuth.js           ← хук для доступа к AuthContext
│   │   └── useWebSocket.js      ← STOMP-соединение (с передачей JWT в CONNECT)
│   │
│   ├── components/
│   │   ├── Navbar.jsx           ← из navbar.ftlh
│   │   ├── PublicationCard.jsx  ← из publicationsList.ftlh
│   │   ├── PublicationForm.jsx  ← форма создания/редактирования
│   │   └── ProtectedRoute.jsx   ← редирект на /login, если нет токена
│   │
│   └── pages/                   ← страницы = бывшие .ftlh шаблоны
│       ├── Login.jsx            ← security/login.ftlh
│       ├── Register.jsx         ← security/registration.ftlh
│       ├── Home.jsx             ← greeting.ftlh
│       ├── Publications.jsx     ← publications/publications.ftlh
│       ├── PublicationDetail.jsx ← publications/browsePublication.ftlh
│       ├── PublicationEdit.jsx  ← publications/editPublication.ftlh
│       ├── UserPublications.jsx ← publications/userPublications.ftlh
│       ├── Profile.jsx          ← profile/profile.ftlh
│       ├── ProfileEdit.jsx      ← profile/edit.ftlh
│       └── Chat.jsx             ← chat/chat.ftlh (+ static/js/chat.js, static/css/chat.css)
│
├── .env                         ← VITE_API_BASE (см. раздел 4)
├── index.html
├── vite.config.js               ← dev-proxy на :8080 (см. раздел 4)
└── package.json
```

### React для backend-разработчика: ключевые концепции

**Компонент** — функция, возвращающая HTML (JSX). Аналог метода, рендерящего фрагмент страницы.

```jsx
function PublicationCard({ publication }) {
  return (
    <div className="card">
      <h5>{publication.title}</h5>
      <p>{publication.tags}</p>
    </div>
  );
}
```

**useState** — локальное состояние компонента. Аналог поля класса, при изменении которого идёт перерисовка.

```jsx
const [publications, setPublications] = useState([]);
```

**useEffect** — код при монтировании компонента (= при открытии страницы). Аналог `@PostConstruct`.

```jsx
useEffect(() => {
  fetchPublications().then(setPublications);
}, []); // [] = один раз при загрузке
```

**Context** — глобальное хранилище (аналог сессии). Храним JWT и данные текущего пользователя.

---

## 3. Backend: что нужно изменить

### 3.1 Новые зависимости (dependencies/pom.xml)

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<!-- Валидация тел запросов (@Valid) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### 3.2 Новые классы

> **Важно про размещение (направление зависимостей):** `assembly → ui-adapter → service-adapter → core`.
> Поэтому JWT-инфраструктуру, которую использует `AuthApiController` (он в `ui-adapter`),
> нельзя класть в `assembly` — она размещается в **`ui-adapter`** (`ru.find.me.api.security`).
> Это и архитектурно корректно: JWT — security входящего веб-адаптера.

```
adapters/ui-adapter/src/main/java/ru/find/me/api/
├── security/
│   ├── JwtTokenProvider.java          ← генерация и валидация токенов (HS256)
│   ├── JwtAuthFilter.java             ← OncePerRequestFilter (Bearer из заголовка)
│   └── WebSocketAuthInterceptor.java  ← ChannelInterceptor: JWT в STOMP CONNECT (см. 3.6)
└── ... (контроллеры и dto — см. 3.3, 3.4)

assembly/src/main/java/ru/find/me/configuration/
├── WebSecurityConfigrer.java         ← изменить: STATELESS, csrf off, CORS, JWT filter, убрать formLogin
├── CorsConfig.java                   ← новый: CorsConfigurationSource (только для dev)
└── chat/
    └── WebSocketConfigurer.java      ← изменить: подключить interceptor в configureClientInboundChannel
```

> `authenticationManager()` уже определён и использует `BCryptPasswordEncoder` —
> переиспользуем его в `AuthApiController`, ничего в нём менять не нужно.
> `WebSecurityConfigrer` импортирует `JwtAuthFilter` из `ui-adapter` (assembly от него зависит).

### 3.3 Новые REST-контроллеры (ui-adapter модуль)

Новый пакет: `ru.find.me.api`

| Файл | Эндпоинты |
|---|---|
| `AuthApiController.java` | `POST /api/auth/login`, `/register`, `/refresh`, `/logout` |
| `ProfileApiController.java` | `GET /api/profile/me`, `GET /api/profile/{userId}`, `PUT /api/profile/me` |
| `PublicationApiController.java` | `GET /api/publications` (+ `/{id}`, `/filter?tags=`, `/user/{userId}`); `POST`, `PUT /{id}`, `DELETE /{id}` (проверка владельца) |
| `CommentApiController.java` | `POST /api/publications/{publicationId}/comments` |
| `ChatApiController.java` | `GET /api/chat/dialogs`, `GET /api/chat/rooms/{roomId}/messages` |
| `UploadApiController.java` | `POST /api/upload` (multipart `file`) → `{fileName}` |

> **Статус: Шаг 2 выполнен** — `mvn compile` зелёная. Реализованы DTO (`api.dto`), ручной
> маппер `ApiMapper`, контроллеры выше и `ApiExceptionHandler` (`@RestControllerAdvice` → `ProblemDetail`).
> Просмотр публикаций публичный (GET permitAll), запись/комментарии/профиль/чат — под аутентификацией.
>
> Отклонения от наброска: профиль редактируется только свой (`PUT /me`); комментарии — отдельный
> контроллер с вложенным путём. Типизированные 404: добавлено доменное `NotFoundException` (`ports`),
> сервисы (`findById` публикации/профиля/комнаты/пользователя) кидают его вместо «голого»
> `RuntimeException`, в `ApiExceptionHandler` оно маппится в 404 ProblemDetail.
>
> `AuthApiController.register` повторяет логику `RegistrationController`:
> `findByUsername`, `passwordEncoder.encode(...)`, `setActive(true)`,
> `setRoles(Set.of(Role.USER))`, `setProfile(new Profile())`, `userService.save(...)`.

### 3.4 DTO (ui-adapter модуль)

Новый пакет: `ru.find.me.api.dto` (в адаптере, не в `core` — REST-DTO это деталь адаптера).
Маппинг сущность↔DTO — вручную или через MapStruct/отдельный mapper-класс.
**Сущности наружу не отдаём** (у `User`/`Publication` двунаправленные EAGER-связи →
бесконечная рекурсия и гигантские payload'ы при прямой сериализации).

```java
record AuthRequest(String username, String password) {}
record RegisterRequest(String username, String password) {}
record AuthResponse(String token, Long userId, String username) {}
record PublicationDto(Long id, String title, String tags, String description,
                      String fileName, LocalDate date, UserDto author) {}
record UserDto(Long id, String username) {}
record ProfileDto(Long id, String firstName, String lastName, String avatar,
                  String skills, String country, String city, String description) {}
record CommentDto(Long id, String text, UserDto user) {}
record MessageDto(Long senderId, Long recipientId, String content) {}
```

### 3.5 Изменения в существующих конфигах

**`WebSecurityConfigrer.java`**
- `sessionManagement(s -> s.sessionCreationPolicy(STATELESS))`
- `csrf(csrf -> csrf.disable())` (для stateless API с токеном в заголовке)
- `cors(...)` — подключить `CorsConfigurationSource` (см. раздел 4)
- убрать `formLogin(...)` и `logout(...)`
- `permitAll` для `/api/auth/**`, `/img/**`, `/messenger/**`; остальное `authenticated()`
- добавить `JwtAuthFilter` перед `UsernamePasswordAuthenticationFilter`
- бины `passwordEncoder()` и `authenticationManager()` оставить как есть

**`MvcConfigurer.java`**
- удалить `addViewControllers(...)` (view-контроллеры `/login`, `/security/login` больше не нужны)
- resource handler `/img/**` → **оставить** (nginx проксирует `/img/` сюда, файлы отдаёт backend)
- resource handler `/static/**` → удалить (статика уезжает во фронтенд)

### 3.6 JWT-аутентификация WebSocket — РЕАЛИЗОВАНО

При stateless-JWT у WebSocket-сессии нет аутентификации «из коробки». Сделано:

1. **`WebSocketAuthInterceptor implements ChannelInterceptor`** (`ui-adapter`, `api.security`):
   на команде `CONNECT` читает нативный заголовок `Authorization: Bearer ...`, валидирует
   через `JwtTokenProvider`, грузит пользователя и кладёт `Principal` в `accessor.setUser(...)`.
   **Нет/неверный токен на CONNECT → `MessagingException`** (соединение отклоняется).
   После CONNECT `Principal` закреплён за сессией и доступен во всех кадрах.
2. Подключён в `WebSocketConfigurer.configureClientInboundChannel(...)`.
3. SockJS-handshake (`/messenger`) остаётся `permitAll` в `WebSecurityConfigrer` — это обычный
   HTTP без токена; аутентификация происходит уже на уровне STOMP CONNECT.
4. **`MessageController.sendMsg(Message, Principal)`** переписан прод-лайк:
   - отправитель берётся из `principal.getName()` (**не** из тела — иначе спуфинг отправителя);
   - доставка `convertAndSendToUser(recipient.getUsername(), "/messages", msg)` — по username,
     а не по сырому `recipientId` (Principal теперь = username);
   - метод стал `void` (раньше возвращал `Message` в несуществующий `/topic`).
5. На фронте: `stompClient.connectHeaders = { Authorization: 'Bearer ' + accessToken }`,
   подписка на `/user/messages`.

> Замечание: при логине Principal-имя = username, поэтому и доставка, и подписки завязаны
> на username. Альтернатива (токен в query SockJS `/messenger?token=`) отклонена —
> токен попадает в логи.

### 3.7 Refresh-токены (access + refresh, прод-лайк) — РЕАЛИЗОВАНО

Вместо одного долгого токена — пара: короткий **access** (stateless JWT, 15 мин) и
длинный **refresh** (7 дней, серверный, ротируемый). Это стандарт OAuth 2.0.

**Модель хранения (`core`):** сущность `RefreshToken` (`refresh_tokens`): `tokenHash`
(SHA-256 от сырого токена — в БД сырой не хранится), `userId`, `expiresAt`, `createdAt`,
`revoked`. Репозиторий `RefreshTokenRepo`.

**Сервис (`ports` → `service-adapter`):** `RefreshTokenService` / `RefreshTokenServiceImpl`:
- `issue(userId)` — генерит 256-битный случайный токен (`SecureRandom`), сохраняет его хеш, возвращает сырой;
- `rotate(rawToken)` — проверяет и **ротирует**: старый помечает `revoked`, выпускает новый;
  при предъявлении уже отозванного токена считает это кражей и **отзывает все токены пользователя** (reuse detection).
  ⚠️ Метод помечен `@Transactional(noRollbackFor = InvalidRefreshTokenException.class)`: в ветке reuse мы
  сначала отзываем все токены, потом кидаем исключение — без `noRollbackFor` бросок откатил бы и отзыв
  (баг, найденный интеграционным тестом);
- `revoke(rawToken)` — отзыв (logout);
- `@Scheduled` ежедневная очистка просроченных (`@EnableScheduling` на `FindMeApp`).

**Доставка (`ui-adapter`, `AuthApiController`):**
- access-токен — в теле ответа (`AuthResponse.accessToken`), клиент шлёт в `Authorization: Bearer`;
- refresh-токен — в **httpOnly Secure SameSite=Lax cookie** с `path=/api/auth` (JS его не читает → защита от XSS; уходит только на refresh/logout);
- эндпоинты: `POST /api/auth/login`, `/register`, `/refresh` (читает cookie, ротирует), `/logout` (отзывает + чистит cookie).

**Конфиг (`application.yaml`):** `jwt.secret` (через `${JWT_SECRET:...}`), `jwt.access-expiration-ms=900000`,
`jwt.refresh-expiration-ms=604800000`, `app.cookie.secure` (false для локального http, true в проде).

> Замечание про cookie: схема рассчитана на same-origin (dev — через Vite-proxy, prod — через nginx).
> Если фронт ходит на `:8080` напрямую, для отправки cookie понадобится CORS с `allowCredentials=true`
> и конкретным origin (см. раздел 4).

---

## 4. CORS и dev-режим

В **production** nginx отдаёт и фронт, и API на одном origin (`http://localhost:80`),
поэтому **CORS не нужен вообще** — браузер видит один источник.

CORS актуален только в **разработке**, когда React крутится на Vite (`:5173`),
а backend на `:8080`. Есть два варианта:

**Вариант A (рекомендуется) — dev-proxy Vite, без CORS:**
Vite сам проксирует `/api`, `/img`, `/messenger` на `:8080`, фронт всегда общается «сам с собой».

```js
// vite.config.js
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api':       'http://localhost:8080',
      '/img':       'http://localhost:8080',
      '/messenger': { target: 'http://localhost:8080', ws: true },
    },
  },
});
```
В этом случае `VITE_API_BASE` пустой, axios `baseURL: '/api'`.

**Вариант B — включить CORS на backend** (если фронт ходит напрямую на `:8080`):

```java
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(false); // JWT в заголовке, cookie не нужны
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

> Вариант A проще и ближе к проду (тот же relative-путь `/api`). Вариант B оставлен как запасной.

---

## 5. Nginx-конфигурация

Файл: `nginx/nginx.conf` в корне проекта.

```nginx
events {}

http {
    include mime.types;

    server {
        listen 80;

        # SPA: всё, что не файл — отдаём index.html (роутинг на стороне React)
        location / {
            root   /путь/к/frontend/dist;
            try_files $uri $uri/ /index.html;
        }

        # API на Spring Boot
        location /api/ {
            proxy_pass http://localhost:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }

        # Загруженные изображения (отдаёт backend через resource handler /img/**)
        location /img/ {
            proxy_pass http://localhost:8080;
        }

        # WebSocket (STOMP/SockJS) — нужны заголовки upgrade
        location /messenger {
            proxy_pass http://localhost:8080;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
        }
    }
}
```

---

## 6. Best Practices

### Backend
- **Не возвращать доменные сущности** — всегда DTO (у сущностей EAGER-связи → рекурсия).
- **`@Valid` на теле запроса** — валидировать входящие данные (jakarta.validation).
- **`@RestControllerAdvice`** — централизованная обработка исключений, возвращать `ProblemDetail` (RFC 7807).
- **STATELESS + JWT** — никаких сессий; токен в `Authorization: Bearer`.
- **Версионирование API** — *опционально*; если вводить `/api/v1/`, то единообразно во всех контроллерах.

### Frontend
- **Все запросы только через `src/api/`** — никогда `fetch`/`axios` напрямую в компонентах.
- **JWT в localStorage** (cookie потребовал бы CORS с credentials).
- **`ProtectedRoute`** — единая обёртка для приватных страниц.
- **Loading state** — placeholder, пока грузятся данные.
- **Базовый путь API в `.env`** (`VITE_API_BASE`), не хардкодить.

---

## 7. Порядок реализации (пошагово)

> Важно: после Шага 1 (STATELESS + JWT, без formLogin) **старый FreeMarker-UI перестанет
> работать** — это ожидаемо. Поэтому работаем строго на ветке и не мерджим в `master`,
> пока React не достигнет паритета и не выполнен cleanup (Шаг 6).

### Шаг 1 — Backend: JWT + Security ✅ ВЫПОЛНЕНО (+ refresh-токены, см. 3.7)
1. Добавить `jjwt` и `spring-boot-starter-validation` в `dependencies/pom.xml`
2. `JwtTokenProvider.java` (генерация/валидация токена)
3. `JwtAuthFilter.java` (OncePerRequestFilter, читает Bearer)
4. Обновить `WebSecurityConfigrer.java` (STATELESS, csrf off, cors, JWT filter, убрать formLogin)
5. `AuthApiController.java` (login через `authenticationManager`; register — логика из `RegistrationController`)
6. Проверка: `curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"...","password":"..."}'`

### Шаг 2 — Backend: REST API ✅ ВЫПОЛНЕНО
1. DTO-классы и mapper в `ui-adapter` (`ru.find.me.api.dto`)
2. `PublicationApiController` (самый важный) + фильтр по тегам
3. `ProfileApiController`
4. `CommentApiController`
5. `ChatApiController` (история диалогов/комнат)
6. `UploadApiController`
7. `@RestControllerAdvice` с `ProblemDetail`

### Шаг 3 — Backend: WebSocket по JWT ✅ ВЫПОЛНЕНО
1. `WebSocketAuthInterceptor` (ChannelInterceptor, CONNECT → Principal)
2. Подключить в `WebSocketConfigurer.configureClientInboundChannel`
3. Убедиться, что `MessageController` отправляет по authenticated Principal

### Шаг 4 — Frontend: инициализация ✅ ВЫПОЛНЕНО (код написан вручную)
1. Модуль `frontend/` создан вручную (Node в системе отсутствует, поэтому без `npm create`).
2. `package.json` с react, react-router-dom, axios, @stomp/stompjs, sockjs-client + vite.
3. Настроены `vite.config.js` (dev-proxy + `global` polyfill), `src/api/client.js`
   (Bearer + авто-refresh, single-flight), `src/auth/AuthContext.jsx`, `src/auth/ProtectedRoute.jsx`.

### Шаг 5 — Frontend: страницы ✅ ВЫПОЛНЕНО
Реализованы: Login, Register, Publications (список+фильтр), PublicationDetail (+комментарии,
+удаление владельцем), PublicationEdit (создание/правка + загрузка файла), Profile (свой/чужой),
Chat (диалоги + история REST + live по STOMP). Архитектура и пояснения — в `frontend/README.md`.

> ⚠️ Сборка НЕ проверена: в системе нет Node.js. Перед запуском: поставить Node 18+,
> `cd frontend && npm install && npm run dev` (бэкенд на :8080). Возможны мелкие правки
> версий/импортов, которые всплывут только при первом `npm install`.

### Шаг 6 — Nginx
1. Установить Nginx (Windows: скачать с nginx.org, распаковать)
2. `npm run build` в `frontend/` → `dist/`
3. Прописать путь к `dist/` в `nginx/nginx.conf`, запустить

### Шаг 7 — Cleanup ✅ ВЫПОЛНЕНО
1. ✅ Удалены 15 MVC-view-контроллеров: пакеты `chat` (`ChatController`, `SelectChatRoomController`),
   `profiles` (3), `publications` (8), `security` (`GreetingController`, `RegistrationController`).
   **`MessageController` оставлен** (`@RestController` + `@MessageMapping`).
2. ✅ Удалены все 18 `.ftlh` (вся папка `templates/`, включая мусорные `publ/browsePublication.ftlh`,
   `security/user.ftlh`) и статика (`static/js/chat.js`, `static/css/chat.css`, `static/style.css`).
   > Остался неиспользуемый `resources/noimage.jpeg` (нигде не референсится) — оставлен как безвредный.
3. ✅ Из `dependencies/pom.xml` убраны `spring-boot-starter-freemarker`,
   `spring-boot-starter-thymeleaf`, `thymeleaf-extras-springsecurity6`,
   webjars (`sockjs-client`, `stomp-websocket`, `bootstrap`, `jquery`, `webjars-locator-core`).
4. ✅ Из `ui-adapter/pom.xml` убраны `guava` и `org.jetbrains:annotations` (проверено: нигде в адаптерах не используются).
5. ✅ `MvcConfigurer`: удалены `addViewControllers(...)` и resource handler `/static/**`; `/img/**` оставлен.
6. ✅ CLAUDE.md обновлён (миграция, состав `ui-adapter`, чат по JWT, refresh-токены; раздел про FreeMarker-шаблоны удалён).

> Проверка: `mvn clean install -DskipTests` → BUILD SUCCESS; `npm run build` → ok.
> `spring-session-jdbc` удалять не пришлось — его в проекте нет.

> **Осталось только Шаг 6 (Nginx)** — это деплой-шаг (поставить nginx, прописать путь к `dist/`).
> Backend-миграция завершена: FreeMarker полностью удалён, UI независим.

---

## 8. Начинаем с Шага 1

Первые файлы для реализации:
- `dependencies/pom.xml` — добавить `jjwt` + `spring-boot-starter-validation`
- `adapters/ui-adapter/.../api/security/JwtTokenProvider.java` — новый
- `adapters/ui-adapter/.../api/security/JwtAuthFilter.java` — новый
- `assembly/.../configuration/WebSecurityConfigrer.java` — изменить
- `assembly/.../configuration/CorsConfig.java` — новый
- `adapters/ui-adapter/.../api/AuthApiController.java` — новый
- `adapters/ui-adapter/.../api/dto/{AuthRequest,RegisterRequest,AuthResponse}.java` — новые DTO

> **Статус: Шаг 1 выполнен (включая refresh-токены, см. 3.7)** — сборка `mvn compile` зелёная.
> API stateless+JWT, access (15 мин) + refresh (7 дней, ротация + reuse detection, httpOnly cookie).
> Старый FreeMarker-UI с form-login больше не работает (ожидаемо). Дальше — Шаг 2 (REST API) и Шаг 3 (WS).
