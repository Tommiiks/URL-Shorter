# url-shorter

API REST per accorciare URL. Spring Boot 4 + PostgreSQL + JWT auth.

Ti registri, ricevi un token JWT e da li puoi creare, modificare, eliminare i tuoi short link. Supporta alias custom, scadenza e contatore accessi.
I redirect sono pubblici (no auth necessaria).

## Stack

Java 25, Spring Boot 4.0.5, Spring Security, Spring Data JPA, PostgreSQL, jjwt 0.13, Lombok, Maven.

## Setup

Requisiti: JDK 25+, PostgreSQL su `localhost:5432` con un db chiamato `urlshorter`.

```bash
git clone https://github.com/Tommiiks/url-shorter.git
cd url-shorter
./mvnw spring-boot:run
```

Credenziali DB e JWT secret stanno in `src/main/resources/application.yaml`. Il server parte sulla 8080.

## API

Auth (`/api/v1/auth`):

- `POST /register` - registrazione, torna direttamente il token
- `POST /login` - login

Body: `{ "username": "...", "password": "..." }`

Short URLs (`/api/v1/urls`) - servono tutti il token nel header `Authorization: Bearer <token>`:

- `POST /` - crea short link (body: `originalUrl` obbligatorio, `customAlias` e `expiresAt` opzionali)
- `GET /` - lista paginata dei tuoi link
- `GET /{shortCode}` - dettaglio
- `PUT /{shortCode}` - modifica
- `DELETE /{shortCode}` - elimina (owner o admin)
- `GET /{shortCode}/stats` - statistiche

Redirect (`/api/v1/redirect`):

- `GET /{shortCode}` - redirect 302, pubblico
