# user-e2e — ручные HTTP-запросы пользователя

Сюда складываются ручные запросы к API Find Me (`http://localhost:8080/api/**`):
файлы `.http`/`.rest` (REST Client), curl-скрипты, Postman-экспорты и т.п.

Предусловие — поднятый backend (см. скилл `manual-testing`).

Быстрый старт (логин → токен → защищённый запрос):

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

curl -s http://localhost:8080/api/chat/dialogs -H "Authorization: Bearer $TOKEN"
```

Добавляя новый запрос, впиши строку в таблицу «Справочник ручных запросов» в `../README.md`.
