# Movie streaming application

## Description
> The project is inspired by Netflix and Megogo.
It implements the backend part of a simple streaming application with a test paymentEntity system.
Users can choose and purchase different subscription plans, each containing a list of movies.
They can also view detailed information about movies, directors, actors, and subscription plans.
Admin roles are implemented to manage content and user access.

## Tech stack

### Language and version
- **Java** 21 - Modern LTS version with enhanced features

### Framework
- **Spring Boot** 4.0.0 - Comprehensive framework for building production-ready applications
- **Spring Web MVC** - RESTful web services
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access layer with repository pattern

### ORM and Database
- **Hibernate** - ORM for Java (via Spring Data JPA)
- **PostgreSQL** 16 - Relational database
- **Flyway** 11.1.0 - Database migration tool

### Testing Framework
- **JUnit** 5 - Unit testing framework
- **Mockito** - Mocking framework for tests
- **Testcontainers** - Integration testing with Docker containers
- **Spring Security Test** - Security testing utilities

### Other Technologies
- **Docker** and **Docker Compose** - Containerization and orchestration
- **Swagger/OpenAPI** 3.0.0 - API documentation (SpringDoc)
- **Stripe** 31.0.0 - Payment processing integration
- **MapStruct** 1.6.3 - DTO mapping
- **Lombok** - Boilerplate code reduction
- **Bean Validation** - Request validation

## Setup Instructions

### Prerequisites

- **Docker** and **Docker Compose** installed on your system
- **Git** for cloning the repository
- (Optional) **Java 21** and **Maven** for local development

### Quick Start

1. **Clone the repository**

```bash
git clone https://github.com/StudentPP1/StreamingService_BD.git
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
docker compose down -v
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

## API examples

> **Note:** All examples use `localhost:8081` as the base URL.  
> This port is defined in `application.yml` (`server.port` parameter) and can be changed as needed.

## Modular monolith (Lab 5)

The project includes explicit module contracts for cross-context communication:

- `payments.api.event.*`
- `subscription.api.event.*`
- `analytics.api.*`

The `analytics` bounded context is read-only and updates projections from async events.

- Endpoint: `GET /api/analytics/summary`

