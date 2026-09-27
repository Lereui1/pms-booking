# PMS & Booking SaaS Platform

REST API платформа для автоматизации гостиничного бизнеса.

## Tech Stack

- Java 17+
- Spring Boot 3
- Maven
- Spring Web
- PostgreSQL
- Spring Data JPA / Hibernate
- Spring Security + JWT
- RabbitMQ
- JUnit 5
- Mockito
- Testcontainers
- Docker
- GitHub Actions
- Swagger / OpenAPI

## Current Progress

### Spring Boot Foundation

- [x] Spring Boot project
- [x] Maven
- [x] Project structure
- [x] Controller
- [x] Service
- [x] Repository layer
- [x] Hotel entity
- [x] GET /api/hotels
- [x] GET /api/hotels/{id}
- [x] POST /api/hotels

### PostgreSQL + Flyway

- [x] PostgreSQL configured
- [x] PostgreSQL database `pms_booking` connected
- [x] Flyway configured
- [x] Initial database migration created
- [x] `V1__create_users.sql`
- [x] `users` table created by Flyway
- [x] `flyway_schema_history` created and managed by Flyway
- [x] Database password configured through `DB_PASSWORD` environment variable
