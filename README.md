# TravelPlan API

The REST API behind [TravelPlan](https://github.com/Sreelekha2408/travel-planner-web), a travel
planner: accounts, trips, day-by-day itineraries and expenses. Spring Boot + PostgreSQL, with the
session JWT in an HttpOnly cookie and CSRF protection.

> Built with React and Spring Boot; used Claude for code review, debugging and test generation.

## Tech stack

| Area | Choices |
| ---- | ------- |
| Runtime | Java 17, Spring Boot 3.5 |
| Web & security | Spring Web, Spring Security 6.5, JJWT (HS256), CSRF double-submit cookie |
| Data | Spring Data JPA (Hibernate), PostgreSQL, Liquibase migrations |
| Validation | Bean Validation (Hibernate Validator) on request bodies |
| Mapping & boilerplate | MapStruct, Lombok |

## Running locally

### Prerequisites

- **Java 17** (the Maven wrapper downloads Maven itself)
- **PostgreSQL** with an empty database called `travel_planner`

### Configure

Secrets live in `src/main/resources/local.properties`, which is gitignored. Copy the example and
fill it in:

```bash
cp src/main/resources/local.properties.example src/main/resources/local.properties
```

```properties
spring.datasource.username=your-db-user   # if it isn't the default in application.properties
spring.datasource.password=your-db-password
app.jwt.secret=...                        # openssl rand -base64 32
```

### Start

```bash
./mvnw spring-boot:run   # http://localhost:8080
```

On start-up Liquibase creates or updates the schema; then Hibernate validates that the entities
match the tables (`ddl-auto=validate`) and refuses to start if they don't.

### Production settings

| Environment variable | Purpose |
| -------------------- | ------- |
| `APP_JWT_SECRET` | JWT signing key (required; the app won't start without one) |
| `APP_AUTH_COOKIE_SECURE=true` | Send the auth cookie over HTTPS only |
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | Database connection |

## API

All endpoints except sign-up, login and logout need the `access_token` cookie. Requests
that change data (POST/PUT/DELETE) must also send the `X-XSRF-TOKEN` header with the value of the
`XSRF-TOKEN` cookie.

| Method | Endpoint | Purpose |
| ------ | -------- | ------- |
| `POST` | `/users/signup` | Create an account |
| `POST` | `/users/login` | Sign in; sets the HttpOnly `access_token` cookie (`rememberMe` for 7 days) |
| `GET` | `/users/me` | The signed-in user (401 if not signed in) |
| `POST` | `/users/logout` | Sign out; expires the cookie |
| `POST` | `/trip/create` | Create a trip |
| `GET` | `/trip/list` | The signed-in user's trips |
| `GET` / `PUT` / `DELETE` | `/trip/{id}` | One trip; delete also removes its itinerary and expenses |
| `GET` / `POST` | `/itineraries/{tripId}` | A trip's itinerary / add a list of activities |
| `PUT` / `DELETE` | `/itineraries/{itemId}` | Edit or delete one activity |
| `DELETE` | `/itineraries/all/{tripId}` | Clear a trip's itinerary |
| `GET` / `POST` | `/expenses/{tripId}` | A trip's expenses, newest first / add one |
| `PUT` / `DELETE` | `/expenses/{expenseId}` | Edit or delete one expense |

### Errors

Errors come back as JSON with a readable `message`. Validation errors list every problem at
once, field by field (in the order the fields appear in the form):

```json
{
  "message": "Trip name is required. End date is required.",
  "fieldErrors": { "title": "Trip name is required", "endDate": "End date is required" }
}
```

For a list body (adding several activities), keys name the item: `"[1].title"`.

## How it's built

- **Cookie auth**: the JWT is set in an HttpOnly, SameSite=Lax cookie, so JavaScript in the
  browser can never read it; CSRF uses Spring's double-submit cookie
  (`CookieCsrfTokenRepository`). A 401 entry point replaces the default login redirect.
- **Ownership**: every trip, itinerary and expense operation checks that the data belongs to the
  signed-in user. Someone else's data gets the same 404 as missing data, so ids can't be probed.
- **Validation in two layers**: single-field rules are annotations on the request classes
  (`@NotBlank`, `@Size`, `@Positive`, `@Email`...), checked by `@Valid` before the service runs.
  Rules that compare fields or need data stay in the services: end date on or after start date,
  a supported currency, travellers covering the organiser plus everyone invited, unique email.
- **Errors in one place**: `ApiExceptionHandler` (`@RestControllerAdvice`) turns validation,
  malformed-JSON, auth and `ResponseStatusException` errors into the JSON above.
- **Schema migrations with Liquibase**: one master changelog
  (`src/main/resources/db/changelog/db.changelog-master.xml`) includes one XML file per change,
  named `<change>-v<release>.xml`. v1.0.0 is the baseline; later releases add columns with a
  backfill and an explicit rollback (for example `add-expense-title-v1.2.0.xml`).
- **Money**: each trip stores an ISO 4217 currency code (default INR); expenses use the trip's
  currency unless told otherwise.

## Project structure

```
src/main/java/com/travelplanner/travel_planner_api/
  config/       security configuration, CSRF handler, user lookup for authentication
  controller/   REST controllers and ApiExceptionHandler
  dto/          request/response classes (with validation annotations)
  entities/     JPA entities and enums
  jwt/          JWT creation/validation, auth cookie helpers, JWT filter
  mapper/       MapStruct mappers between entities and DTOs
  repo/         Spring Data repositories
  service/      business logic, ownership checks, cross-field validation
src/main/resources/
  application.properties     shared settings (no secrets)
  local.properties.example   template for local secrets
  db/changelog/              Liquibase master changelog and change files
```

## Roadmap

- [ ] Tests (unit and integration)
- [ ] Packing list and shared-trip endpoints (invite acceptance, invite emails)
- [ ] OpenAPI/Swagger docs, pagination, login rate limiting
- [ ] Refresh tokens
