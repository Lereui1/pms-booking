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

### Hotel API + DTO

- [x] Added `HotelRequest`
- [x] Added `HotelResponse`
- [x] Converted Hotel API from Entity responses to DTO responses
- [x] Hotel creation uses `HotelRequest`
- [x] Hotel responses use `HotelResponse`
- [x] Added validation for hotel request fields
- [x] `@NotBlank` validation
- [x] `@Size(max = 255)` for name and address
- [x] `@Size(max = 1000)` for description
- [x] Tested Hotel API with JWT authentication
- [x] Tested validation errors with Postman
- [x] Tested unauthorized access without JWT

### Hotel API DTO flow

```text
JSON
 ↓
HotelRequest
 ↓
HotelController
 ↓
HotelService
 ↓
Hotel Entity
 ↓
PostgreSQL
 ↓
Hotel Entity
 ↓
HotelResponse
 ↓
JSON
```

### Booking API

* [x] Added `BookingController`
* [x] Added `BookingService`
* [x] Implemented booking creation: `POST /api/bookings`
* [x] Added `BookingRequest` and `BookingResponse`
* [x] Retrieved the authenticated user from `SecurityContext`
* [x] Added room existence check
* [x] Added check-in and check-out date validation
* [x] Implemented booking date overlap detection
* [x] Excluded `CANCELLED` bookings from availability checks
* [x] Added booking conflict handling
* [x] Returned booking data through `BookingResponse`

### Booking creation flow

```text
POST /api/bookings
   ↓
BookingRequest
   ↓
BookingController
   ↓
BookingService
   ↓
@Transactional
   ↓
Get authenticated User
   ↓
Lock Room
   ↓
Validate dates
   ↓
Check existing bookings
   ↓
Create Booking
   ↓
BookingRepository
   ↓
PostgreSQL
   ↓
BookingResponse
   ↓
JSON
```

### Transactions + Locking

* [x] Learned transaction boundaries
* [x] Implemented transaction management with `@Transactional`
* [x] Studied race conditions
* [x] Implemented pessimistic locking with `PESSIMISTIC_WRITE`
* [x] Configured optimistic locking with `@Version`
* [x] Used pessimistic locking to synchronize concurrent booking requests
* [x] Added a concurrent booking integration test
* [x] Tested concurrent requests using multiple threads and `CountDownLatch`
* [x] Verified that only one concurrent booking succeeds for the same room and overlapping dates
* [x] Configured a separate test database: `pms_booking_test`
* [x] Verified the test suite with Maven: `BUILD SUCCESS`

### Concurrent booking test flow

```text
Request A                 Request B
    ↓                         ↓
    └────── Start together ───┘
                  ↓
          Lock the same Room
                  ↓
       One request waits for lock
                  ↓
          Check booking dates
                  ↓
       A: SUCCESS / B: CONFLICT
                  ↓
       No double booking
```

## Task Management

The Task Management module provides REST API endpoints for creating and managing hotel staff tasks.

### Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks/{id}` | Get a task by ID |
| POST | `/api/tasks` | Create a task |
| PUT | `/api/tasks/{id}` | Update task details |
| PATCH | `/api/tasks/{id}/status?status=IN_PROGRESS` | Update task status |
| DELETE | `/api/tasks/{id}` | Delete a task |

### Task fields

- `hotelId` — required hotel ID.
- `roomId` — optional room ID.
- `bookingId` — optional booking ID.
- `assignedStaffId` — optional ID of a staff user.
- `title` — required, up to 255 characters.
- `description` — required, up to 2000 characters.
- `dueDate` — optional due date.
- `status` — task status.
- `createdAt` — task creation timestamp.

### Task status workflow

Tasks follow the allowed status transitions:

`BACKLOG → IN_PROGRESS → DONE`

Invalid status transitions are rejected with HTTP 400.

### Validation and error handling

The service validates that referenced hotels, rooms, bookings, and staff users exist and that related entities belong to the selected hotel. Users assigned to tasks must have the `STAFF` role.

The API uses structured error responses for validation errors, missing resources, and invalid business operations.

### Database migration

Flyway migration `V6__extend_tasks.sql` adds the room, assigned staff, and due date fields to the tasks table.

### Task Security + RBAC

Role-Based Access Control (RBAC) restricts task management operations based on the authenticated user's role and hotel assignment.

#### Authorization rules

- [x] Restricted Task API endpoints to `STAFF` and `HOTEL_ADMIN` using `@PreAuthorize`
- [x] Associated staff users with hotels through the `hotel_id` column in the `users` table
- [x] Retrieved the authenticated user from `SecurityContext`
- [x] Restricted task listing to the authenticated user's hotel
- [x] Added hotel ownership checks before reading, updating or deleting tasks
- [x] Prevented users from creating tasks for another hotel
- [x] Prevented assigning staff members belonging to another hotel
- [x] Added service-level authorization checks

#### Database migration

- [x] Added `V7__add_hotel_to_users.sql`
- [x] Added nullable `hotel_id` column to `users`
- [x] Added a foreign key from `users.hotel_id` to `hotels.id`

#### Security testing

- [x] Verified that `CLIENT` cannot access Task API endpoints
- [x] Tested cross-hotel access restrictions with Postman
- [x] Added automated tests for task authorization
- [x] Tested reading, status updates and deletion of another hotel's task
- [x] Tested access to tasks belonging to the user's own hotel
- [x] Tested creating tasks for another hotel
- [x] Tested assigning staff from another hotel
- [x] Verified the full Maven test suite: 8 tests passed, 0 failures, 0 errors
