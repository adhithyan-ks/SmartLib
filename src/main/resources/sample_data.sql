-- Sample Data for SmartLib

-- 1. Insert a Librarian (Password is 'password123' hashed using BCrypt)
-- Note: The hash was generated with jBcrypt using 12 rounds.
INSERT INTO librarians (username, password_hash, name) 
VALUES ('admin', '$2a$12$BXtk3cieal0XaAOZrgQ.4eiI.mBCU7M2tk2gjIsIo4uJEH7mKGIJ6', 'Admin Librarian');

-- 2. Insert Categories
INSERT INTO categories (name) VALUES ('Computer Science'), ('Mathematics'), ('Physics');

-- 3. Insert Books
INSERT INTO books (accession_id, isbn, title, author, category_id, status) VALUES 
('B1001', '978-0134685991', 'Effective Java', 'Joshua Bloch', 1, 'AVAILABLE'),
('B1002', '978-0201633610', 'Design Patterns', 'Erich Gamma', 1, 'AVAILABLE'),
('B1003', '978-0321125217', 'Domain-Driven Design', 'Eric Evans', 1, 'ISSUED');

-- 4. Insert Students (Password is 'password123' hashed using BCrypt)
INSERT INTO students (ktu_id, password_hash, name, branch, semester, batch, email, phone) VALUES 
('TVE23CS001', '$2a$12$BXtk3cieal0XaAOZrgQ.4eiI.mBCU7M2tk2gjIsIo4uJEH7mKGIJ6', 'Alice Smith', 'CSE', 5, '2023-2027', 'alice@cet.ac.in', '9876543210'),
('TVE23CS002', '$2a$12$BXtk3cieal0XaAOZrgQ.4eiI.mBCU7M2tk2gjIsIo4uJEH7mKGIJ6', 'Bob Jones', 'CSE', 5, '2023-2027', 'bob@cet.ac.in', '9876543211');

-- 5. Insert an Active Borrow Transaction for the issued book
INSERT INTO borrow_transactions (student_id, book_id, librarian_id, issue_date, due_date, fine, status) 
VALUES ('TVE23CS001', 'B1003', 1, '2023-10-01', '2023-10-15', 0.00, 'ACTIVE');

-- 6. Insert a Pending Reservation
INSERT INTO reservations (student_id, book_isbn, status) 
VALUES ('TVE23CS002', '978-0321125217', 'PENDING');
