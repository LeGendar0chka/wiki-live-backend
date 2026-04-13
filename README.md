# WikiLive Backend

Бэкенд-сервис для модуля wiki-редактора MWS Tables, разработанный в рамках хакатона True Tech Hack 2026.

## Требования

- Podman с podman-compose (рекомендуется) или Docker с docker-compose
- Альтернативно: Java 17, Maven, PostgreSQL 15, Redis 7 (для запуска без контейнеров)

## Инструкция по установке и запуску

1. Клонирование репозитория: git clone git@github.com:LeGendar0chka/wiki-live-backend.git
2. Переход в папку: cd wiki-live-backend
3. Запуск через Podman Compose: podman-compose up -d
4. Запуск через Docker Compose: docker-compose up -d
5. Альтернативный запуск скриптом (если нет Compose): chmod +x start.sh && ./start.sh
6. Ручной запуск без контейнеров: mvn clean package && java -jar target/wiki-live-backend-1.0.0.jar

## Остановка и очистка

- Для Compose: podman-compose down или docker-compose down
- Для скрипта: podman stop wikilive-backend-app wikilive-postgres wikilive-redis && podman rm wikilive-backend-app wikilive-postgres wikilive-redis && podman network rm wikilive-net

## Список API эндпоинтов

- POST /api/wiki/pages — создать страницу
- GET /api/wiki/pages/{id} — получить страницу по ID
- PATCH /api/wiki/pages/{id} — обновить страницу (автосохранение)
- GET /api/wiki/pages/{id}/backlinks — получить обратные ссылки
- GET /api/tables/search?spaceId=...&q=... — поиск таблиц
- GET /api/tables/{tableId} — метаданные таблицы
- POST /api/ai/assist — запрос к MWS GPT
- WebSocket: ws://localhost:8080/api/wiki/collab/{pageId} — совместное редактирование

## Примеры запросов (curl)

- **Создание страницы**: `curl -X POST http://localhost:8080/api/wiki/pages -H "Content-Type: application/json" -H "X-User-Id: demo-user" -d '{"spaceId":"demo-space","title":"Пример страницы","contentJson":"{\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"Текст страницы\"}]}]}"}'`
- **Обновление страницы**: `curl -X PATCH http://localhost:8080/api/wiki/pages/{id} -H "Content-Type: application/json" -H "X-User-Id: demo-user" -d '{"contentJson":"{\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"Измененный текст\"}]}]}"}'`
- **Получение обратных ссылок** : `curl http://localhost:8080/api/wiki/pages/{id}/backlinks`
- **AI-запрос**: `curl -X POST http://localhost:8080/api/ai/assist -H "Content-Type: application/json" -d '{"prompt":"Суммаризируй выделенный текст","context":"Текст для обработки"}'`

## Структура проекта (дерево папок)

- pom.xml, Dockerfile, docker-compose.yml, start.sh, README.md
- src/main/java/com/mws/wiki/...
- src/main/resources/application.yml и db/migration/

## Используемые технологии

- Java 17, Spring Boot 3.2.4, Spring Data JPA
- PostgreSQL 15, Redis 7
- WebSocket (Yjs), Flyway
- Podman / Docker

## Лицензия

Проект разработан в рамках хакатона True Tech Hack 2026.
