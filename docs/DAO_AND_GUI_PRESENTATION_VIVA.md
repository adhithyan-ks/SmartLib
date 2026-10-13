# SmartLib: `dao` and `gui` Packages — Presentation & Viva Voce Guide

This document provides a comprehensive technical walkthrough of the **Data Access Object (`dao`)** and **Graphical User Interface (`gui`)** packages in **SmartLib**. It is designed specifically for project presentations, software architecture reviews, and academic viva voce examinations.

---

## Table of Contents

1. [High-Level Architectural Overview](#1-high-level-architectural-overview)
2. [The `dao` (Data Access Object) Package](#2-the-dao-data-access-object-package)
   - [Design Pattern & Core Purpose](#design-pattern--core-purpose)
   - [The Generic Base: `CrudDAO<T, ID>`](#the-generic-base-cruddaot-id)
   - [Exhaustive Breakdown of DAO Interfaces & Implementations](#exhaustive-breakdown-of-dao-interfaces--implementations)
   - [JDBC Internals & Database Safety Engineering](#jdbc-internals--database-safety-engineering)
3. [The `gui` (Graphical User Interface) Package](#3-the-gui-graphical-user-interface-package)
   - [Swing & AWT Architectural Roles](#swing--awt-architectural-roles)
   - [Master Controller & View Router: `MainApplication`](#master-controller--view-router-mainapplication)
   - [Styling Framework: `UITheme`](#styling-framework-uitheme)
   - [Table Interaction Engine: `TableSelectionHelper`](#table-interaction-engine-tableselectionhelper)
   - [The 7 Screen Panels Breakdown](#the-7-screen-panels-breakdown)
   - [Event Dispatch Thread (EDT) & Data Binding](#event-dispatch-thread-edt--data-binding)
4. [End-to-End Execution Traces (GUI ↔ Service ↔ DAO ↔ MySQL)](#4-end-to-end-execution-traces)
   - [Trace 1: Direct Book Issue](#trace-1-direct-book-issue)
   - [Trace 2: Direct Book Return & Overdue Fine Calculation](#trace-2-direct-book-return--overdue-fine-calculation)
   - [Trace 3: Student Catalog Search & Borrow Request](#trace-3-student-catalog-search--borrow-request)
5. [Presentation Script & Talking Points (Slide-by-Slide)](#5-presentation-script--talking-points)
6. [Academic Viva Voce Q&A (Top 15 Questions & Model Answers)](#6-academic-viva-voce-qa)

---

## 1. High-Level Architectural Overview

SmartLib adheres strictly to the **Multi-Tier Layered Architecture** with rigorous **Separation of Concerns (SoC)** and the **Dependency Inversion Principle (DIP)**:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                              │
│                           package: gui                                 │
│  MainApplication (JFrame + CardLayout), UITheme, TableSelectionHelper  │
│      7 Modular Panels: Login, Dashboard, Books, Students, etc.         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Method Invocations (DTOs / POJOs)
┌───────────────────────────────────▼────────────────────────────────────┐
│                      BUSINESS SERVICE LAYER                            │
│                         package: service                               │
│     BookService, StudentService, BorrowService, AuthService, etc.      │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Interface References (Polymorphism)
┌───────────────────────────────────▼────────────────────────────────────┐
│                    DATA ACCESS OBJECT LAYER                            │
│                      package: dao & dao.impl                           │
│   CrudDAO<T, ID>, BookDAO, StudentDAO, BorrowTransactionDAO, etc.      │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Parameterized SQL over TCP/IP
┌───────────────────────────────────▼────────────────────────────────────┐
│                       PERSISTENT DATABASE                              │
│                          MySQL (smartlib)                              │
│      7 Relational Tables: books, students, borrow_transactions, etc.   │
└────────────────────────────────────────────────────────────────────────┘
```

### Architectural Axioms for Presentations:
- **Decoupled Responsibilities**: The `gui` package never imports `java.sql.*` or writes raw SQL statements. It only interacts with the `service` layer.
- **Persistence Abstraction**: The `dao` package is agnostic to visual components. It only knows how to map SQL tables to Java model objects (`Book`, `Student`, etc.) and vice versa.
- **Service Orchestration**: The `service` layer bridges the two. It enforces domain business rules, handles multi-table ACID transactions, and converts SQL exceptions into user-friendly domain errors (`LibraryException`).

---

## 2. The `dao` (Data Access Object) Package

### Design Pattern & Core Purpose
The **Data Access Object (DAO)** pattern isolates the domain models and application logic from low-level database APIs (JDBC) and SQL database schemas. This ensures that changes to the database structure (e.g., column renames, database vendor changes) only affect the `dao.impl` package, leaving the rest of the application completely untouched.

### The Generic Base: `CrudDAO<T, ID>`
Located at `com.college.library.dao.CrudDAO<T, ID>`:
```java
public interface CrudDAO<T, ID> {
    void create(T entity) throws LibraryException;
    Optional<T> findById(ID id) throws LibraryException;
    List<T> findAll() throws LibraryException;
    void update(T entity) throws LibraryException;
    void delete(ID id) throws LibraryException;
}
```

#### Why this matters in a Viva:
- **Java Generics**: Type parameter `T` represents the Domain Model (e.g., `Book`, `Student`), while `ID` represents the Primary Key type (e.g., `String` for ISBN/Accession ID, `Integer` for auto-increment IDs).
- **`java.util.Optional`**: Prevents `NullPointerException` bugs by forcing callers of `findById` to explicitly handle missing records via `.isPresent()` or `.orElseThrow()`.

---

### Exhaustive Breakdown of DAO Interfaces & Implementations

#### 1. `BookDAO` & `BookDAOImpl`
- **Interface**: Extends `CrudDAO<Book, String>` (where `accession_id` is the primary key).
- **Specialized Methods**:
  - `updateStatus(String accessionId, String status, Connection conn)`: Updates the copy's status within an active transaction.
  - `updateStatusIfCondition(String accessionId, String newStatus, String expectedOldStatus, Connection conn)`: **Atomic conditional update** preventing race conditions during concurrent checkouts.
  - `getAllCategories()`: Retrieves catalog categories (`Category` objects).
  - `findByIsbn(String isbn)`: Retrieves all physical copies of a given title edition.
- **ResultSet Mapper**: Private method `mapResultSetToBook(ResultSet rs)` extracts columns (`accession_id`, `isbn`, `title`, `author`, `category_id`, `status`) into a `Book` object.

#### 2. `StudentDAO` & `StudentDAOImpl`
- **Interface**: Extends `CrudDAO<Student, String>` (where `ktu_id` is the primary key).
- **Specialized Methods**:
  - `updateStatus(String ktuId, String status)`: Allows toggling student membership between `ACTIVE` and `INACTIVE` (Soft Disable).
  - `hasRelatedRecords(String ktuId)`: Executes an aggregate subquery across `borrow_transactions`, `borrow_requests`, and `reservations` to verify if child records exist before attempting deletion.
  - `getRelatedRecordsSummary(String ktuId)`: Returns a human-readable list of dependencies (e.g., "1 active borrowed book and 2 borrowing history records") to display in confirmation dialogs.
- **Data Integrity Safeguard**: `delete(String ktuId)` intercepts MySQL error code `1451` (`SQLState 23000` - Foreign Key Constraint Violation) and converts it into a safe, helpful message preventing referential cascade accidents.

#### 3. `LibrarianDAO` & `LibrarianDAOImpl`
- **Interface**: Extends `CrudDAO<Librarian, Integer>`.
- **Specialized Methods**:
  - `findByUsername(String username)`: Used by `AuthService` during staff authentication. Queries `SELECT * FROM librarians WHERE username = ?`.

#### 4. `BorrowTransactionDAO` & `BorrowTransactionDAOImpl`
- **Interface**: Extends `CrudDAO<BorrowTransaction, Integer>`.
- **Specialized Methods**:
  - `createWithConnection(BorrowTransaction tx, Connection conn)`: Participates in external transactions. Uses `Statement.RETURN_GENERATED_KEYS` to capture MySQL's auto-generated `id` and set it back on `tx.setId(...)`.
  - `updateWithConnection(BorrowTransaction tx, Connection conn)`: Updates checkout records (such as `return_date`, `fine`, and `status = 'COMPLETED'`) under an active transaction lock.
  - `findActiveByBookId(String bookId)`: Queries `SELECT * FROM borrow_transactions WHERE book_id = ? AND status = 'ACTIVE'`.
  - `findByStudentId(String studentId)`: Retrieves complete checkout history for a student.
  - `countActiveTransactions()`: Returns total unreturned books for the dashboard counter.

#### 5. `BorrowRequestDAO` & `BorrowRequestDAOImpl`
- **Interface**: Extends `CrudDAO<BorrowRequest, Integer>`.
- **Specialized Methods**:
  - `findByStudentId(String studentId)`: Retrieves all requests for the student portal view.
  - `findByStatus(String status)`: Used by the librarian circulation desk to display `PENDING` approvals.
  - `findPendingByStudentAndBook(String studentId, String bookId)`: Prevents duplicate requests for the same book copy.

#### 6. `ReservationDAO` & `ReservationDAOImpl`
- **Interface**: Extends `CrudDAO<Reservation, Integer>`.
- **Specialized Methods**:
  - `findOldestPendingByIsbn(String isbn, Connection conn)`: Enforces **First-In, First-Out (FIFO)** waitlist queue ordering by querying:
    ```sql
    SELECT * FROM reservations WHERE book_isbn = ? AND status = 'PENDING' ORDER BY request_date ASC LIMIT 1
    ```
  - `findPendingByStudentId(String studentId)`: Checks outstanding reservations for a specific student.

---

### JDBC Internals & Database Safety Engineering

#### A. Central Connection Management: `DatabaseConnection`
- Located at `com.college.library.util.DatabaseConnection`.
- Reads connection settings from `src/main/resources/database.properties` inside a `static { ... }` initializer block using Java ClassLoader.
- Provides static factory method `getConnection()` calling `DriverManager.getConnection(url, user, pass)`.

#### B. SQL Injection Defense
Every query in the DAO layer strictly uses `java.sql.PreparedStatement`:
```java
String sql = "SELECT * FROM books WHERE accession_id = ?";
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setString(1, id); // Value is treated as literal data, never executable SQL
    ...
}
```

#### C. Try-with-Resources Resource Management
Every `Connection`, `PreparedStatement`, and `ResultSet` implements `java.lang.AutoCloseable`. They are instantiated inside parentheses of `try (...)`, guaranteeing socket closures even when network errors occur.

#### D. ACID Transactions & Concurrency Control
In `BorrowService.issueBook`, multi-table coordination is executed as follows:
```java
try (Connection conn = DatabaseConnection.getConnection()) {
    conn.setAutoCommit(false); // 1. Begin Transaction
    try {
        // 2. Atomic conditional update (Row lock in MySQL)
        int updated = bookDAO.updateStatusIfCondition(bookId, "ISSUED", "AVAILABLE", conn);
        if (updated == 0) {
            throw new LibraryException("Book is no longer available.");
        }
        
        // 3. Insert transaction record
        transactionDAO.createWithConnection(bt, conn);
        
        conn.commit(); // 4. Commit ACID Transaction
    } catch (Exception e) {
        conn.rollback(); // 5. Rollback on any failure
        throw new LibraryException("Issue failed: " + e.getMessage(), e);
    }
}
```

---

## 3. The `gui` (Graphical User Interface) Package

### Swing & AWT Architectural Roles
- **Java Swing (`javax.swing.*`)**: Supplies lightweight, platform-independent visual components (`JFrame`, `JPanel`, `JButton`, `JTable`, `JTextField`, `JPasswordField`, `JOptionPane`).
- **Java AWT (`java.awt.*`)**: Supplies core windowing layouts (`CardLayout`, `BorderLayout`, `GridLayout`, `FlowLayout`, `GridBagLayout`) and native event listeners (`ActionListener`, `MouseListener`).

---

### Master Controller & View Router: `MainApplication`

`MainApplication` extends `JFrame` and acts as the central coordinator of the entire application:

```
                      MainApplication (JFrame)
                                │
               CardLayout ("LOGIN", "DASHBOARD", ...)
                                │
  ┌───────────────┬─────────────┼───────────────┬────────────────┐
  ▼               ▼             ▼               ▼                ▼
LoginPanel   DashboardPanel  BookPanel    StudentPanel    BorrowPanel ...
```

#### Key Responsibilities:
1. **Dependency Injection**:
   - `initServices()` instantiates concrete DAOs and injects them into Service classes.
   - Example: `new BorrowService(transactionDAO, bookDAO, studentDAO, reservationDAO, borrowRequestDAO);`
2. **Container Management**:
   - Creates a parent `JPanel` with `CardLayout`.
   - Mounts 7 view panels using string identifiers (`"LOGIN"`, `"DASHBOARD"`, `"BOOKS"`, `"STUDENTS"`, `"BORROW"`, `"RESERVE"`, `"STUDENT_DASHBOARD"`).
3. **Navigation Controller**:
   - Method `navigateTo(String viewName)` displays the specified card and immediately calls `.refreshData()` on that panel.
4. **Session State**:
   - Stores `loggedInUser` (`Librarian`) and `loggedInStudent` (`Student`) across card transitions.

---

### Styling Framework: `UITheme`

Located at `com.college.library.gui.UITheme`, this class gives SmartLib a modern desktop appearance:
- **Clean Palette**: Uses Slate 50 (`#F8FAFC`) for backgrounds, Royal Blue (`#2563EB`) for primary accents, Emerald (`#10B981`) for success, and Crimson (`#EF4444`) for destructive actions.
- **Dynamic Font Resolution**: Queries `GraphicsEnvironment.getLocalGraphicsEnvironment()` to auto-detect system fonts, prioritizing `Segoe UI` (Windows), `Inter` (Linux), `Helvetica Neue` (macOS), or falling back to `SansSerif`.
- **Custom UI Components**:
  - `ModernButton`: Custom `JButton` subclass with rounded corners, smooth hover transitions, and click state handling.
  - `CardPanel`: Rounded white container simulating modern web card design.
  - `RoundedBorder`: Custom `AbstractBorder` supporting configurable radius and border colors.
- **`styleTable(JTable table)`**: Uniformly configures row height (32px), selection colors, column header fonts, and alternating row colors across all tables.

---

### Table Interaction Engine: `TableSelectionHelper`

Standard Swing `JTable` controls lack modern selection behaviors. `TableSelectionHelper` solves three critical UX problems:
1. **Toggle Row Deselection**: Clicking an already-selected row deselects it (standard Swing tables remain selected forever until another row is clicked).
2. **Mutually Exclusive Tables**: In multi-table panels (like `StudentDashboardPanel`), clicking a row in the *Reservations* table automatically clears selections in the *Search*, *Requests*, and *History* tables.
3. **Click Outside to Clear**: Attaches recursive mouse listeners to all container panels and text fields. Clicking on background space or focusing a text box immediately deselects active table rows.

---

### The 7 Screen Panels Breakdown

| Panel Class | Role & User Access | Key Features & Implementation Details |
|---|---|---|
| `LoginPanel` | Public Entry Screen | Dual-mode tab switcher (Student Portal vs. Staff). Regex validation on KTU ID (`^TVE(22\|23\|24\|25\|26)[A-Z]{2}[0-9]{3}$`). Account creation modal dialog for students with BCrypt hashing. |
| `DashboardPanel` | Librarian Home | 5 real-time stat cards (Total Books, Available Copies, Issued Books, Total Students, Active Loans). Navigation buttons to all circulation sub-modules. |
| `BookPanel` | Librarian Catalog | Inventory `JTable`. "Add New Book" dialog with `JComboBox<Category>` data binding. Deletes books with safety confirmation prompts. |
| `StudentPanel` | Librarian Directory | Student `JTable`. Dynamic KTU ID parser that auto-selects batch and branch. Soft status toggling (`ACTIVE` vs. `INACTIVE`). Delete protection checking for child records. |
| `BorrowPanel` | Circulation Desk | Two side-by-side action cards: Direct Book Issue & Direct Book Return (with automated overdue fine popups). Bottom table for student borrow request approval/rejection. |
| `ReservationPanel` | Waitlist Desk | Title-level reservation form by ISBN. Priority waitlist queue table ordered by timestamp. "Fulfil Reservation" button to process waitlist loans. |
| `StudentDashboardPanel` | Student Self-Service | $2 \times 2$ grid layout: 1) Catalog search by title/author/ISBN with copy counts and "Request Borrow"; 2) Personal request tracker; 3) Personal borrowing history & fine dues; 4) Personal waitlist queue position. |

---

### Event Dispatch Thread (EDT) & Data Binding

- **Thread Safety**: Swing is not thread-safe. `MainApplication.main()` wraps startup execution in `SwingUtilities.invokeLater(() -> new MainApplication().setVisible(true));` to ensure all UI components are constructed on the Event Dispatch Thread.
- **Data Binding via `DefaultTableModel`**:
  - Tables do not directly hold domain models. Instead, an anonymous subclass of `DefaultTableModel` overrides `isCellEditable(row, col)` returning `false` to make table cells read-only.
  - To refresh views, `tableModel.setRowCount(0)` wipes existing rows, and records fetched from the service layer are loaded row-by-row via `tableModel.addRow(new Object[]{...})`.

---

## 4. End-to-End Execution Traces

### Trace 1: Direct Book Issue
```
[Librarian UI: BorrowPanel]
   │
   ├─► User inputs "TVE23CS001" (Student KTU ID) & "B001-1" (Accession ID)
   ├─► Clicks "Issue Book" button
   │
[Service Layer: BorrowService.issueBook]
   │
   ├─► Calls studentDAO.findById("TVE23CS001")
   │      └─► Validates student exists and isActive() == true
   ├─► Opens JDBC Connection: conn.setAutoCommit(false)
   ├─► Calls bookDAO.updateStatusIfCondition("B001-1", "ISSUED", "AVAILABLE", conn)
   │      └─► Runs: UPDATE books SET status='ISSUED' WHERE accession_id=? AND status='AVAILABLE'
   │      └─► Affected rows == 1 (Verifies atomic concurrency guarantee)
   ├─► Prepares BorrowTransaction POJO (Issue date = today, Due date = today + 14 days)
   ├─► Calls transactionDAO.createWithConnection(bt, conn)
   │      └─► Runs: INSERT INTO borrow_transactions (...)
   ├─► Calls conn.commit()
   │
[UI Callback]
   │
   └─► BorrowPanel displays JOptionPane.showMessageDialog("Book issued successfully!")
   └─► Clears input fields
```

---

### Trace 2: Direct Book Return & Overdue Fine Calculation
```
[Librarian UI: BorrowPanel]
   │
   ├─► User inputs "B001-1" (Accession ID) & clicks "Return Book"
   │
[Service Layer: BorrowService.returnBook]
   │
   ├─► Opens JDBC Connection: conn.setAutoCommit(false)
   ├─► Calls transactionDAO.findActiveByBookId("B001-1") -> returns BorrowTransaction
   ├─► Fine Calculation Logic:
   │      LocalDate now = LocalDate.now();
   │      if (now.isAfter(bt.getDueDate())) {
   │          long days = ChronoUnit.DAYS.between(bt.getDueDate(), now);
   │          fine = days * FINE_PER_DAY; // Rs. 10.0 per overdue day
   │      }
   ├─► Updates transaction: returnDate = now, fine = fine, status = 'COMPLETED'
   ├─► Calls transactionDAO.updateWithConnection(bt, conn)
   ├─► Calls bookDAO.updateStatus("B001-1", "AVAILABLE", conn)
   ├─► Checks Reservation Queue:
   │      reservationDAO.findOldestPendingByIsbn(book.getIsbn(), conn)
   │      └─► If pending reservation found: constructs notification message
   ├─► Calls conn.commit()
   │
[UI Callback]
   │
   └─► Displays fine popup (e.g., "Book returned. Overdue fine: ₹50.00. Reserved for: TVE23CS045")
   └─► Refreshes circulation tables
```

---

### Trace 3: Student Catalog Search & Borrow Request
```
[Student UI: StudentDashboardPanel]
   │
   ├─► Student types "Java" in search field & clicks "Search"
   │
[Service Layer: BookService.searchAvailableBooks("Java")]
   │
   ├─► Calls bookDAO.findAll() -> executes SELECT * FROM books
   ├─► Service filters books matching "Java" in title/author/ISBN
   ├─► Groups by ISBN and calculates available copies count
   ├─► Returns List<Object[]>: [ISBN, Title, Author, Category, AvailableCount]
   │
[Student UI Table Display]
   │
   ├─► Displays aggregated books in searchTable
   ├─► Student selects row and clicks "Request Borrow"
   │
[Service Layer: BorrowService.requestToBorrow]
   │
   ├─► Checks student status is ACTIVE
   ├─► Finds first available copy accession ID
   ├─► Checks no existing pending request for same student & book
   ├─► Calls borrowRequestDAO.create(new BorrowRequest(studentId, bookId, PENDING))
   │
[UI Callback]
   │
   └─► Displays "Borrow request submitted successfully!"
   └─► Refreshes "My Borrow Requests" table
```

---

## 5. Presentation Script & Talking Points

Use these concise, professional talking points during your project slides:

### Slide 1: System Overview & Architecture
> *"SmartLib is an enterprise-style Desktop Library Circulation System designed for colleges. It uses a modern multi-tier architecture with Java Swing for the presentation layer, MySQL for the persistent database, and a specialized DAO and Service layer separating the two. This guarantees that user interface code contains zero database queries, adhering to the Single Responsibility Principle."*

### Slide 2: The `dao` Package & Persistence Layer
> *"In our `dao` package, we implemented the Data Access Object design pattern using a generic base interface, `CrudDAO<T, ID>`. All database queries are executed using `PreparedStatement` with parameterized placeholders, eliminating the risk of SQL injection. Furthermore, every resource is wrapped in try-with-resources blocks to guarantee zero memory or socket leaks."*

### Slide 3: The `gui` Package & Desktop UX
> *"Our presentation layer in `gui` is managed by `MainApplication` using Java's `CardLayout`. Rather than confusing users with multiple pop-up windows, all 7 main screens exist within a single desktop window like cards in a deck. We also developed a custom `UITheme` providing modern palettes and custom buttons, alongside `TableSelectionHelper` to enable intuitive click-to-deselect and mutually exclusive table interactions."*

### Slide 4: Concurrency & Transaction Management
> *"A critical achievement in SmartLib is handling concurrency. If two students request the last copy of a book at the same second, `BorrowService` executes an atomic conditional update at the database level: `UPDATE books SET status='ISSUED' WHERE status='AVAILABLE'`. MySQL's row lock ensures only the first user succeeds, while the second transaction automatically rolls back and alerts the user without causing data corruption."*

---

## 6. Academic Viva Voce Q&A

Here are the top 15 questions examiners ask regarding DAO, JDBC, Swing, and Desktop Architecture, along with the exact model answers:

#### Q1: What is the DAO pattern and why did you use it?
**Model Answer**: The Data Access Object (DAO) pattern abstracts and encapsulates all access to the data source. It decouples the business logic and user interface from low-level JDBC database operations. If we migrate from MySQL to PostgreSQL or SQLite, only the classes in `dao.impl` need to change; the Service and GUI layers remain untouched.

#### Q2: Why define interfaces like `BookDAO` instead of writing only `BookDAOImpl`?
**Model Answer**: This implements the **Dependency Inversion Principle** (the 'D' in SOLID). High-level modules (`BookService`) depend on abstractions (`BookDAO`), not concrete implementations. This enables loose coupling and makes unit testing possible using mock objects without requiring a live database connection.

#### Q3: How does SmartLib protect against SQL Injection?
**Model Answer**: SmartLib exclusively uses `java.sql.PreparedStatement` with `?` placeholders. The parameters are bound using typed setters (e.g., `pstmt.setString()`, `pstmt.setInt()`). The database driver escapes and treats these values strictly as literal data, preventing malicious SQL code injection.

#### Q4: What is the purpose of `CrudDAO<T, ID>` using Generics?
**Model Answer**: It enforces standard CRUD operations (`create`, `findById`, `findAll`, `update`, `delete`) across all entities while maintaining compile-time type safety. `T` denotes the entity type (e.g., `Book`), and `ID` denotes the primary key type (e.g., `String` or `Integer`), eliminating redundant boilerplate code across interfaces.

#### Q5: Why is `CardLayout` used in `MainApplication` instead of opening multiple `JFrame` windows?
**Model Answer**: `CardLayout` manages multiple views within a single parent window container, treating panels like cards. This provides a clean single-window desktop application experience, reduces operating system window management overhead, and easily preserves session states (`loggedInUser`, `loggedInStudent`).

#### Q6: What is the Event Dispatch Thread (EDT) and why is it important in Swing?
**Model Answer**: Swing components are not thread-safe. All component creation, updates, and event listeners must execute on a single dedicated background thread called the **Event Dispatch Thread (EDT)**. We initialize the application via `SwingUtilities.invokeLater(...)` to schedule GUI setup on the EDT, preventing race conditions, UI freezing, or rendering glitches.

#### Q7: How are database transactions handled when an operation touches multiple tables?
**Model Answer**: In `BorrowService`, we obtain a shared `Connection` and set `conn.setAutoCommit(false)`. We pass this same `Connection` to the respective DAOs via methods like `createWithConnection(bt, conn)` and `updateStatusIfCondition(..., conn)`. If all operations succeed, we call `conn.commit()`. If an exception occurs, `conn.rollback()` restores the database state, fulfilling the **Atomicity** requirement of ACID transactions.

#### Q8: What does `Statement.RETURN_GENERATED_KEYS` do in `BorrowTransactionDAOImpl`?
**Model Answer**: The `borrow_transactions` table uses an `AUTO_INCREMENT` primary key. Specifying `Statement.RETURN_GENERATED_KEYS` during `prepareStatement` instructs MySQL to return the ID assigned by the database engine upon insertion. We then extract it from `pstmt.getGeneratedKeys()` and set it back on the Java model object.

#### Q9: What problem does your custom `TableSelectionHelper` solve?
**Model Answer**: By default, Swing `JTable` components cannot be deselected by clicking on an already-selected row, and having multiple tables causes awkward multi-table selections. `TableSelectionHelper` uses custom `MouseListener` hooks to provide click-to-deselect, mutually exclusive group selections across tables, and outside-click deselection.

#### Q10: How does SmartLib prevent race conditions if two users issue the same book copy at the same time?
**Model Answer**: In `BorrowService.issueBook`, we execute: `UPDATE books SET status='ISSUED' WHERE accession_id=? AND status='AVAILABLE'`. MySQL uses row-level locking during updates. Only the first query matches and updates 1 row; the concurrent query finds 0 matching rows. The service checks the affected row count, rolls back the transaction, and throws a concurrency error to the second user.

#### Q11: Why is `LibraryException` a checked exception instead of an unchecked `RuntimeException`?
**Model Answer**: `LibraryException` extends `java.lang.Exception`. Making it checked forces callers (such as GUI event handlers) to explicitly handle errors using `try-catch` blocks. This guarantees that errors (such as failed logins, duplicate checkouts, or network failures) are caught and displayed to the user via `JOptionPane` dialogs rather than silently crashing the application.

#### Q12: How is the overdue fine calculated in `BorrowPanel`?
**Model Answer**: When returning a book, `BorrowService.returnBook` compares `LocalDate.now()` with `transaction.getDueDate()`. If today is after the due date, it calculates overdue days using `ChronoUnit.DAYS.between(dueDate, now)` and multiplies it by `FINE_PER_DAY` (Rs. 10.0/day). The fine is recorded in `borrow_transactions.fine` upon return.

#### Q13: What happens to a returned book if someone has reserved it?
**Model Answer**: Upon processing a return, `BorrowService` queries `reservationDAO.findOldestPendingByIsbn(...)` to check for active reservations for that title edition. If a waiting student exists, the system returns a prominent notification prompting library staff to hold the book for that student.

#### Q14: How does data binding work in your GUI tables?
**Model Answer**: We use `DefaultTableModel`. In our panel classes, `refreshData()` wipes existing rows with `tableModel.setRowCount(0)`, fetches the latest domain objects from the service layer, and populates the table using `tableModel.addRow(new Object[]{...})`. We override `isCellEditable(...)` to return `false` so users cannot edit table cells directly.

#### Q15: How does `StudentDAOImpl` protect against foreign key constraint violations?
**Model Answer**: Before deleting a student, `StudentDAOImpl` checks `hasRelatedRecords(ktuId)`. If the student has historical borrow transactions, active borrow requests, or reservations, it prevents deletion and outputs an informative summary explaining why the record cannot be purged, thereby preserving database integrity.
