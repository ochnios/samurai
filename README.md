# SamurAI application

## Key features

* Virtual assistant interface for document-based information retrieval using RAG pattern
* Natural language conversation capabilities for questions and answers based on the knowledge base
* Source-linked responses - each assistant's answer includes references to source documents
* Knowledge base management:
  * Support for various document formats (focusing on text content)
  * Document content editing capabilities
  * Document management by designated moderators
* User roles and permissions:
  * Regular users - can interact with the assistant
  * Moderators - can manage documents and view user conversations
  * Administrators - can manage user roles and system access
* Conversation monitoring tools for quality assurance

## Motivation

This project is a part of my engineering thesis. The goal was to create some useful system using new technologies and learn as much as possible.

## Running the application with Docker

Create `.env` file based on `.env.example` file.
Run the application using docker using the following command:

```bash
docker compose -f docker-compose-local.yml up --build
```

It will build the application and run it in docker containers along with all necessary dependencies .

App frontend will be available at [http://localhost](http://localhost).

By default there should be some sample data in the database - you can login to the application using one of the sample users: user:user, mod:mod, admin:admin.

## Backend

See [samurai-be/README.md](samurai-be/README.md) for more information.

## Frontend

See [samurai-fe/README.md](samurai-fe/README.md) for more information.
