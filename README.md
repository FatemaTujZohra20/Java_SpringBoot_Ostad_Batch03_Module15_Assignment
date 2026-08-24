# Secure Note-Taking Application

A multi-user note-taking REST API built with Spring Boot 3 and Spring Security 6. Users register, authenticate over HTTP Basic, and manage notes that are scoped to their own account. Administrators get oversight across every note in the system.

The core of the project is **role-based access control combined with owner-level data isolation** — a `USER` cannot read another user's note even by guessing its ID, because ownership is enforced in the query itself rather than checked after the fact.

> Ostad Batch 03 — Module 15 Assignment

---

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.11 |
| Security | Spring Security 6 (HTTP Basic + BCrypt) |
| Persistence | Spring Data JPA (Hibernate) |
| Database | H2 (file-based) |
| Validation | Jakarta Bean Validation |
| Boilerplate | Lombok |
| Testing | Spring Boot Test, Spring Security Test |
| Build | Maven (wrapper included) |

---

## Getting Started

### Prerequisites

- JDK 21 or newer
- No Maven install required — use the bundled wrapper

### Run

```bash
git clone https://github.com/FatemaTujZohra20/Secure-Note-Taking-Application-Java_SpringBoot_Ostad_Batch03_Module15_Assignment.git
cd Secure-Note-Taking-Application-Java_SpringBoot_Ostad_Batch03_Module15_Assignment

# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The API starts on **http://localhost:8080**.

### Build a JAR

```bash
./mvnw clean package
java -jar target/secure_note_taking_app-0.0.1-SNAPSHOT.jar
```

### Database

H2 runs in **file mode**, so data survives restarts. The database files live in `./data/`.

H2 Console: **http://localhost:8080/h2-console**

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/secure_note` |
| Username | `sa` |
| Password | *(empty)* |

Useful queries:

```sql
SELECT * FROM USER_ENTITY;  -- registered accounts and BCrypt hashes
SELECT * FROM NOTE;         -- all notes with their owner_username
```

---

## Security Model

### Authentication

Registration hashes the incoming password with **BCrypt** before persisting; the raw password is never stored. On each subsequent request, `CustomUserDetailsService` loads the account by username and hands Spring Security a `UserDetails` carrying the stored hash and the user's authority.

Authentication is **HTTP Basic** — send credentials on every protected request. In Postman, set the *Authorization* tab to *Basic Auth*.

### Authorization

Roles are persisted with the `ROLE_` prefix already applied (`ROLE_USER`, `ROLE_ADMIN`) and mapped straight to authorities, which is what lets `hasRole("USER")` resolve correctly in the filter chain.

| Path | Access |
|---|---|
| `/api/auth/register/**` | Public |
| `/api/notes/**` | `ROLE_USER` |
| `/api/admin/**` | `ROLE_ADMIN` |
| `/h2-console/**` | Public *(development convenience)* |
| everything else | Authenticated |

### Owner-level isolation

Role checks alone would still let one `USER` read another `USER`'s notes. This project closes that gap at the repository layer:

```java
List<Note> findByOwnerUsername(String username);
Optional<Note> findByIdAndOwnerUsername(Long id, String username);
```

Every read is filtered by the authenticated principal, injected via `@AuthenticationPrincipal`. A note that exists but belongs to someone else is simply not returned. `GET /api/notes/{id}` responds `403 Forbidden` for both "no such note" and "not your note" — deliberately identical, so the response cannot be used to enumerate which IDs exist.

### Configuration notes

- **CSRF disabled** — acceptable for a stateless API consumed by non-browser clients; it would need reinstating for any cookie-based browser session.
- **Frame options set to `sameOrigin`** — required for the H2 Console to render, since Spring Security defaults to `DENY`.
- **Security debug logging enabled** via `logging.level.org.springframework.security=DEBUG`, which makes the filter chain and authorization decisions visible during development.

---

## Data Model

### `UserEntity`

| Field | Type | Constraints |
|---|---|---|
| `id` | `Long` | Auto-generated (identity) |
| `username` | `String` | Unique, not null |
| `password` | `String` | Not null, BCrypt hash |
| `role` | `String` | Not null, `ROLE_USER` or `ROLE_ADMIN` |

### `Note`

| Field | Type | Constraints |
|---|---|---|
| `id` | `Long` | Auto-generated (identity) |
| `title` | `String` | Must not be blank |
| `content` | `String` | Must not be blank |
| `ownerUsername` | `String` | Not null, set server-side from the authenticated principal |

`ownerUsername` is assigned by the service from the security context — it is never read from the request body, so a client cannot create a note on someone else's behalf.

---

## API Reference

### Registration — public

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register/user` | Create an account with `ROLE_USER` |
| `POST` | `/api/auth/register/admin` | Create an account with `ROLE_ADMIN` |

```bash
curl -X POST http://localhost:8080/api/auth/register/user \
  -H "Content-Type: application/json" \
  -d '{"username": "student", "password": "password123"}'
```

```
User registered successfully with ROLE_USER!
```

The role is assigned server-side regardless of what the request body contains. If the username is taken, the response is `Error: Username is already taken!`.

### Notes — `ROLE_USER`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/notes` | Create a note owned by the caller |
| `GET` | `/api/notes` | List only the caller's notes |
| `GET` | `/api/notes/{id}` | Fetch one of the caller's notes |

```bash
curl -X POST http://localhost:8080/api/notes \
  -u student:password123 \
  -H "Content-Type: application/json" \
  -d '{"title": "My First Note", "content": "This is private content."}'
```

```json
{
  "id": 1,
  "title": "My First Note",
  "content": "This is private content.",
  "ownerUsername": "student"
}
```

```bash
curl -u student:password123 http://localhost:8080/api/notes
```

### Administration — `ROLE_ADMIN`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/admin/notes` | List every note in the system |
| `DELETE` | `/api/admin/notes/{id}` | Delete any note |

```bash
curl -u professor:adminpassword http://localhost:8080/api/admin/notes
```

### Status codes

| Situation | Status |
|---|---|
| Success | `200 OK` |
| Missing or bad credentials | `401 Unauthorized` |
| Authenticated but wrong role | `403 Forbidden` |
| Note absent or owned by another user | `403 Forbidden` |

---

## Testing Flow

A full walkthrough — register both roles, then exercise the user and admin endpoints, then verify persistence in the H2 Console — is documented in [`README.md`](src/main/java/com/assignment15/secure_note_taking_app/README.md) inside the package directory.

The quickest way to see the security model working:

1. Register `student` (USER) and `professor` (ADMIN).
2. As `student`, create a note and list notes — the note appears.
3. As `professor`, call `/api/notes` — `403`, because ADMIN lacks `ROLE_USER`.
4. As `student`, call `/api/admin/notes` — `403`, because USER lacks `ROLE_ADMIN`.
5. Register a second user, create a note, then try to read the first user's note by ID — `403`.

Run the test suite with:

```bash
./mvnw test
```

---

## Project Structure

```
src/main/java/com/assignment15/secure_note_taking_app/
├── SecureNoteTakingAppApplication.java   # Entry point
├── config/
│   └── SecurityConfig.java               # Filter chain, RBAC rules, BCrypt bean
├── controllers/
│   ├── AuthController.java               # Public registration
│   ├── NoteController.java               # User-scoped note operations
│   └── AdminController.java              # Cross-user oversight
├── services/
│   ├── CustomUserDetailsService.java     # UserEntity → UserDetails
│   └── NoteService.java                  # Note operations + ownership scoping
├── repository/
│   ├── UserRepository.java
│   └── NoteRepository.java               # Owner-filtered finders
└── model/
    ├── UserEntity.java
    └── Note.java

src/main/resources/
└── application.properties                # H2 file DB, JPA, security logging

data/                                     # H2 database files (created at runtime)
```

---

## Roadmap

Known gaps and the intended next steps:

- **Add `.gitignore`** for `target/` and `data/` — the H2 database file is currently tracked in version control
- **Restrict `/h2-console`** to a `dev` profile rather than exposing it publicly
- **Apply `@Valid`** on the note and registration request bodies so the existing `@NotBlank` constraints are actually enforced
- **Introduce DTOs** for registration and notes, keeping `UserEntity` (which holds the password hash) off the API surface
- **Correct `UserRepository`** to `JpaRepository<UserEntity, Long>` to match the entity's `Long` identifier
- **Add a `@RestControllerAdvice`** so failures return structured JSON with accurate status codes instead of `500`
- **Expose `DELETE /api/notes/{id}`**, wiring up the ownership-checked delete already present in `NoteService`
- **Standardize on constructor injection** across all controllers and services
- **Migrate to JWT** for stateless authentication in place of HTTP Basic
- **Externalize datasource credentials** and add a PostgreSQL profile
- **Add integration tests** using `spring-security-test` (`@WithMockUser`) to lock in the access-control rules

---

## License

Released for educational purposes as part of the Ostad Spring Boot curriculum.
