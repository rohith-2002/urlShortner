URL Shortener — README

Quick start (local)

Prerequisites:
- Java 21 (or compatible JDK)
- Maven

1) Add DB credentials (recommended):
- Create src/main/resources/application-local.properties (gitignored) or set env vars:
  SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/urlshortnerdb
  DB_USER=your_user
  DB_PASSWORD=your_password

2) Run with local profile:
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

Or run with env vars set in the shell:
  SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/urlshortnerdb DB_USER=postgres DB_PASSWORD=pass ./mvnw spring-boot:run

API Endpoints & examples (curl)

1) Shorten a URL (auto-generated code):
curl -i -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/very/long/path"}'
# Expected: 201 JSON body with shortCode and shortUrl

2) Shorten with custom alias:
curl -i -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/other","customAlias":"my-link"}'
# Expected: 201 JSON with shortCode: "my-link"

3) Duplicate alias -> 409 Conflict
4) Invalid URL -> 400 Bad Request

5) Redirecting:
curl -i http://localhost:8080/q0
# Expected: 302 Found and Location header set to original URL

Running tests

mvn test

Notes
- Use application-local.properties for local secrets. This file is gitignored.
- For production, supply environment variables (or use Docker/Render setup described in project docs).
