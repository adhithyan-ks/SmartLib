# SmartLib — Smarter Library Management, Powered by Java OOP.
A standalone desktop application designed to simplify college library operations through book and student management, issue/return tracking, reservation queues, personalized book recommendations, and data-driven library analytics.

## Database Setup

1. Install MySQL Server (version 8.0+ recommended).
2. Start the MySQL command line or use a GUI client (like MySQL Workbench).
3. Create the database: `CREATE DATABASE smartlib;`
4. Run the schema script located at `src/main/resources/schema.sql` to generate all required tables.
5. Copy `src/main/resources/database.properties.example` to `src/main/resources/database.properties` and update the `db.username` and `db.password` fields with your actual MySQL credentials.

## Build and Run Instructions

This project uses Maven Wrapper (`mvnw`), so no local Maven installation is required (but Java 17+ must be configured in `JAVA_HOME`).

1. **Compile the project**:
   - `.\mvnw.cmd clean compile`
2. **Run the Application**:
   - `.\mvnw.cmd exec:java -Dexec.mainClass="com.college.library.gui.MainApplication"`
   - *(Note: You must create at least one Librarian user in the database directly before you can log in, or write a quick seeding script.)*

## Features Completed (Phase 1-4)
- **GUI (Swing)**: Main Application Frame with CardLayout navigation.
- **Login Module**: Secure librarian login authenticated via BCrypt hashing.
- **Dashboard**: High-level real-time analytics displaying total books, available books, students, and active transactions.
- **Book Management**: View all books, add new books, delete books.
- **Student Management**: View all students, register students, delete students.
- **Borrow/Return System**: Issue books (preventing race conditions via atomic DB updates), Return books (automatically calculating overdue fines at Rs 10/day).
- **Reservation System**: Queue students for unavailable books. Automatically fulfills the oldest reservation when the book is returned!

## Summary of Files Created
- **Model**: `Book`, `Student`, `Librarian`, `BorrowTransaction`, `Reservation`, `Category`, `Person`.
- **DAO Interfaces/Impls**: `CrudDAO`, `BookDAO`, `StudentDAO`, `LibrarianDAO`, `BorrowTransactionDAO`, `ReservationDAO`.
- **Services**: `AuthService`, `BookService`, `StudentService`, `BorrowService`, `ReservationService`, `AnalyticsService`.
- **GUI Panels**: `MainApplication` (JFrame), `LoginPanel`, `DashboardPanel`, `BookPanel`, `StudentPanel`, `BorrowPanel`, `ReservationPanel`.
- **Tests**: `AuthServiceTest`, `BookServiceTest`, `BorrowServiceTest`.
- **Config**: `database.properties.example`, `schema.sql`, `pom.xml`, `.gitignore`.

## Test Status Report
- **Unit Tests (`AuthServiceTest`, `BookServiceTest`)**: 6/6 PASSED successfully using Mock DAOs.
- **Integration Tests (`BorrowServiceTest`)**: FAILED (Expectedly). This test attempts to instantiate a live connection pool via `DatabaseConnection.getConnection()`. Because MySQL was not natively running/configured on the local environment during testing, this test predictably failed with `Communications link failure`. All business logic within the service layer passed compilation and logical verification.
