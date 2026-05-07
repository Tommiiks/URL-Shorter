# url-shorter

REST API for shortening URLs. Spring Boot + PostgreSQL + JWT auth.

Register, get a JWT token, then create, update and delete your short links.
Supports custom aliases, expiration and access tracking.
Redirects are public, no auth needed.

## Setup

Requirements: JDK 25+, PostgreSQL on `localhost:5432` with a database named `urlshorter`.

```bash
git clone https://github.com/Tommiiks/url-shorter.git
cd url-shorter
./mvnw spring-boot:run
```

DB credentials and JWT secret go in `src/main/resources/application.yaml`. Server runs on port 8080.

## API

Auth (`/api/v1/auth`):

- `POST /register` — register, returns the token directly
- `POST /login` — login

Body: `{ "username": "...", "password": "..." }`

Short URLs (`/api/v1/urls`) — all require `Authorization: Bearer <token>`:

- `POST /` — create short link (`originalUrl` required, `customAlias` and `expiresAt` optional)
- `GET /` — paginated list of your links
- `GET /{shortCode}` — details
- `PUT /{shortCode}` — update
- `DELETE /{shortCode}` — delete (owner or admin)
- `GET /{shortCode}/stats` — stats

Redirect (`/api/v1/redirect`):

- `GET /{shortCode}` — 302 redirect, public
