-- Sample Data for SmartLib

-- 1. Insert a Librarian (Password is 'password123' hashed using BCrypt)
-- Note: The hash was generated with jBcrypt using 12 rounds.
INSERT INTO librarians (username, password_hash, name) 
VALUES ('admin', '$2a$12$BXtk3cieal0XaAOZrgQ.4eiI.mBCU7M2tk2gjIsIo4uJEH7mKGIJ6', 'Admin Librarian');

-- 2. Insert Categories
INSERT INTO categories (name) VALUES ('Computer Science'), ('Mathematics'), ('Physics');

-- 3. Insert Books
INSERT INTO books (accession_id, isbn, title, author, publisher, edition, category_id, status) VALUES 
('B1001', '978-0134685991', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', '3rd', 1, 'AVAILABLE'),
('B1002', '978-0201633610', 'Design Patterns', 'Erich Gamma', 'Addison-Wesley', '1st', 1, 'AVAILABLE'),
('B1003', '978-0321125217', 'Domain-Driven Design', 'Eric Evans', 'Addison-Wesley', '1st', 1, 'ISSUED');

-- 4. Insert Students
INSERT INTO students (ktu_id, name, branch, semester, batch, email, phone) VALUES 
('TCR20CS001', 'Alice Smith', 'CSE', 5, '2020-2024', 'alice@example.com', '1234567890'),
('TCR20CS002', 'Bob Jones', 'CSE', 5, '2020-2024', 'bob@example.com', '0987654321');

-- 5. Insert an Active Borrow Transaction for the issued book
INSERT INTO borrow_transactions (student_id, book_id, librarian_id, issue_date, due_date, fine, status) 
VALUES ('TCR20CS001', 'B1003', 1, '2023-10-01', '2023-10-15', 0.00, 'ACTIVE');

-- 6. Insert a Pending Reservation
INSERT INTO reservations (student_id, book_isbn, status) 
VALUES ('TCR20CS002', '978-0321125217', 'PENDING');
