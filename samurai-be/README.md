# SamurAI Backend
# SamurAI Backend

Backend module for SamurAI - an AI-driven document search and retrieval system. Built with Spring Boot, Spring AI, and Apache Tika.

## Project Structure

- `/src/main/java/pl/ochnios/samurai`
  - `/commons` - Utility classes
  - `/config` - Application configuration
  - `/controllers` - REST API endpoints
  - `/model` - Domain models and DTOs
  - `/repositories` - Data access layer
  - `/services` - Business logic and services

## Key Features

- Modern [Spring Boot](https://github.com/spring-projects/spring-boot) with Java 21
- AI integration with [Spring AI](https://github.com/spring-projects/spring-ai)
- Document processing with [Apache Tika](https://github.com/apache/tika)
- Vector database [Qdrant](https://github.com/qdrant/qdrant) for semantic search (possible to use other providers)
- OpenAI integration for chat and embeddings (possible to use other providers)
- JWT-based authentication
- API documentation with [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi)

## Prerequisites

- Java 21
- PostgreSQL 15+
- Qdrant vector database
- OpenAI API key

## Environment Setup

Create environment variables or update `application-local.yml`:
```bash
export PRIMARY_DB_HOST=postgres_host
export PRIMARY_DB_PORT=postgres_port
export PRIMARY_DB_NAME=postgres_db_name
export PRIMARY_DB_USER=postgres_user_name
export PRIMARY_DB_PASSWORD=postgres_password
export QDRANT_HOST=qdrant_host
export QDRANT_PORT=qdrant_port
export OPENAI_API_KEY=openai_api_key
export JWT_PASSPHRASE=jwt_passphrase
```

## Development

1. Install dependencies:

```bash
./gradlew build
```

2. Run application:
Make sure that you have a running postgres database and qdrant vector database.

```bash
./gradlew bootRun
```
After application starts:
- Data seeder will seed some basic data to the database along with sample users. You can change this behavior in `application-local.yml`.
- You can access the API documentation at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).
- You can login to the application using one of the sample users: user:user, mod:mod, admin:admin.

3. Run tests:

```bash
./gradlew test
```

## Code formatting

Project is formatted with
[spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle#java)
and
[palantir-java-format](https://github.com/palantir/palantir-java-format).

Run `./gradlew spotlessApply` to apply formatting
