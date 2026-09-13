# Data Flow Diagrams (DFD)
## Smart Queue Management System

---

## 1. Level-0 DFD (Context Diagram)

```mermaid
graph TD
    User["USER (Student/Visitor)"] -->|Registration Request / Login Credentials / Join Queue Request / Cancel Token| System["(0.0) Smart Queue Management System"]
    Admin["ADMIN (Counter Staff)"] -->|Login Credentials / Add & Edit Services / Call Next / Complete / Skip| System

    System -->|Token Status / Waiting Time / Queue History| User
    System -->|Queue Statistics / Today Log / Live Counter State| Admin
    System -->|Live Now Serving & Next Tokens (AJAX)| LobbyDisplay["Public Lobby TV Display"]
```

---

## 2. Level-1 DFD (Detailed Process Breakdown)

```mermaid
graph TD
    %% Entities
    User["USER"]
    Admin["ADMIN"]

    %% Processes
    P1["(1.0) Authentication & Registration"]
    P2["(2.0) Service Management"]
    P3["(3.0) Token Generation & Queue Joining"]
    P4["(4.0) Status & Wait Time Calculation"]
    P5["(5.0) Admin Queue Operations"]

    %% Data Stores
    D1[("D1: Users Store")]
    D2[("D2: Services Store")]
    D3[("D3: Queue Entries Store")]

    %% Flows
    User -->|Register / Login Info| P1
    P1 -->|Check / Save User| D1
    P1 -->|Session Token| User

    Admin -->|Add / Toggle Services| P2
    P2 -->|Save Service Data| D2

    User -->|Select Service & Join Queue| P3
    D2 -->|Fetch Active Services| P3
    P3 -->|Validate & Generate Token| D3
    P3 -->|Token Number| User

    D3 -->|Fetch Active Entries & Join Times| P4
    D2 -->|Fetch Avg Service Time| P4
    P4 -->|People Ahead & Wait Time| User

    Admin -->|Call Next / Complete / Skip| P5
    P5 -->|Update Token Status & Timestamps| D3
    P5 -->|Live Broadcast State| Display["Public Lobby Display"]
```

---

## 3. Data Stores Description

| Data Store | Entity Name | Stored Information | Primary Key |
|---|---|---|---|
| D1 | Users Store (`users`) | User ID, Full Name, Username, Email, Encrypted Password, Phone, Role, Created Timestamp | `id` |
| D2 | Services Store (`services`) | Service ID, Name, Description, Avg Service Time, Active Flag | `id` |
| D3 | Queue Entries Store (`queue_entries`) | Queue ID, Token Number, User ID (FK), Service ID (FK), Queue Date, Join Time, Called Time, Completed Time, Status | `id` |
