-- Core Tables
CREATE TABLE IF NOT EXISTS librarians (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255),
    name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS students (
    ktu_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100),
    branch VARCHAR(50),
    semester INT,
    batch VARCHAR(10),
    email VARCHAR(100),
    phone VARCHAR(20)
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
