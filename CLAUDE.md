# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run

```bash
# Сборка всего проекта
mvn clean install

# Запуск приложения (из модуля assembly)
mvn spring-boot:run -pl assembly
```

Перед запуском необходим файл `assembly/src/main/resources/application.properties` (не хранится в репозитории) с настройками:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/find_me
spring.datasource.username=...
spring.datasource.password=...
upload.path=/path/to/uploads
```

База данных — PostgreSQL. Сессии хранятся в БД через `spring-session-jdbc` (требуется инициализация схемы Spring Session).

## Архитектура

Проект реализует **гексагональную архитектуру** (Ports & Adapters) и разбит на Maven-модули:

| Модуль | Назначение |
|---|---|
| `core` | Доменные модели (`User`, `Publication`, `Comment`, `Profile`, `Role`, `chat/Room`, `chat/Message`) и JPA-репозитории (`dao/`) |
| `ports` | Интерфейсы сервисов (`UserService`, `PublicationService`, `CommentService`, `ProfileService`, `RoomService`) |
| `adapters/service-adapter` | Реализации сервисов (`*ServiceImpl`), утилита загрузки файлов `TransferFile` |
| `adapters/ui-adapter` | Spring MVC контроллеры и FreeMarker-шаблоны (`.ftlh`) |
| `assembly` | Точка входа (`FindMeApp`), конфигурации Spring Security, MVC и WebSocket |
| `dependencies` | Родительский POM с единым управлением зависимостями |

Модуль `assembly` собирает всё воедино и создаёт исполняемый JAR через `spring-boot-maven-plugin`.

## Ключевые особенности реализации

**Внедрение зависимостей**: Контроллеры и конфигурации ссылаются на бины по имени через `@Qualifier("userServiceImpl")`, `@Qualifier("publicationServiceImpl")` и т.д.

**Загрузка файлов**: `TransferFile` сохраняет изображения (аватары профилей и файлы публикаций) в директорию `upload.path`. Статика раздаётся через `/img/**`.

**Чат (WebSocket)**: STOMP поверх SockJS, endpoint `/messenger`. Брокер сообщений — in-memory (`/user`). Сообщения маршрутизируются через `/app/send`, доставляются получателю через `/user/{recipientId}/messages`.

**Аутентификация**: Spring Security с `UserService` как `UserDetailsService`. Пароли хранятся в открытом виде (`NoOpPasswordEncoder`) — не для продакшена.

**Шаблоны**: FreeMarker (`.ftlh`). Переиспользуемые макросы находятся в `ui-adapter/src/main/resources/templates/macroses/`.

**JPQL-запросы**: `PublicationServiceImpl.findByTags` строит запрос через конкатенацию строк (не параметризованный запрос) — при изменении использовать `@Query` с параметрами.
