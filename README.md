# Bank Recommendations Service

Сервис для формирования персонализированных рекомендаций банковских продуктов на основе транзакционной активности
клиентов.

Приложение анализирует операции клиента, применяет статические и динамические правила и формирует персональные
рекомендации, доступные через REST API и Telegram-бота.

## Стек технологий

- Java 22
- Spring Boot 3
- Spring Web
- Spring Data JDBC
- Spring Data JPA
- PostgreSQL
- H2 Database
- Liquibase
- Caffeine Cache
- OpenAPI / Swagger
- Telegram Bot API
- Maven

## Основные возможности

- Получение рекомендаций для пользователя через REST API
- Поддержка статических правил рекомендаций
- Поддержка динамических правил рекомендаций
- Получение рекомендаций через Telegram-бота
- Получение статистики срабатывания динамических правил
- Очистка кеша через management endpoint
- Получение информации о сервисе через management endpoint

## Архитектура и документация

Подробная проектная документация доступна в GitHub Wiki:

- [Requirements](https://github.com/OATimofeev/bankRecommendationsService/wiki/Requirements)
- [Requirements Traceability](https://github.com/OATimofeev/bankRecommendationsService/wiki/Requirements-Traceability)
- [Architecture](https://github.com/OATimofeev/bankRecommendationsService/wiki/Architecture)
- [REST API (OpenAPI)](https://github.com/OATimofeev/bankRecommendationsService/wiki/REST-API-(OpenAPI))
- [Deployment](https://github.com/OATimofeev/bankRecommendationsService/wiki/Deployment)

## REST API

Сервис предоставляет REST API для получения рекомендаций по пользователю. Формат основного эндпоинта соответствует
заданию: `GET /recommendation/{user_id}`.

После запуска приложения Swagger UI доступен по адресу:

```text
http://localhost:8080/swagger-ui/index.html
```

## Развертывание

Инструкция по запуску и настройке приложения находится в Wiki:

- [Deployment](https://github.com/OATimofeev/bankRecommendationsService/wiki/Deployment)

Основной способ передачи конфигурации в приложение — файл:

```text
env/.env.properties
```

Пример содержимого:

```properties
application.recommendations-db.username=username
application.recommendations-db.password=password
application.recommendations-db.url=jdbc:postgresql://localhost:5555/db
application.recommendations-db.read-only=false
application.recommendations-db.driver-class-name=org.postgresql.Driver
application.transactions-db.url=jdbc:h2:file:./transaction
application.transactions-db.driver-class-name=org.h2.Driver
application.transactions-db.read-only=true
telegram.bot.token=YOUR_TELEGRAM_BOT_TOKEN
```

## Сборка и запуск

1. Собрать проект:

```bash
mvn clean package
```

2. Запуск приложения (пример):

```bash
mvn spring-boot:run
```

или запуск собранного JAR:

```bash
java -jar target/recservice-*.jar
```

> Важно: команда `mvn clean package` обязательна перед первым запуском,
> так как в процессе сборки генерируются служебные файлы (в т.ч. build-info для /management/info и swagger).

## Структура проекта

Проект реализован как Spring Boot-приложение, использующее JDBC/JdbcTemplate для работы с транзакционной базой и
exposing REST API для выдачи рекомендаций пользователю.

В минимальной архитектуре задания ожидаются контроллер, сервис рекомендаций, репозиторий запросов к БД и набор
реализаций `RecommendationRuleSet`, по одной на каждую рекомендацию.