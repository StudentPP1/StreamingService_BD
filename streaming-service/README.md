# Movie streaming application

## Description
> The project is inspired by Netflix and Megogo.
It implements the backend part of a simple streaming application with a test payment system.
Users can choose and purchase different subscription plans, each containing a list of movies.
They can also view detailed information about movies, directors, actors, and subscription plans.
Admin roles are implemented to manage content and user access.

## Team
| Domain                      | GitHub                                      |
|-----------------------------|---------------------------------------------|
| Movie, Actor, Performance   | [markOone](https://github.com/markOone) |
| Subscption, Subscption Plan | [arcctg](https://github.com/arcctg)        |
| Payment, User               | [StudentPP1](https://github.com/StudentPP1) |

## Tech stack

### Language and version
- **Java** 21 — Modern LTS version with enhanced features

### Framework
- **Spring Boot** 4.0.0 — Comprehensive framework for building production-ready applications
- **Spring Web MVC** — RESTful web services
- **Spring Security** — Authentication and authorization
- **Spring Data JPA** — Database access layer with repository pattern

### ORM and Database
- **Hibernate** — ORM for Java (via Spring Data JPA)
- **PostgreSQL** 16 — Relational database
- **Flyway** 11.1.0 — Database migration tool

### Testing Framework
- **JUnit** 5 — Unit testing framework
- **Mockito** — Mocking framework for tests
- **Testcontainers** — Integration testing with Docker containers
- **Spring Security Test** — Security testing utilities

### Other Technologies
- **Docker** and **Docker Compose** — Containerization and orchestration
- **Swagger/OpenAPI** 3.0.0 — API documentation (SpringDoc)
- **Stripe** 31.0.0 — Payment processing integration
- **MapStruct** 1.6.3 — DTO mapping
- **Lombok** — Boilerplate code reduction
- **Bean Validation** — Request validation

## Setup Instructions

### Prerequisites

- **Docker** and **Docker Compose** installed on your system
- **Git** for cloning the repository
- (Optional) **Java 21** and **Maven** for local development

### Quick Start

1. **Clone the repository**

```bash
git clone https://github.com/markOone/StreamingService_BD.git
cd streaming-service
```

2. Running application

**Start with all services and app in Docker**

```bash
docker compose --profile app up -d
```

**Start only services needed for local run**

```bash
docker compose up -d
```

This automatically:
- Builds the Docker image for the backend application
- Starts PostgreSQL database
- Starts PgAdmin for database management
- Starts Stripe CLI for webhook forwarding
- Applies Flyway database migrations

The application will be available at `http://localhost:8081`

### Configuration (Optional)

If you need to change default parameters, create or modify the `.env` file in the project root:

```env
# Database Configuration
DB_USER=postgres
DB_PASSWORD=your_password
DB_NAME=streaming_db

# PgAdmin Configuration
PGADMIN_DEFAULT_EMAIL=admin@admin.com
PGADMIN_DEFAULT_PASSWORD=admin

# Stripe Configuration
STRIPE_SECRET_KEY=your_stripe_secret_key
STRIPE_PUBLIC_KEY=your_stripe_public_key
STRIPE_WEBHOOK_KEY=your_stripe_webhook_key
```

**Default parameters:**
- PostgreSQL port: `5432`
- PgAdmin port: `8080`
- Application port: `8081`
- Database: `streaming_db`
- User: `postgres`

**Stop services:**
```bash
docker compose down
```

**Restart services:**
```bash
docker compose restart
```

**Rebuild and restart:**
```bash
docker compose up -d --build
```

## API Documentation

After starting the application, Swagger documentation is available at:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/v3/api-docs

## Running Tests

**Note:** To run tests, ensure Docker containers with databases are running. Make sure `docker compose up -d` has been executed.

### Run all tests

```bash
mvn clean test
```

### Run specific test file

```bash
mvn -Dtest=PaymentRepositoryTest test
```

### Run specific test method

```bash
mvn -Dtest=PaymentRepositoryTest#deletePaymentsBefore_removesOnlyOlderPayments test
```

## Project Structure

```
streaming-service/
├── src/
│   ├── main/
│   │   ├── java/dev/studentpp1/streamingservice/
│   │   │   ├── StreamingServiceApplication.java  # Application entry point
│   │   │   ├── analytics/          # Business analytics endpoints
│   │   │   ├── auth/               # Authentication and authorization
│   │   │   ├── users/              # User management
│   │   │   ├── payments/           # Stripe payment integration
│   │   │   ├── subscription/       # Subscription logic and plans
│   │   │   ├── movies/             # Movies, actors, directors
│   │   │   └── common/             # Shared configs, exceptions, utilities
│   │   │       ├── config/         # Application configs
│   │   │       ├── exception/      # Global exception handlers
│   │   │       └── validation/     # Custom validators
│   │   └── resources/
│   │       ├── application.yml     # Application configuration
│   │       └── db/migration/       # Flyway SQL migrations
│   └── test/
│       └── java/                   # Unit & integration tests
├── docs/                           # Project documentation
│   ├── schema.md                   # Database schema documentation
│   └── queries.md                  # Complex queries documentation
├── target/                         # Compiled code (generated)
├── .mvn/                           # Maven wrapper files
├── docker-compose.yml              # Docker Compose configuration
├── Dockerfile                      # Docker image for application
├── pom.xml                         # Maven dependencies and build config
├── .env                            # Environment variables (gitignored)
├── .gitignore                      # Git ignore rules
└── README.md                       # This file
```

### Module Architecture

Each business module follows a standard layered structure:

```
module-name/
├── controller/         # REST API controllers (HTTP endpoints)
├── service/            # Business logic layer
├── repository/         # Data access layer (Spring Data JPA)
├── entity/             # JPA entities (database models)
├── dto/                # Data Transfer Objects
├── mapper/             # MapStruct mappers (Entity ↔ DTO)
└── exception/          # Domain-specific exceptions
```

## API examples

> **Note:** All examples use `localhost:8081` as the base URL.  
> This port is defined in `application.yml` (`server.port` parameter) and can be changed as needed.

### Users endpoints

+ Register

```http request
POST /api/auth/register HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "name": "Jonh",
  "surname": "Black",
  "email": "jonh@gmail.com",
  "password": "1234Ahadh~!",
  "birthday": "2004-12-03"
}
```

+ Get info about current user
```http request
GET /api/users/info HTTP/1.1
Host: localhost:8081
```

+ Login

```http request
POST /api/auth/login HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "email": "jonh@gmail.com",
  "password": "1234Ahadh~!"
}
```

+ Update user details

```http request
POST /api/users/update HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "name": "Mike",
  "surname": "White"
}
```

+ Logout
```http request
POST /api/auth/logout HTTP/1.1
Host: localhost:8081
```

+ Soft delete user by id (admin only)
```http request
DELETE /api/users/2 HTTP/1.1
Host: localhost:8081
```

## Payment endpoints
+ Buy subscription

```http request
POST /api/payments/checkout HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
    "id": 1
}
```

+ Payment history of current user
```http request
GET /api/payments/user HTTP/1.1
Host: localhost:8081
```

+ Payment history of selected user's subscription
```http request
GET /api/payments/user/subscription/1 HTTP/1.1
Host: localhost:8081
```

## Subscription endpoints

+ Get all subscription plans (with optional search and pagination)
```http request
GET http://localhost:8081/api/subscription-plans?search=premium&sort=price HTTP/1.1
Host: localhost:8081
```

+ Get subscription plan by ID
```http request
GET /api/subscription-plans/1 HTTP/1.1
Host: localhost:8081
```

+ Subscribe to a plan

```http request
POST /api/subscriptions HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
    "planId": 1
}
```

+ Create family subscription

```http request
POST /api/subscriptions/family HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
    "planId": 1,
    "memberEmails": [
        "family1@gmail.com",
        "family2@gmail.com"
    ]
}
```

+ Get my subscriptions (with pagination)
```http request
GET /api/subscriptions?sort=endTime HTTP/1.1
Host: localhost:8081
```

+ Cancel subscription
```http request
DELETE /api/subscriptions/1 HTTP/1.1
Host: localhost:8081
```

+ Create subscription plan (admin only)

```http request
POST /api/subscription-plans HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
    "name": "Premium Plan",
    "description": "Access to premium content",
    "price": 9.99,
    "duration": 30,
    "includedMovieIds": [1, 2, 3]
}
```

+ Update subscription plan (admin only)

```http request
PUT /api/subscription-plans/1 HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
    "name": "Updated Premium Plan",
    "description": "Enhanced premium content",
    "price": 12.99,
    "duration": 30,
    "includedMovieIds": [1, 2]
}
```

+ Add movies to subscription plan (admin only)

```http request
POST /api/subscription-plans/1/movies HTTP/1.1
Host: localhost:8081
Content-Type: application/json

[4, 5, 6]
```

+ Remove movies from subscription plan (admin only)

```http request
DELETE /api/subscription-plans/1/movies HTTP/1.1
Host: localhost:8081
Content-Type: application/json

[2, 3]
```

+ Delete subscription plan (admin only)
```http request
DELETE /api/subscription-plans/1 HTTP/1.1
Host: localhost:8081
```

## Actors Endpoints 
+ Get actor details (including performances/movies)
```GET /api/actors/1/details HTTP/1.1
Host: localhost:8081
```
+ Get actor by ID
```GET /api/actors/1 HTTP/1.1
Host: localhost:8081
```
+ Create new actor (admin only)
```POST /api/actors HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "name": "Leonardo",
  "surname": "DiCaprio",
  "biography": "American actor and film producer.",
  "birthDate": "1974-11-11"
}
```
+ Update actor (admin only)
```PUT /api/actors/1 HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "name": "Leonardo",
  "surname": "Wilhelm DiCaprio",
  "biography": "Updated biography...",
  "birthDate": "1974-11-11"
}
```
+ Delete actor (admin only)
```DELETE /api/actors/1 HTTP/1.1
Host: localhost:8081
```

## Director endpoints
+ Get director details (including movies)
```GET /api/directors/1/details HTTP/1.1
Host: localhost:8081
```

+ Get director by ID
```GET /api/directors/1 HTTP/1.1
Host: localhost:8081
```
+ Create new director (admin only)
```POST /api/directors HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "name": "Christopher",
  "surname": "Nolan",
  "biography": "British-American film director, producer, and screenwriter."
}
```

+ Update director (admin only)
```PUT /api/directors/1 HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "name": "Christopher",
  "surname": "Nolan",
  "biography": "Updated biography text."
}
```
+ Delete director (admin only)
```DELETE /api/directors/1 HTTP/1.1
Host: localhost:8081
```

## Movie endpoints

+ Get all movies
```GET /api/movies HTTP/1.1
Host: localhost:8081
```
+ Get movie details (including actors/performances)
```GET /api/movies/1/details HTTP/1.1
Host: localhost:8081
```
+ Get movie by ID
```GET /api/movies/1 HTTP/1.1
Host: localhost:8081
```
+ Create new movie (admin only)
```POST /api/movies HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "title": "Inception",
  "description": "A thief who steals corporate secrets through the use of dream-sharing technology...",
  "year": 2010,
  "rating": 8.8,
  "directorId": 1
}
```
+ Update movie (admin only)
```PUT /api/movies/1 HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "title": "Inception",
  "description": "Updated description",
  "year": 2010,
  "rating": 9.0,
  "directorId": 1
}
```
+ Delete movie (admin only)
```DELETE /api/movies/1 HTTP/1.1
Host: localhost:8081
```
## Performance endpoints
+ Get performance by ID
```GET /api/performances/1 HTTP/1.1
Host: localhost:8081
```
+ Add actor to movie (Create performance - admin only)
```POST /api/performances HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "movieId": 1,
  "actorId": 1,
  "role": "Cobb"
}
```
+ Remove actor from movie (Delete performance - admin only)
```DELETE /api/performances/1 HTTP/1.1
Host: localhost:8081
```

## Analytics endpoints

+ Get top directors by revenue (admin only)

```http request
GET /api/analytics/directors-roi?from=2025-01-01T00:00:00&to=2025-12-31T23:59:59 HTTP/1.1
Host: localhost:8081
```

+ Watch percent of subscription plan gained money by month (admin only)

```http request
GET /api/analytics/monthly-plans HTTP/1.1
Host: localhost:8081
```

+ Get actors rating analytics (admin only)
```http request
GET /api/analytics/actors-rating HTTP/1.1
Host: localhost:8081
```

## Additional Information

For detailed information about database structure and schema, see [schema.md](./docs/schema.md)

For information about complex analytical queries, see [queries.md](./docs/queries.md)

