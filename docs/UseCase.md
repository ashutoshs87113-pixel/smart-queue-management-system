# Use Case Specification
## Smart Queue Management System

---

## 1. Actors
- **USER (Student / Visitor)**: Standard system user who registers, logs in, joins service queues, views token status, and cancels queue tokens.
- **ADMIN (Counter Officer / Staff)**: Administrative user who manages college services, calls next tokens, completes or skips tokens, and views system statistics.

---

## 2. Use Case Summary Table

| Use Case ID | Use Case Name | Primary Actor | Description |
|---|---|---|---|
| UC-01 | Register Account | USER | User registers a new account with full name, username, email, phone, and password. |
| UC-02 | Login | USER / ADMIN | Authenticates credentials and redirects to dashboard based on role. |
| UC-03 | View Services | USER | Browse available active college services and average service times. |
| UC-04 | Join Queue | USER | Request a new digital token for an active college service today. |
| UC-05 | View Queue Position | USER | Track token status, currently serving token, people ahead, and estimated wait time. |
| UC-06 | Cancel Queue | USER | Cancel an active token before being called. |
| UC-07 | View Queue History | USER | View past queue tokens and completion statuses. |
| UC-08 | Manage Services | ADMIN | Add, edit, activate, or deactivate college services. |
| UC-09 | Call Next Token | ADMIN | Advance the earliest waiting token for a service to SERVING status. |
| UC-10 | Complete Token | ADMIN | Mark the currently serving token as COMPLETED with completion timestamp. |
| UC-11 | Skip Token | ADMIN | Skip an active or waiting token if student is absent. |
| UC-12 | View Statistics | ADMIN | View dashboard metrics (total users, services, waiting, serving, completed, cancelled). |
| UC-13 | View Public Display | PUBLIC / ALL | Open public lobby counter screen showing live serving and upcoming tokens. |

---

## 3. Detailed Use Case Scenarios

### UC-04: Join Queue
- **Primary Actor**: USER
- **Preconditions**: User is logged in and on the Dashboard.
- **Main Success Scenario**:
  1. User selects an active service (e.g. Admission) and clicks **JOIN QUEUE**.
  2. System checks if user has an active token for this service today.
  3. System generates next token (e.g. `ADM-001`).
  4. System persists `QueueEntry` with status `WAITING`.
  5. System calculates people ahead and estimated wait time.
  6. User sees success banner with token number.
- **Alternative Flow (Duplicate Entry)**:
  - If user is already in queue for that service today, system displays: *"You are already in this queue."*

### UC-09: Call Next Token
- **Primary Actor**: ADMIN
- **Preconditions**: Admin is logged in and on Live Counter Control Console.
- **Main Success Scenario**:
  1. Admin clicks **CALL NEXT TOKEN** for a service counter.
  2. System marks any existing `SERVING` token as `COMPLETED`.
  3. System fetches the earliest `WAITING` token for that service today.
  4. System updates its status to `SERVING` and sets `calledTime` to current timestamp.
  5. System saves update to MySQL and updates public lobby display.
