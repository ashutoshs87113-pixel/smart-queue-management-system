# Smart Queue Management System

A full-stack, production-ready Java Spring Boot & MySQL web application designed for digital queue token management in college offices, administrative departments, and service counters.

## 📌 Project Overview
The **Smart Queue Management System** eliminates physical standing lines by allowing students and visitors to digitally register, join service queues from anywhere on campus, view live serving tokens, track people ahead, and receive estimated waiting times. College administrators and counter staff manage queues with one-click token controls (**Call Next**, **Complete**, **Skip**, **Cancel**), and lobby screens update live in real-time via an automated public queue display.

---

## 🛠️ Technology Stack
- **Backend Framework**: Spring Boot 3.3.3 (Java 17 LTS / Java 24)
- **Security**: Spring Security 6 with BCrypt Password Hashing
- **Persistence & ORM**: Spring Data JPA, Hibernate ORM
- **Database**: MySQL 8+ (`smart_queue_db`)
- **Frontend Template Engine**: Thymeleaf HTML5, CSS3, JavaScript (AJAX auto-refresh)
- **UI Framework**: Bootstrap 5 + Glassmorphism aesthetic cards
- **Build Tool**: Apache Maven 3.9+
- **Testing**: JUnit 5, Spring Boot Test, MockMvc

---

## ⚙️ System Requirements & MySQL Configuration

### 1. MySQL Setup
Ensure your MySQL server is running on `localhost:3306`.
Create database:
```sql
CREATE DATABASE IF NOT EXISTS smart_queue_db;
```

### 2. Configure Database Credentials
Edit `src/main/resources/application.properties` to match your local MySQL configuration if needed:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_queue_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

---

## 🔑 Default Admin Credentials
When the application starts, a default administrator account is seeded automatically:
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `ADMIN`

---

## 🚀 How to Run the Project

### 1. Build & Run via Maven
From the project root directory:
```bash
# Compile and package
mvn clean install

# Run Spring Boot application
mvn spring-boot:run
```

### 2. Access Web Application
Open your web browser and navigate to:
- **Home Page**: [http://localhost:8080/](http://localhost:8080/)
- **Register User**: [http://localhost:8080/register](http://localhost:8080/register)
- **Login**: [http://localhost:8080/login](http://localhost:8080/login)
- **User Dashboard**: [http://localhost:8080/dashboard](http://localhost:8080/dashboard)
- **My Queue History**: [http://localhost:8080/my-queue](http://localhost:8080/my-queue)
- **Public Counter Lobby TV Display**: [http://localhost:8080/queue-display](http://localhost:8080/queue-display)
- **Admin Control Panel**: [http://localhost:8080/admin](http://localhost:8080/admin)

---

## 🧪 How to Run Automated Tests
Run unit and integration tests using:
```bash
mvn clean test
```

---

## 📂 Project Structure
```
smart-queue-system/
├── pom.xml
├── README.md
├── docs/
│   ├── SRS.md
│   ├── UseCase.md
│   ├── DFD.md
│   ├── ERDiagram.md
│   ├── ClassDiagram.md
│   ├── SequenceDiagram.md
│   └── Testing.md
└── src/
    ├── main/
    │   ├── java/com/smartqueue/
    │   │   ├── config/          # SecurityConfig, DataInitializer
    │   │   ├── controller/      # Auth, Home, User, Admin controllers
    │   │   ├── dto/             # RegisterRequest, LoginRequest, QueueResponse, ServiceRequest
    │   │   ├── entity/          # User, ServiceEntity, QueueEntry, Enums
    │   │   ├── exception/       # Custom Exceptions & GlobalExceptionHandler
    │   │   ├── repository/      # UserRepository, ServiceRepository, QueueEntryRepository
    │   │   ├── security/        # CustomUserDetailsService
    │   │   └── service/         # UserService, QueueService, ServiceManagementService
    │   └── resources/
    │       ├── application.properties
    │       └── templates/       # Thymeleaf views (index, register, login, dashboard, admin/*)
    └── test/                    # JUnit 5 & MockMvc unit tests
```

---

## 🔮 Future Enhancements
- SMS / WhatsApp token status notification alerts
- QR-code scanning at campus entrance counters
- Multi-branch and multi-counter load balancing
- AI-driven predictive waiting time modeling
