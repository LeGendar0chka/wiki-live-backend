#!/bin/bash

# Создаём сеть, если её нет
podman network exists wikilive-net || podman network create wikilive-net

# Запускаем PostgreSQL (если контейнер уже есть — стартуем, иначе создаём)
if podman container exists wikilive-postgres; then
    podman start wikilive-postgres
else
    podman run -d \
      --name wikilive-postgres \
      --network wikilive-net \
      -e POSTGRES_DB=wikilive \
      -e POSTGRES_USER=wikiuser \
      -e POSTGRES_PASSWORD=wikipass \
      -v pgdata:/var/lib/postgresql/data \
      docker.io/library/postgres:15
fi

# Запускаем Redis
if podman container exists wikilive-redis; then
    podman start wikilive-redis
else
    podman run -d \
      --name wikilive-redis \
      --network wikilive-net \
      docker.io/library/redis:7-alpine
fi

# Ждём пару секунд, чтобы БД и Redis проснулись
sleep 5

# Запускаем Backend
if podman container exists wikilive-backend-app; then
    podman stop wikilive-backend-app
    podman rm wikilive-backend-app
fi

podman run -d \
  --name wikilive-backend-app \
  --network wikilive-net \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://wikilive-postgres:5432/wikilive \
  -e SPRING_DATASOURCE_USERNAME=wikiuser \
  -e SPRING_DATASOURCE_PASSWORD=wikipass \
  -e SPRING_REDIS_HOST=wikilive-redis \
  -e MWS_TABLES_API_URL=https://tables.mws.ru/api \
  -e MWS_GPT_API_URL=https://gpt.mws.ru/api \
  -e MWS_GPT_API_KEY=sk-ewgiaPC3A6pPDYHwR8siVA \
  localhost/wikilive-backend:latest

echo "Все сервисы запущены. API доступен на http://localhost:8080"