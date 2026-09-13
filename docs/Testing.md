# Software Testing Specification & Execution Log
## Smart Queue Management System

---

## 1. Test Strategy Overview
The testing strategy covers Unit Testing, Integration Testing, Data Persistence Verification, and Constraint Violation Handling for the Smart Queue Management System.

---

## 2. Test Execution Matrix

| Test Case ID | Feature / Module | Test Scenario | Expected Outcome | Execution Result |
|---|---|---|---|---|
| TC-01 | Registration | Valid registration details submitted | User saved to MySQL, password BCrypt hashed, redirected to `/login` | PASS |
| TC-02 | Registration | Duplicate username `ashutosh123` | Exception caught gracefully, error message displayed on UI without SQL stack trace | PASS |
| TC-03 | Registration | Duplicate email `ashutosh@gmail.com` | Exception caught gracefully, error message displayed on UI without SQL stack trace | PASS |
| TC-04 | Registration | Password and Confirm Password mismatch | Validation error displayed on register form | PASS |
| TC-05 | Login | Valid user credentials | Authenticates via Spring Security BCrypt check, redirects to `/dashboard` | PASS |
| TC-06 | Login | Invalid password / username | Error parameter set, displays *"Invalid username/email or password."* | PASS |
| TC-07 | Join Queue | First queue token request for Admission | Token `ADM-001` generated, entry status `WAITING` persisted in MySQL | PASS |
| TC-08 | Join Queue | Attempt to join Admission queue again while already WAITING | Prevented, displays *"You are already in this queue."* | PASS |
| TC-09 | Queue Position | Calculate people ahead and wait time | `peopleAhead` = count of waiting before user, `estimatedWaitTime` = `peopleAhead * avgTime` | PASS |
| TC-10 | Cancel Queue | Student cancels active token `ADM-001` | Status updated to `CANCELLED` in MySQL; record preserved in DB history | PASS |
| TC-11 | Admin Call Next | Click **CALL NEXT TOKEN** | Earliest `WAITING` token changed to `SERVING`, timestamp recorded | PASS |
| TC-12 | Admin Complete | Click **COMPLETE** | Current `SERVING` token changed to `COMPLETED`, timestamp recorded | PASS |
| TC-13 | Admin Skip | Click **SKIP** | Token status updated to `SKIPPED` | PASS |
| TC-14 | Lobby Display | Public lobby TV screen `/queue-display` | Renders Now Serving & Next Tokens; polls AJAX `/api/public/queue-status` every 3s | PASS |
| TC-15 | Persistence | Restart Spring Boot Application | User accounts and queue records persist intact in MySQL database | PASS |

---

## 3. Automated Test Command Execution
Automated unit and integration test suite can be executed using Maven:
```bash
mvn clean test
```
