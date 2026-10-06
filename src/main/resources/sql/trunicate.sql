USE smartlib;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE borrow_transactions;
TRUNCATE TABLE borrow_requests;
TRUNCATE TABLE reservations;
TRUNCATE TABLE books;
TRUNCATE TABLE students;
TRUNCATE TABLE librarians;
TRUNCATE TABLE categories;

SET FOREIGN_KEY_CHECKS = 1;