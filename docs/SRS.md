# Software Requirements Specification (SRS)
## Smart Queue Management System

---

## 1. Introduction
### 1.1 Purpose
The purpose of this document is to provide a comprehensive Software Requirements Specification (SRS) for the **Smart Queue Management System**. This system aims to digitize line queue management in college administrative offices, reducing physical crowding and providing real-time visibility into queue positions and estimated wait times.

### 1.2 Scope
The scope of the project encompasses:
- User registration, authentication, and role-based authorization (USER and ADMIN).
- Digital service queue token generation with service-specific prefixes and daily token counters.
- User dashboard displaying active token, currently serving token, people ahead, and dynamic estimated wait time.
- Admin management console allowing staff to add/edit/deactivate services and control tokens (**Call Next**, **Complete**, **Skip**, **Cancel**).
- Public lobby TV counter display (`/queue-display`) with automated real-time AJAX updates.

---

## 2. Overall Description

### 2.1 Existing Problem
Traditional physical queues at college service counters result in long standing lines, crowding, inefficient counter utilization, lack of wait-time transparency, and student frustration.

### 2.2 Proposed Solution
A centralized digital web system where students register, join a service queue digitally from their device, receive a unique daily token (e.g., `ADM-001`), and track queue status live. Admin counter staff call and complete tokens systematically.

---

## 3. System Requirements

### 3.1 Functional Requirements
1. **User Registration & Login**: Validate fields, verify password matching, enforce unique username and email constraints, and hash passwords using BCrypt.
2. **Queue Joining**: Generate transaction-safe daily token numbers (e.g. `ADM-001`, `FEE-002`). Prevent duplicate active entries for the same service on the same day.
3. **Queue Status Calculation**: Dynamically compute `peopleAhead` = count of waiting entries before current user, and `estimatedWaitTime` = `peopleAhead * averageServiceTime`.
4. **Token Cancellation**: Allow users to cancel active tokens while maintaining historical database records with status `CANCELLED`.
5. **Admin Token Operations**: Support `CALL NEXT` (transition WAITING -> SERVING), `COMPLETE` (transition SERVING -> COMPLETED), `SKIP` (transition to SKIPPED).
6. **Service CRUD**: Admin can add, edit, activate, or deactivate services.
7. **Public Display**: Render lobby TV monitor screen (`/queue-display`) showing Now Serving token and upcoming tokens with 3-second AJAX polling.

### 3.2 Non-Functional Requirements
- **Security**: Role-based access control (RBAC), BCrypt password hashing, session management.
- **Performance**: Response time < 500ms for status inquiries; AJAX polling lightweight footprint.
- **Data Integrity**: Relational constraints, foreign keys, transaction boundaries (@Transactional).
- **Usability**: Responsive glassmorphic layout, Bootstrap 5 UI.

---

## 4. Hardware & Software Requirements

### 4.1 Hardware Requirements
- Processor: Intel Core i3 / Apple Silicon M1 or equivalent
- RAM: Minimum 4 GB (8 GB recommended)
- Disk Space: 500 MB for application binaries and database storage

### 4.2 Software Requirements
- Operating System: Cross-platform (macOS, Windows, Linux)
- Java SE JDK: Java 17 LTS or compatible newer version
- Web Framework: Spring Boot 3.3.3
- Database: MySQL 8+
- Build System: Apache Maven 3.9+
- Browser: Google Chrome, Mozilla Firefox, Safari, or Microsoft Edge
