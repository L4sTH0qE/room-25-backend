# Room 25 (сервер)

### Локальный запуск
Перед запуском приложений необходимо поднять контейнер для БД. Для этого выполните команду docker-compose up -d из
корня проекта (по файлу compose.yaml).
После поднятия БД приступайте к запуску приложений (клиента и сервера).

Для сервера укажите такие переменные среды: 
- SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/backend;
- SPRING_DATASOURCE_USERNAME=admin;
- SPRING_DATASOURCE_PASSWORD=admin;
- CLIENT_URL=http://localhost:3000