# DevTracker API

[![Ler este README em Português (Brasil)](https://img.shields.io/badge/README-Ler_em_Portugu%C3%AAs_(Brasil)-009739?style=for-the-badge)](readme.md)

**Manage clients, development projects, and project finances through a Java REST API.**

![Java 25](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square)
![Spring Boot 4.1.1](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square)
![Database H2](https://img.shields.io/badge/Database-H2-09476B?style=flat-square)
![Docker Compose](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square)

DevTracker API connects clients to development projects and records their income and expenses. Projects support fixed-price and hourly-rate pricing, while financial transactions can be retrieved in BRL or converted to USD using an external exchange-rate provider.

Built as a backend learning project, it brings together REST endpoints, business rules, persistence, automated tests, and containerized execution.

## Contents

- [Features](#features)
- [Technology stack](#technology-stack)
- [Architecture](#architecture)
- [Getting started](#getting-started)
- [Running with Docker](#running-with-docker)
- [API endpoints](#api-endpoints)
- [Postman collection](#postman-collection)
- [Request examples](#request-examples)
- [Validation and errors](#validation-and-errors)
- [Testing](#testing)
- [Configuration and data](#configuration-and-data)
- [Author](#author)

## Features

- Create, find, update, and deactivate clients.
- Create fixed-price projects with a budget or hourly-rate projects with an hourly rate.
- Track worked hours, project dates, and status.
- Filter projects by status: `IN_PROGRESS`, `COMPLETED`, or `CANCELLED`.
- Record project income and expenses.
- Retrieve financial transactions by ID or project.
- Return transaction amounts in BRL or convert them to USD.
- Validate request fields and return structured error responses.
- Run the API with Docker Compose and persistent H2 storage.

> Client deletion is a soft delete: the client is marked inactive rather than removed from the database.

## Technology stack

| Technology | Purpose |
| --- | --- |
| Java 25 | Application language and runtime |
| Spring Boot 4.1.1 / Spring MVC | REST API and application configuration |
| Spring Data JPA / Hibernate | Persistence and entity mapping |
| H2 | Embedded database |
| Jakarta Bean Validation | Request validation |
| MapStruct | Mapping between entities and DTOs |
| Lombok | Generated constructors and accessors |
| JUnit Jupiter, Mockito, MockMvc | Automated service and controller tests |
| Maven Wrapper | Build and dependency management |
| Docker / Docker Compose | Image creation, configuration, and persistent storage |

## Architecture

The application separates HTTP handling, business operations, persistence, and DTO mapping.

```text
HTTP request
    |
    v
Controller -> Service -> Repository -> H2
                 |
                 +-> Mapper -> Response DTO
                 |
                 +-> Currency integration, when USD conversion is requested
```

```text
src/
├── main/
│   ├── java/com/gustavomp/devtrackerapi/
│   │   ├── annotations/    # Custom validation annotations
│   │   ├── controllers/   # HTTP endpoints
│   │   ├── dtos/          # Request and response records
│   │   ├── exceptions/    # Exceptions and HTTP error handling
│   │   ├── integrations/  # External API client
│   │   ├── mappers/       # MapStruct mappings
│   │   ├── models/        # Entities and enums
│   │   ├── repositories/  # Spring Data repositories
│   │   ├── services/      # Business operations
│   │   └── validators/    # Custom validation rules
│   └── resources/
│       └── application.properties
└── test/java/com/gustavomp/devtrackerapi/
    ├── controllers/
    ├── services/
    └── suites/
```

## Getting started

### Requirements

- JDK 25.
- Git.
- Docker Desktop with Linux containers and Docker Compose, if using containers.

Maven is supplied through the repository's wrapper.

### Clone and build

The commands below use Windows PowerShell.

```powershell
git clone https://github.com/gustavo-martins-pereira/DevTracker_API.git
cd DevTracker_API
.\mvnw.cmd clean verify
```

On Linux or macOS, use `./mvnw` instead of `.\mvnw.cmd`. If necessary, first run `chmod +x mvnw`.

### Run locally

```powershell
.\mvnw.cmd spring-boot:run
```

The local API is available at [http://localhost:8080](http://localhost:8080).

Alternatively, run the packaged application:

```powershell
java -jar target/devtrackerapi-0.0.1-SNAPSHOT.jar
```

> Local execution uses an in-memory H2 database and `create-drop`. Data is temporary and does not survive application shutdown.

## Running with Docker

### 1. Configure the database password

```powershell
Copy-Item .env.example .env
```

Edit `.env` and set a local development password:

```dotenv
DB_PASSWORD=replace-with-your-local-password
```

The real `.env` file is excluded from Git. Keep credentials out of commits.

### 2. Package and start

```powershell
.\mvnw.cmd clean verify
docker compose up -d --build
```

The Dockerfile copies the JAR from `target/`; it does not compile Java source. Rebuild the JAR before rebuilding the image after code changes.

The container API is available at [http://localhost:8081](http://localhost:8081). Compose maps host port `8081` to container port `8080` and binds it to localhost.

### 3. Check status and logs

```powershell
docker compose ps
docker compose logs -f api
```

### 4. Stop or restart

```powershell
docker compose down
docker compose up -d
```

The named volume preserves the file-based H2 database across container replacement.

> **Data reset:** `docker compose down -v` also deletes the database volume. Use it only when you intend to discard the stored data.

## API endpoints

Use `http://localhost:8080` for local execution or `http://localhost:8081` for Docker Compose.

### Clients

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/clients` | Create a client |
| GET | `/clients` | List active clients |
| GET | `/clients?name=Ana` | Find an active client by name |
| PUT | `/clients?id=1` | Update a client |
| DELETE | `/clients?id=1` | Deactivate a client |

### Projects

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/projects` | Create a project |
| GET | `/projects` | List projects |
| GET | `/projects?projectStatus=COMPLETED` | Filter projects by status |
| GET | `/projects/1` | Find a project by ID |
| PUT | `/projects?id=1` | Update project details |
| PATCH | `/projects?id=1` | Update project status |

### Financial transactions

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/financial-transactions` | Create a transaction |
| GET | `/financial-transactions?projectId=1` | List transactions for a project |
| GET | `/financial-transactions?projectId=1&currency=USD` | List transactions with USD amounts |
| GET | `/financial-transactions/1` | Find a transaction by ID |
| GET | `/financial-transactions/1?currency=USD` | Find a transaction with its USD amount |
| DELETE | `/financial-transactions/1` | Delete a transaction |

> BRL is the default response currency. USD conversion changes the returned amount without modifying the stored transaction amount.

### Exchange rate

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/awesomeapi/currency/usd-brl` | Retrieve the USD/BRL exchange rate |

>Currency conversion and the exchange-rate endpoint require access to the external provider.

## Postman collection

Import the [DevTracker Postman collection](postman/DevTracker.postman_collection.json) to explore the API without writing HTTP requests manually.

The collection contains 19 saved requests organized into four folders:

| Folder | Requests |
| --- | --- |
| Client | Create, list, find by name, update, and deactivate |
| Project | Create, list, filter by status, find by ID, update details, and update status |
| Financial Transaction | Create, list by project, retrieve by ID, request USD amounts, and delete |
| Awesome API | Retrieve the exchange rate; includes a conversion request not yet implemented |

### Import and configure

1. Open Postman, choose **Import**, and select `postman/DevTracker.postman_collection.json`.
2. Open the imported collection and its **Variables** tab.
3. Set `CARREIRA_BACKEND_JAVA_CHECKPOINT_1_DEVTRACKER` to the appropriate base URL:

   | Execution mode | Variable value |
   | --- | --- |
   | Local Spring Boot application | `http://localhost:8080` |
   | Docker Compose | `http://localhost:8081` |

4. Save the collection settings and open a request.
5. Review its JSON body, query parameters, and IDs before choosing **Send**.

The exported variable is intentionally blank. Requests use it as follows:

```text
{{CARREIRA_BACKEND_JAVA_CHECKPOINT_1_DEVTRACKER}}/clients
```

Create a client first, then a project linked to that client, then financial transactions linked to the project. Replace example IDs with the IDs returned by your API. For the find-by-name request, replace the placeholder `name` query value with an existing client's name.

> The collection includes `GET /awesomeapi/currency/convert?amount=100`, which is not implemented in the current local controller. Use the transaction endpoints with `currency=USD` for the implemented conversion functionality.


## Request examples

Create a client first, then use its returned ID to create a project. Use the returned project ID when creating transactions. The IDs below are examples.

### Create a client

`POST /clients`

```json
{
  "name": "Ana",
  "email": "ana@example.com",
  "phone": "12345678",
  "active": true
}
```

Example using PowerShell with the Docker URL:

```powershell
$body = @{
    name = "Ana"
    email = "ana@example.com"
    phone = "12345678"
    active = $true
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8081/clients" -ContentType "application/json" -Body $body
```

### Create a fixed-price project

`POST /projects`

```json
{
  "name": "Portfolio Website",
  "description": "Design and development of a portfolio website",
  "workedHours": 0,
  "startDate": "2026-01-01",
  "dueDate": "2026-01-30",
  "projectStatus": "IN_PROGRESS",
  "clientId": 1,
  "projectType": "FIXED_PRICE",
  "budget": 2500
}
```

For an hourly-rate project, use `"projectType": "HOURLY_RATE"` and replace `budget` with a positive `hourlyRate`.

### Record project income

`POST /financial-transactions`

```json
{
  "description": "Initial project payment",
  "amount": 500,
  "date": "2026-01-01",
  "transactionType": "INCOME",
  "projectId": 1
}
```

Use `EXPENSE` to record an expense. The amount must remain positive; the transaction type identifies its direction.

### Update project status

`PATCH /projects?id=1`

```json
{
  "projectStatus": "COMPLETED"
}
```

## Validation and errors

Requests are validated before service execution. Examples include required fields, email format, an eight-digit client phone number, positive monetary amounts, and valid enum values.

Project creation also validates the pricing type: fixed-price projects require a positive budget, and hourly-rate projects require a positive hourly rate.

| Status | Meaning |
| --- | --- |
| 200 | Successful read or update |
| 201 | Resource created |
| 204 | Successful deletion or deactivation, with no response body |
| 400 | Invalid request, conversion failure, or invalid argument |
| 404 | Requested resource not found |
| 409 | Conflicting resource |
| 502 | External currency provider failure |
| 500 | Unexpected server error |

Error responses contain `timestamp`, `status`, and `error`. The error value may be a message or a map of field errors.

Illustrative validation response:

```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 400,
  "error": {
    "email": "Invalid email format"
  }
}
```

## Testing

The project uses JUnit Jupiter and Mockito for service tests, plus MockMvc for controller request and response tests.

Run the standard Maven verification lifecycle:

```powershell
.\mvnw.cmd clean verify
```

Run the grouped controller and service suites explicitly:

```powershell
.\mvnw.cmd "-Dtest=AllTestsSuite" test
```

In IntelliJ, run `AllTestsSuite` to browse tests by controller and service entity:

```text
All Tests
├── Controllers
│   ├── Client Controller
│   ├── Project Controller
│   └── Financial Transaction Controller
└── Services
    ├── Client
    ├── Financial Transaction
    └── Project
```

Suites organize existing tests. Choose either the aggregate suite or ordinary test discovery for a run to avoid executing the same cases twice.

## Configuration and data

| Setting | Local defaults | Docker Compose |
| --- | --- | --- |
| Host port | 8080 | 8081 |
| Container port | Not applicable | 8080 |
| H2 storage | In memory | File at `/data/devtracker` |
| Hibernate schema handling | `create-drop` | `update` |
| Database password | Empty | Supplied through `DB_PASSWORD` |
| H2 browser console | Enabled at `/h2-console` | Disabled |

Compose mounts the `devtracker-data` named volume at `/data`. Its actual Docker name normally includes the Compose project prefix.

This is a learning setup. Before exposing the API publicly, review access control, dependency advisories, database migrations, and deployment configuration.

## Author

Developed by [Gustavo Martins Pereira](https://github.com/gustavo-martins-pereira).
