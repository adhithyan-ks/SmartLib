# Person.java
**Path:** `src/main/java/com/college/library/model/Person.java`

```java
package com.college.library.model;

public abstract class Person {
    private String name;
    private String email;
    private String phone;

    public Person() {}

    public Person(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
```

# Student.java
**Path:** `src/main/java/com/college/library/model/Student.java`

```java
package com.college.library.model;

public class Student extends Person {
    private String ktuId;
    private String passwordHash;
    private String branch;
    private int semester;
    private String batch;
    private String status = "ACTIVE";

    public Student() {}

    public Student(String ktuId, String passwordHash, String name, String branch, int semester, String batch, String email, String phone, String status) {
        super(name, email, phone);
        this.ktuId = ktuId;
        this.passwordHash = passwordHash;
        this.branch = branch;
        this.semester = semester;
        this.batch = batch;
        this.status = status != null ? status : "ACTIVE";
    }

    public Student(String ktuId, String passwordHash, String name, String branch, int semester, String batch, String email, String phone) {
        this(ktuId, passwordHash, name, branch, semester, batch, email, phone, "ACTIVE");
    }

    public Student(String ktuId, String name, String branch, int semester, String batch, String email, String phone) {
        this(ktuId, null, name, branch, semester, batch, email, phone, "ACTIVE");
    }

    public String getKtuId() { return ktuId; }
    public void setKtuId(String ktuId) { this.ktuId = ktuId; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status != null ? status : "ACTIVE"; }

    public boolean isActive() {
        return status == null || "ACTIVE".equalsIgnoreCase(status);
    }
}
```

# Librarian.java
**Path:** `src/main/java/com/college/library/model/Librarian.java`

```java
package com.college.library.model;

public class Librarian extends Person {
    private int id;
    private String username;
    private String passwordHash;

    public Librarian() {}

    public Librarian(int id, String username, String passwordHash, String name) {
        super(name, null, null);
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
```

# Book.java
**Path:** `src/main/java/com/college/library/model/Book.java`

```java
package com.college.library.model;

public class Book {
    private String accessionId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String edition;
    private int categoryId;
    private String status; // 'AVAILABLE', 'ISSUED', 'LOST', 'DAMAGED'

    public Book() {}

    public Book(String accessionId, String isbn, String title, String author, String publisher, String edition, int categoryId, String status) {
        this.accessionId = accessionId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.edition = edition;
        this.categoryId = categoryId;
        this.status = status;
    }

    public String getAccessionId() { return accessionId; }
    public void setAccessionId(String accessionId) { this.accessionId = accessionId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

# Category.java
**Path:** `src/main/java/com/college/library/model/Category.java`

```java
package com.college.library.model;

public class Category {
    private int id;
    private String name;

    public Category() {}

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }
}
```

# BorrowTransaction.java
**Path:** `src/main/java/com/college/library/model/BorrowTransaction.java`

```java
package com.college.library.model;

import java.time.LocalDate;

public class BorrowTransaction {
    private int id;
    private String studentId;
    private String bookId;
    private int librarianId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private String status; // 'ACTIVE', 'COMPLETED'

    public BorrowTransaction() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public int getLibrarianId() { return librarianId; }
    public void setLibrarianId(int librarianId) { this.librarianId = librarianId; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

# BorrowRequest.java
**Path:** `src/main/java/com/college/library/model/BorrowRequest.java`

```java
package com.college.library.model;

import java.time.LocalDateTime;

public class BorrowRequest {
    private int id;
    private String studentId;
    private String bookId;
    private LocalDateTime requestDate;
    private String status;

    public BorrowRequest() {}

    public BorrowRequest(int id, String studentId, String bookId, LocalDateTime requestDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.bookId = bookId;
        this.requestDate = requestDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

# Reservation.java
**Path:** `src/main/java/com/college/library/model/Reservation.java`

```java
package com.college.library.model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private String studentId;
    private String bookIsbn;
    private LocalDateTime requestDate;
    private String status; // 'PENDING', 'FULFILLED', 'CANCELLED'

    public Reservation() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

# LibraryException.java
**Path:** `src/main/java/com/college/library/exception/LibraryException.java`

```java
package com.college.library.exception;

public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
    
    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

# schema.sql
**Path:** `src/main/resources/schema.sql`

```sql
-- Core Tables
CREATE TABLE IF NOT EXISTS librarians (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255),
    name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS students (
    ktu_id VARCHAR(20) PRIMARY KEY,
    password_hash VARCHAR(255),
    name VARCHAR(100),
    branch VARCHAR(50),
    semester INT,
    batch VARCHAR(10),
    email VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE
);

CREATE TABLE IF NOT EXISTS books (
    accession_id VARCHAR(20) PRIMARY KEY,
    isbn VARCHAR(20),
    title VARCHAR(255),
    author VARCHAR(255),
    publisher VARCHAR(100),
    edition VARCHAR(50),
    category_id INT,
    status ENUM('AVAILABLE', 'ISSUED', 'LOST', 'DAMAGED'),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS borrow_transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20),
    book_id VARCHAR(20),
    librarian_id INT,
    issue_date DATE,
    due_date DATE,
    return_date DATE,
    fine DECIMAL(10,2),
    status ENUM('ACTIVE', 'COMPLETED'),
    FOREIGN KEY (student_id) REFERENCES students(ktu_id),
    FOREIGN KEY (book_id) REFERENCES books(accession_id),
    FOREIGN KEY (librarian_id) REFERENCES librarians(id)
);

-- Innovative Feature Tables
CREATE TABLE IF NOT EXISTS reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20),
    book_isbn VARCHAR(20),
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'FULFILLED', 'CANCELLED'),
    FOREIGN KEY (student_id) REFERENCES students(ktu_id)
);

CREATE TABLE IF NOT EXISTS borrow_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    book_id VARCHAR(50) NOT NULL,
    request_date DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(ktu_id),
    FOREIGN KEY (book_id) REFERENCES books(accession_id)
);
```
