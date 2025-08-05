Padel Court Management System - Technical Report
1. Introduction

This document contains the relevant design and implementation aspects of the Padel Court Management System project. The project is a web application that allows users to create and manage padel clubs, courts, and rentals. It features a RESTful API backend built with Kotlin and Http4k, and a Single Page Application (SPA) for the user interface.

The system supports data persistence through both an in-memory solution for testing and a PostgreSQL database for production. It is designed for deployment using Docker and is hosted on the Render Platform-as-a-Service (PaaS). This report details the system's architecture, from database modeling and backend implementation to the frontend SPA and final deployment strategy.
2. System Architecture
   2.1. Database Modeling
   2.1.1. Conceptual Model

The following diagram holds the Entity-Relationship model for the information managed by the system.

Key aspects of the model:

    Users can own multiple clubs.
    Clubs can have multiple courts.
    Courts can have multiple rentals.
    Rentals are associated with a user and a court.
    The model enforces uniqueness for user emails and club names, and court names within a club.
    Rental times for the same court cannot overlap.

2.1.2. Physical Model

The physical model of the database is available in the createSchema.sql file. We highlight the following aspects of this model:

    User Authentication: The Users table includes a password column to store hashed passwords and a token column of type uuid for session management.
    Referential Integrity: Foreign key constraints are used to ensure data consistency between tables.
    Indexes: Indexes are used to improve query performance on frequently accessed columns.

2.2. Backend Architecture

The backend is built using Kotlin and the Http4k library, following a layered architecture.
2.2.1. Request Lifecycle

A request goes through the following components:

    An HTTP request is received by the Http4k server.
    The request is routed to the appropriate handler based on its path and method.
    Middleware is applied. This includes an exceptionHandler for centralized error management and a userTokenConverter for routes requiring authentication. The userTokenConverter validates the bearer token and injects the user's ID into the request for further processing.
    The API Handler (e.g., ClubsAPI, RentalsAPI) extracts parameters from the request.
    The handler calls the appropriate Service method (e.g., ClubsServices, RentalsServices), which contains the core business logic.
    The service validates the input and calls the corresponding Data Access method.
    The data access layer (UsersData, ClubsData, etc.) interacts with the database.
    The result is returned up the chain, and the API handler formats the data into a JSON response.

2.2.2. API Specification

The RESTful API is documented in the API-docs.yaml file using the OpenAPI 3.0 standard. Key endpoints include:

    User Management: POST /users (create), POST /users/login (authenticate), GET /users, GET /users/{id}.
    Club Management: POST /clubs (create), GET /clubs, GET /clubs/{cid}.
    Court Management: POST /clubs/{cid}/courts (create), GET /clubs/{cid}/courts, GET /clubs/{cid}/courts/{crid}.
    Rental Management: POST /rentals (create), GET /rentals/{cid}/{crid}, PUT /rental/{rid}, DELETE /rental/{rid}/delete.

2.2.3. Data Access & Connection Management

The data access layer is abstracted through interfaces (UsersData, ClubsData, etc.), with two implementations:

    In-Memory (DataMem): A simple in-memory object for testing and local development without a database.
    PostgreSQL (DataPostgres): The production implementation that connects to a PostgreSQL database.

For production, database connections are managed by the DataPostgres object. It uses a PGSimpleDataSource configured via a JDBC_DATABASE_URL environment variable. Transactions are managed manually; autoCommit is set to false, and operations are wrapped in a useWithRollback utility function to ensure atomicity.
2.2.4. Error Handling

A set of custom exceptions, inheriting from AppException, is used for handling business logic errors. A dedicated middleware (exceptionHandler) catches these exceptions and translates them into appropriate HTTP status codes and response bodies, ensuring consistent error reporting across the API.

    AppException.NotFound -> 404 Not Found
    AppException.InvalidData -> 400 Bad Request
    AppException.Conflict -> 409 Conflict
    AppException.Unauthorized -> 401 Unauthorized

3. Single Page Application (SPA)

A Single Page Application provides a web user interface for the system. It is built with vanilla JavaScript and uses a client-side routing approach.
3.1. Architecture

    index.html: The main HTML file that serves as the entry point.
    router.js: Handles client-side routing by parsing the URL hash (#).
    handlers/: Modules that fetch data from the API and render the appropriate views for each route.
    views/: Modules responsible for generating the HTML structure of the application.
    elementDsl.js: A small DSL used to create HTML elements programmatically.
    components.js: Reusable UI components like tables, pagination controls, and breadcrumbs.

3.2. Features & Navigation

    User Authentication: The SPA includes views for Sign Up and Login. Upon successful login, an authentication token is stored in sessionStorage and attached to subsequent API requests. A Logout function clears this token.
    CRUD Operations: Users can view lists of clubs, courts, and rentals. Authenticated users can create clubs and courts, and manage their rentals (create, update, delete).
    Navigation: The application features a clear navigation flow, with a main navigation bar and a breadcrumb component in each view for easy traversal of the application's hierarchy.

4. Deployment

The application is hosted on Render, a Platform-as-a-Service provider.

    PostgreSQL Database: A PostgreSQL instance was created on Render. The database schema was initialized using the createSchema.sql script.
    Containerization: A Dockerfile is provided to build a container image of the application. This image is pushed to Docker Hub.
    Web Service: A Render Web Service was created to deploy the Docker image.
    Environment Variables: The service is configured with a JDBC_DATABASE_URL to connect to the managed database and listens on the port specified by the PORT environment variable.

To launch the application locally using Docker, the following command can be used:
Bash

    docker run -d -p 9000:8080 --env PORT=8080 \
    --env JDBC_DATABASE_URL="jdbc:postgresql://host.docker.internal/<database>?user=<username>&password=<password>" \
    <docker-username>/img-ls-2425-2-<turma>-g<número-do-grupo>

5. Critical Evaluation

The project successfully meets all functional requirements. The backend provides a robust and well-documented RESTful API, and the SPA offers a user-friendly interface for interacting with the system's core features. The separation of concerns between the data, service, and API layers, along with the dual data-source implementation, provides a flexible and maintainable architecture.

Future improvements could include:

    Enhancing SPA form validation and providing more detailed user feedback.
    Implementing more comprehensive test coverage, especially for frontend components.
    Improving UI/UX with more advanced styling and interactive elements.
    Adding more sophisticated filtering and sorting options to the API list endpoints.