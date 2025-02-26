# ticket-tracking (Incomplete)

## Project Overview

This project is a ticket tracking system with client and server components. The system allows users to create, update, and track support tickets. The client-side application is built using Java Swing, while the server-side application is built using Spring Boot.

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Gradle
- Oracle or PostgreSQL database

### Server Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/cozyCodr/ticket-tracking.git
   cd ticket-tracking
   ```

2. Configure the database connection in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:oracle:thin:@localhost:1521:XE
       username: your_db_username
       password: your_db_password
   ```

3. Run the database schema script located at `src/main/resources/schema.sql` to create the necessary tables.

4. Build and run the server:
   ```bash
   ./gradlew bootRun
   ```

### Client Setup

1. Build the client JAR file:
   ```bash
   ./gradlew clientJar
   ```

2. Run the client application:
   ```bash
   ./gradlew runClient
   ```

## Usage Examples

### Server

- The server application will be running at `http://localhost:8081/api/v1`.
- You can access the Swagger UI for API documentation at `http://localhost:8081/api/v1/swagger-ui.html`.

### Client

- The client application provides a graphical user interface for interacting with the ticket tracking system.
- You can use the client to create, update, and view tickets.

## Database Schema

The database schema is defined in the `src/main/resources/schema.sql` file. It includes the following tables:

- `audit_log`: Stores audit logs for various actions.
- `app_user`: Stores user information.
- `ticket`: Stores ticket information.
- `comment`: Stores comments on tickets.
- `ticket_comments`: Stores the relationship between tickets and comments.
- `user_comments`: Stores the relationship between users and comments.
- `app_user_comments`: Stores the relationship between app users and comments.

## Build and Run Scripts

The project includes a script for building and running the client application:

- `scripts/build_and_run_client.sh`: This script builds the client JAR file and runs the client application.
  ```bash
  ./scripts/build_and_run_client.sh
  ```
