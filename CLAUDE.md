# CLAUDE.md

## Сборка и запуск

```bash
mvn clean install                  # сборка всего проекта
mvn spring-boot:run -pl assembly   # запуск приложения
```

Конфигурация — `assembly/src/main/resources/application.yaml` (в репозитории): PostgreSQL на
`localhost:5432`, БД/пользователь `postgres`, пароль `mysecretpassword`; `upload.path` — директория
для загружаемых файлов. Postgres поднимается в Docker (контейнер `find-me-pg`, образ `postgres:16`) —
запуск окружения описан в скилле `manual-testing`.

### Фронтенд (`frontend/`, Vite + React, Node 18+)

```bash
cd frontend
npm install      # один раз
npm run dev      # dev-сервер http://localhost:5173
npm run build    # прод-сборка в dist/
```

Vite проксирует `/api`, `/img`, `/messenger` на бэкенд `:8080` — CORS не нужен. Детали фронта — в `frontend/README.md`.

## Архитектура

Гексагональная (Ports & Adapters), Maven-модули: `core` (домен + JPA-репозитории), `ports` (интерфейсы сервисов), `adapters/service-adapter` (реализации сервисов), `adapters/ui-adapter` (REST API `ru.find.me.api`, WebSocket-чат, JWT-security, DTO), `assembly` (точка входа `FindMeApp` и конфигурации), `dependencies` (родительский POM).

Backend — чистый stateless REST + WebSocket API; UI — независимый React-фронтенд в `frontend/`.

## Гочи и соглашения

- Аутентификация: access JWT (15 мин, `Authorization: Bearer`) + ротируемый refresh-токен в httpOnly-cookie (`path=/api/auth`); хранится только SHA-256-хеш, reuse detection отзывает все токены пользователя. Эндпоинты: `POST /api/auth/{login,register,refresh,logout}`.
- WebSocket-чат: STOMP/SockJS на `/messenger`, JWT передаётся в кадре `CONNECT` (`WebSocketAuthInterceptor`). Отправитель берётся из `Principal`, а не из тела сообщения (защита от спуфинга).
