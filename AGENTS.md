# Repository Guidelines

## Project Structure & Modules
- Root Gradle (Kotlin DSL): `build.gradle.kts`, `settings.gradle.kts`.
- Modules: `domain/`, `repository/`, `server/`.
- Server source: `server/src/main/kotlin` (Ktor app under package `co.hondaya`).
- Resources: `server/src/main/resources` (e.g., `application.yaml`, OpenAPI docs).
- Tests: `server/src/test/kotlin` (example: `ApplicationTest.kt`).
- Docs: `docs/` and `server/docs/` (generated Swagger/OpenAPI assets).

## Build, Test, and Run
- Build all modules: `./gradlew build` — compiles and runs tests.
- Run tests only: `./gradlew test` or per-module `./gradlew :server:test`.
- Run the server: `./gradlew :server:run` — starts Ktor Netty on port 8080.
- Clean: `./gradlew clean` — removes build outputs.

## Coding Style & Naming
- Language: Kotlin (JVM). Indentation: 4 spaces; UTF-8; no tabs.
- Packages: lowercase dot style (e.g., `co.hondaya`).
- Types: PascalCase (`UserService`); functions/props: lowerCamelCase; constants: UPPER_SNAKE_CASE.
- Files: match primary type or feature (e.g., `UsersSchema.kt`, `Routing.kt`).
- Format using Kotlin official style (IDE default). No linters are configured in Gradle.

## Testing Guidelines
- Frameworks: `kotlin.test` + JUnit; Ktor `testApplication` for HTTP.
- Location: `server/src/test/kotlin` (mirror package of code under test).
- Naming: `XxxTest.kt` with clear, single-responsibility tests.
- Run: `./gradlew test` or targeted `./gradlew :server:test`.

## Commits & Pull Requests
- Commits: follow Conventional Commits (e.g., `feat: ...`, `fix: ...`).
- PRs: include summary, linked issues, test instructions (e.g., `curl http://localhost:8080/`), and impact on docs.
- Keep diffs focused; include module prefix in title when helpful (e.g., `[server]` Routing cleanup).

## Security & Configuration
- App config: `server/src/main/resources/application.yaml` (port, modules). Avoid committing secrets.
- Defaults: server listens on `:8080`; Swagger/OpenAPI served under `/openapi`.
- Local DB: H2 in-memory via Exposed; data is ephemeral.

## Architecture Overview
- Ktor (Netty) server with modular setup: `configureSerialization`, `configureDatabases`, `configureHTTP`, `configureRouting`.
- Persistence via Exposed + H2; simple CRUD under `/users`.
- OpenAPI/Swagger UI enabled; update routes and docs together when endpoints change.

