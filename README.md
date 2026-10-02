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

### JPA + Hibernate

- [x] Spring Data JPA
- [x] Hibernate
- [x] User entity
- [x] Hotel entity
- [x] Room entity
- [x] Booking entity
- [x] Task entity
- [x] JPA repositories
- [x] Entity relationships
- [x] Lazy loading
- [x] Optimistic locking with @Version
- [x] Flyway migrations for hotels, rooms, bookings and tasks
- [x] Hibernate schema validation

### DTO + Validation + Exception Handling

- [x] Request DTOs
- [x] Response DTOs
- [x] UserRequest / UserResponse
- [x] RoomRequest / RoomResponse
- [x] BookingRequest / BookingResponse
- [x] TaskRequest / TaskResponse
- [x] ErrorResponse
- [x] Bean Validation
- [x] @Valid
- [x] @NotNull
- [x] @NotBlank
- [x] @Size
- [x] @Email
- [x] GlobalExceptionHandler
- [x] @RestControllerAdvice
- [x] ResourceNotFoundException
- [x] BookingConflictException
- [x] AccessDeniedException
- [x] ValidationException
- [x] MethodArgumentNotValidException handling

## Spring Security + JWT

- [x] Added Spring Security
- [x] Configured `SecurityFilterChain`
- [x] Added `BCryptPasswordEncoder`
- [x] Configured `AuthenticationManager`
- [x] Added custom `UserDetails`
- [x] Added `UserDetailsService`
- [x] Implemented registration: `POST /auth/register`
- [x] Implemented login: `POST /auth/login`
- [x] Added JWT generation and validation
- [x] Added JWT authentication filter
- [x] Configured stateless authentication
- [x] Added roles: `CLIENT`, `STAFF`, `HOTEL_ADMIN`
- [x] Added method-level authorization with `@PreAuthorize`
- [x] Protected API endpoints with Bearer JWT
- [x] Tested authentication and authorization with Postman

### Authentication flow

```text
 Register
   ↓
 User saved with BCrypt password
   ↓
 Login
   ↓
 AuthenticationManager
   ↓
 JWT
   ↓
 Authorization: Bearer <token>
   ↓
 JwtAuthenticationFilter
   ↓
 UserDetails
   ↓
 SecurityContext
   ↓
 Protected endpoint
```
