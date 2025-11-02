# CodeWordle - Word Guessing Game 🎮

A Spring Boot-based word guessing game similar to Wordle, but focused on programming and technology terms. Players have 6 attempts to guess a word related to different tech themes.

## 📋 Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Main Endpoints](#main-endpoints)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Game Rules](#game-rules)
- [Database Schema](#database-schema)

## ✨ Features

- **Theme-based gameplay**: Choose from different technology themes (Java, Spring, DevOps, etc.)
- **Word feedback system**: Get real-time feedback on your guesses
  - Green (2): Correct letter in correct position
  - Yellow (1): Correct letter in wrong position
  - Gray (0): Letter not in the word
- **Attempt tracking**: Maximum 6 attempts per game
- **Game state management**: Track wins, losses, and in-progress games
- **RESTful API**: Clean REST endpoints for game operations
- **Persistent storage**: H2 database for data persistence
- **Swagger UI**: Interactive API documentation

## 🛠 Technologies

- **Java**: 21
- **Spring Boot**: 3.5.7
- **Spring Data JDBC**: For database operations
- **H2 Database**: In-memory/file-based SQL database
- **Lombok**: To reduce boilerplate code
- **SpringDoc OpenAPI**: API documentation (Swagger UI)
- **JUnit 5**: Unit and integration testing
- **Mockito**: Mocking framework for tests

## 📦 Prerequisites

Before running this application, make sure you have:

- **JDK 21** or higher installed
- **Maven 3.6+** installed
- **Git** (optional, for cloning the repository)

## 🚀 Installation

1. **Clone the repository**:
```bash
git clone https://github.com/SpringBoot-Mocha/week2-codewordle.git
cd week2-codewordle
```

2. **Build the project**:
```bash
mvn clean install
```

3. **Run tests** (optional):
```bash
mvn test
```

## ▶️ Running the Application

### Using Maven:
```bash
mvn spring-boot:run
```

### Using Java:
```bash
mvn clean package
java -jar target/codewordle-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## 📚 API Documentation

Once the application is running, you can access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔌 Main Endpoints

### Game Management

#### Create a New Game
```http
POST /api/games?themeId={themeId}
```
**Response**:
```json
{
  "id": 1,
  "themeId": 1,
  "wordId": 5,
  "maxAttempts": 6,
  "currentAttempts": 0,
  "status": "IN_PROGRESS",
  "won": false,
  "startedAt": "2024-11-01T10:00:00"
}
```

#### Make a Guess
```http
POST /api/games/{gameId}/guess
Content-Type: application/json

{
  "word": "JAVA"
}
```
**Response**:
```json
{
  "id": 1,
  "gameId": 1,
  "guessedWord": "JAVA",
  "feedback": "2222",
  "isCorrect": true,
  "attemptedAt": "2024-11-01T10:05:00"
}
```

#### Get Game by ID
```http
GET /api/games/{id}
```

#### Get All Games
```http
GET /api/games
```

### Theme Management

#### Get All Themes
```http
GET /api/themes
```
**Response**:
```json
[
  {
    "id": 1,
    "name": "JAVA",
    "description": "Java programming terms"
  },
  {
    "id": 2,
    "name": "SPRING",
    "description": "Spring Framework concepts"
  }
]
```

#### Get Theme by ID
```http
GET /api/themes/{id}
```

### Word Management

#### Get All Words
```http
GET /api/words
```

#### Get Random Word by Theme
```http
GET /api/words/random?themeId={themeId}
```

### Attempt History

#### Get Attempts by Game
```http
GET /api/attempts/game/{gameId}
```

## 🧪 Testing

The project includes comprehensive test coverage:

### Test Statistics
- **Total Tests**: 46
- **Unit Tests**: 12 (GameServiceTest)
- **Integration Tests**: 34 (Repository tests)
- **Coverage**: All main components

### Test Structure

#### Unit Tests (with Mockito)
- `GameServiceTest`: Business logic validation
  - Game creation
  - Guess processing
  - Feedback generation
  - Game state management

#### Integration Tests (with H2)
- `GameRepositoryIntegrationTest`: Game CRUD operations
- `WordRepositoryIntegrationTest`: Word queries and random selection
- `ThemeRepositoryIntegrationTest`: Theme management
- `AttemptRepositoryIntegrationTest`: Attempt tracking and counting

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=GameServiceTest

# Run with coverage
mvn test jacoco:report
```

## 📁 Project Structure

```
codewordle/
├── src/
│   ├── main/
│   │   ├── java/com/riwi/codewordle/
│   │   │   ├── api/
│   │   │   │   ├── controllers/      # REST Controllers
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   └── error_handler/    # Global exception handling
│   │   │   ├── config/               # Configuration classes
│   │   │   ├── domain/
│   │   │   │   ├── entities/         # Domain models
│   │   │   │   └── enums/            # Enumerations
│   │   │   ├── repository/           # Data access layer
│   │   │   ├── service/              # Business logic
│   │   │   └── CodewordleApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── data.sql              # Initial data
│   │       ├── schema.sql            # Database schema
│   │       ├── static/               # Static resources
│   │       └── templates/            # JSP templates
│   └── test/
│       └── java/com/riwi/codewordle/
│           ├── repository/           # Integration tests
│           └── service/              # Unit tests
├── pom.xml
└── README.md
```

## 🎯 Game Rules

1. **Choose a Theme**: Select from available technology themes
2. **Start Guessing**: You have 6 attempts to guess the word
3. **Feedback System**:
   - **2 (Green)**: Letter is correct and in the right position
   - **1 (Yellow)**: Letter exists in the word but wrong position
   - **0 (Gray)**: Letter doesn't exist in the word
4. **Win Condition**: Guess the word within 6 attempts
5. **Loss Condition**: Exceed 6 attempts without guessing correctly

### Example Game Flow

```
Target Word: JAVA
Attempt 1: CAKE → Feedback: 0100 (A is in the word, wrong position)
Attempt 2: MARK → Feedback: 1100 (A is correct position)
Attempt 3: JAVA → Feedback: 2222 (Perfect! You won!)
```

## 🗄 Database Schema

### Tables

#### THEMES
```sql
CREATE TABLE themes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### WORDS
```sql
CREATE TABLE words (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(50) NOT NULL,
    theme_id BIGINT NOT NULL,
    hint VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (theme_id) REFERENCES themes(id)
);
```

#### GAMES
```sql
CREATE TABLE games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theme_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    target_word VARCHAR(50) NOT NULL,
    max_attempts INT DEFAULT 6,
    current_attempts INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    won BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,
    FOREIGN KEY (theme_id) REFERENCES themes(id),
    FOREIGN KEY (word_id) REFERENCES words(id)
);
```

#### ATTEMPTS
```sql
CREATE TABLE attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    guessed_word VARCHAR(50) NOT NULL,
    feedback VARCHAR(50) NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES games(id)
);
```

## 📊 Sample Themes and Words

### Java Theme
- JAVA, CLASS, ARRAY, INTERFACE, METHOD

### Spring Theme
- BEAN, CONTROLLER, SERVICE, REPOSITORY, AUTOWIRED

### DevOps Theme
- BUILD, NGINX, CACHE, PROXY, DOCKER, KUBERNETES

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is part of a Spring Boot learning exercise at RIWI.

## 👥 Author

- **Johan** - [SpringBoot-Mocha](https://github.com/SpringBoot-Mocha)

## 🙏 Acknowledgments

- RIWI for the project requirements
- Spring Boot community for excellent documentation
- Wordle for the game inspiration