# Library Testing Project

## Overview
This is a didactic Spring Boot project designed to teach unit testing with JUnit 5 and Mockito to university students.

## Domain
Digital Library System - Manages books, users, and loans with complete business logic and exception handling.

## Technologies
- Java 21
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- JUnit 5
- Mockito

## Project Structure
```
library-testing/
├── src/
│   ├── main/
│   │   ├── java/co/edu/uniremington/library/
│   │   │   ├── LibraryApplication.java
│   │   │   ├── domain/
│   │   │   │   ├── model/ (Book, User, Loan)
│   │   │   │   └── exception/ (6 custom exceptions)
│   │   │   ├── repository/ (BookRepository, UserRepository, LoanRepository)
│   │   │   ├── service/ (LibraryService + DTOs)
│   │   │   ├── controller/ (LibraryController)
│   │   │   └── config/ (GlobalExceptionHandler)
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/co/edu/uniremington/library/
│           ├── service/ (LibraryServiceTest)
│           ├── controller/ (LibraryControllerTest)
│           └── config/ (GlobalExceptionHandlerTest)
└── pom.xml
```

## How to Run
```bash
mvn spring-boot:run
```

## How to Test
```bash
mvn test
```

## API Endpoints
- `POST /api/library/loans` - Create a new loan
- `PUT /api/library/loans/{id}/return` - Return a book
- `GET /api/library/books/available` - Get available books
