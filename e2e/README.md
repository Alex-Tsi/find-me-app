# E2E / функциональное тестирование Find Me

Справочник тестов и точка входа для ручного/функционального тестирования (UI + REST).
Запуск тестов оркестрирует скилл **`manual-testing`** (`.claude/skills/manual-testing/`):
он поднимает окружение (Docker Postgres → backend → frontend), затем гоняет нужный тест.

## Структура

```
e2e/
├── package.json        ← зависимости авто-тестов (playwright, @stomp/stompjs, sockjs-client)
├── claude-e2e/         ← АВТО-тесты (пишет Claude): Playwright UI + STOMP
│   └── screenshots/    ← артефакты прогонов (в git не коммитятся)
└── user-e2e/           ← РУЧНЫЕ HTTP-запросы пользователя (curl/.http/.rest)
```

## Установка (один раз)

```bash
cd e2e
# системный Chrome используется через channel:'chrome', бандл-браузеры качать не нужно:
PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 npm install
```

## Предусловия запуска

Backend `:8080` и frontend `:5173` должны быть подняты (этим занимается скилл `manual-testing`).
Нужны тестовые пользователи `alice`/`bob` (пароль `password123`).

## Запуск

```bash
cd e2e
npm run test:chat-stomp   # быстрый STOMP-тест доставки (без браузера)
npm run test:chat         # полный UI-тест чата
npm run test:publications # публикации/логин/комментарии
npm run test:e2e          # все по очереди
```

Переменные окружения (необязательно): `E2E_BASE` (база фронта, по умолчанию `http://localhost:5173`),
`E2E_HEADED=1` (показать окно браузера — для `publications.mjs`).

---

## Справочник авто-тестов (claude-e2e)

| Тест | npm-скрипт | Что проверяет | Тип |
|---|---|---|---|
| `claude-e2e/chat-stomp.mjs` | `test:chat-stomp` | Live-доставка чата по WebSocket (STOMP): alice→bob, проверка получения | STOMP, без браузера |
| `claude-e2e/chat.mjs` | `test:chat` | Чат через UI: 2 браузерных контекста (alice/bob), двусторонний обмен, скриншоты | Playwright (Chrome) |
| `claude-e2e/publications.mjs` | `test:publications` | Логин, создание публикации, комментарий, список — через UI | Playwright (Chrome) |

> **Добавление нового теста:** любой запрошенный тест, которого здесь нет, кладётся в
> `claude-e2e/` (авто) или `user-e2e/` (ручной HTTP) и **обязательно** получает строку в
> соответствующей таблице этого файла. Скилл `manual-testing` ссылается сюда, а не дублирует список.

## Справочник ручных запросов (user-e2e)

| Файл | Что делает |
|---|---|
| _(пока пусто)_ | Сюда пользователь складывает свои ручные HTTP-запросы (curl/.http) |
