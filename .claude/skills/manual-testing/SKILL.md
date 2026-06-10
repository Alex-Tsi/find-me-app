---
name: manual-testing
description: >
  Ручное/функциональное тестирование Find Me через UI или REST. Используй, когда
  пользователь просит «протестировать функционал/чат/публикации/UI», прогнать e2e
  или проверить фичу вживую. Скилл поднимает окружение (Docker Postgres → backend
  → frontend), проверяет готовность и запускает нужный тест. Справочник конкретных
  тестов — НЕ здесь, а в `e2e/README.md` (ссылаться туда).
---

# Ручное / функциональное тестирование (Find Me)

Порядок строгий: **Docker Postgres → backend → frontend → проверка готовности → тест**.
Перечень тестов и что каждый проверяет — в `e2e/README.md`.

## Шаг 1. Docker Postgres

Контейнер `find-me-pg` может уже существовать или быть запущен — проверь и переиспользуй
(`docker start find-me-pg`), а не создавай заново.

Если контейнера нет — проверить образ и при необходимости скачать (чистая система,
где только Docker Desktop), затем создать:

```bash
docker images --format "{{.Repository}}:{{.Tag}}" | grep -x "postgres:16" \
  || docker pull postgres:16          # нет образа → скачать
docker run -d --name find-me-pg \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -e POSTGRES_DB=postgres \
  -p 5432:5432 \
  postgres:16
```

Готовность: `docker exec find-me-pg pg_isready -U postgres` → "accepting connections".

> Если `docker` не отвечает — не запущен Docker Desktop; попросить пользователя запустить его
> (движок на Windows поднимается через Docker Desktop, не через WSL вручную).

## Шаг 2. Backend + Frontend

Если порт уже слушает — пропустить запуск. Поднять в фоне:

```bash
# backend (:8080), из корня репозитория
nohup mvn spring-boot:run -pl assembly > backend.log 2>&1 &
# frontend (:5173)
cd frontend && nohup npm run dev > ../frontend.log 2>&1 &
```

Готовность (бэк стартует ~5–15 с, ждать до ~60 с):
- backend: `Started FindMeApp` в `backend.log` (ошибка — `APPLICATION FAILED`)
- frontend: `ready in` в `frontend.log`
- порты `:8080` и `:5173` слушают

## Шаг 3. Тестовые пользователи

Нужны `alice` и `bob` (пароль `password123`). Регистрировать через
`POST /api/auth/register` (`{"username":"alice","password":"password123"}`);
если уже существуют — ошибку игнорировать.

## Шаг 4. Прогон теста

Установка зависимостей (один раз): `cd e2e && PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 npm install`.

```bash
cd e2e
npm run test:chat-stomp   # быстрый STOMP
npm run test:chat         # UI чат
npm run test:e2e          # всё
```

Полный список — в `e2e/README.md`. REST-проверки — напрямую curl'ом на `:8080`
(заготовки — в `e2e/user-e2e/`).

**Новый тест, которого нет в наборе:** положить в `e2e/claude-e2e/` (авто) или
`e2e/user-e2e/` (ручной HTTP), добавить строку в `e2e/README.md` — и только потом гонять.