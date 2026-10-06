-- CATEGORIES

INSERT INTO `categories` (`id`, `name`) VALUES

(6, 'Artificial Intelligence'),

(4, 'Computer Networks'),

(2, 'Data Structures'),

(3, 'Database Systems'),

(7, 'Electronics'),

(8, 'Engineering'),

(5, 'Operating Systems'),

(1, 'Programming');


-- LIBRARIANS

INSERT INTO `librarians` (`id`, `username`, `password_hash`, `name`) VALUES
(1, 'admin', '$2a$12$BXtk3cieal0XaAOZrgQ.4eiI.mBCU7M2tk2gjIsIo4uJEH7mKGIJ6', 'Library Administrator'),
(2, 'librarian', '$2a$12$BXtk3cieal0XaAOZrgQ.4eiI.mBCU7M2tk2gjIsIo4uJEH7mKGIJ6', 'Main Librarian');


-- STUDENTS

INSERT INTO `students` (`ktu_id`, `password_hash`, `name`, `branch`, `semester`, `batch`, `email`, `phone`) VALUES

('TVE25CS070', '$2a$12$act2.lYNgJf.OA/EkQPNhujntlCoHNq40Pa7VcmMVmEY7OxjpsHS2', 'Adhithyan', 'CSE', 3, '2025-2029', 'adhithyanks@cet.ac.in', '9747601190'),

('TVE26AE009', NULL, 'Ishaan M', 'AEI', 4, '2024-2028', 'ishaan09@cet.ac.in', '6682443302'),

('TVE26AE010', NULL, 'Keerthana V', 'AEI', 6, '2023-2027', 'keerthana10@cet.ac.in', '9887558036'),

('TVE26AE011', NULL, 'Jeevan K', 'AEI', 3, '2024-2028', 'jeevan11@cet.ac.in', '6544998977'),

('TVE26AE012', NULL, 'Meera P', 'AEI', 3, '2024-2028', 'meera12@cet.ac.in', '6721800973'),

('TVE26CE021', NULL, 'Varun M', 'CE', 6, '2023-2027', 'varun21@cet.ac.in', '6658533585'),

('TVE26CE022', NULL, 'Anjali P', 'CE', 6, '2023-2027', 'anjali22@cet.ac.in', '9496287529'),

('TVE26CE023', NULL, 'Amal Dev', 'CE', 6, '2023-2027', 'amal23@cet.ac.in', '7103078240'),

('TVE26CE024', NULL, 'Devika N', 'CE', 4, '2024-2028', 'devika24@cet.ac.in', '6127790973'),

('TVE26CS001', NULL, 'Aarav Menon', 'CSE', 3, '2024-2028', 'aarav01@cet.ac.in', '6649210032'),

('TVE26CS002', NULL, 'Ananya Krishnan', 'CSE', 4, '2024-2028', 'ananya02@cet.ac.in', '9472769699'),

('TVE26CS003', NULL, 'Arjun Raj', 'CSE', 7, '2022-2026', 'arjun03@cet.ac.in', '8727823385'),

('TVE26CS004', NULL, 'Athira S', 'CSE', 4, '2024-2028', 'athira04@cet.ac.in', '8215607454'),

('TVE26EC005', NULL, 'Adithya Nair', 'ECE', 5, '2023-2027', 'adithya05@cet.ac.in', '9451507651'),

('TVE26EC006', NULL, 'Diya Thomas', 'ECE', 5, '2023-2027', 'diya06@cet.ac.in', '6930324714'),

('TVE26EC007', NULL, 'Fathima K', 'ECE', 5, '2023-2027', 'fathima07@cet.ac.in', '8124546404'),

('TVE26EC008', NULL, 'Gokul R', 'ECE', 7, '2022-2026', 'gokul08@cet.ac.in', '7176007735'),

('TVE26EE013', NULL, 'Karthik S', 'EEE', 6, '2023-2027', 'karthik13@cet.ac.in', '9572769975'),

('TVE26EE014', NULL, 'Nandana R', 'EEE', 5, '2023-2027', 'nandana14@cet.ac.in', '7950363817'),

('TVE26EE015', NULL, 'Rahul K', 'EEE', 6, '2023-2027', 'rahul15@cet.ac.in', '7931820094'),

('TVE26EE016', NULL, 'Riya Thomas', 'EEE', 7, '2022-2026', 'riya16@cet.ac.in', '6960576415'),

('TVE26EL017', NULL, 'Sanjay P', 'EL', 4, '2024-2028', 'sanjay17@cet.ac.in', '9305635965'),

('TVE26EL018', NULL, 'Sreeram V', 'EL', 4, '2024-2028', 'sreeram18@cet.ac.in', '7772503091'),

('TVE26EL019', NULL, 'Theertha S', 'EL', 6, '2023-2027', 'theertha19@cet.ac.in', '6687568187'),

('TVE26EL020', NULL, 'Vishnu K', 'EL', 6, '2023-2027', 'vishnu20@cet.ac.in', '6588015511'),

('TVE26IE029', NULL, 'Midhun K', 'IE', 4, '2024-2028', 'midhun29@cet.ac.in', '9571064949'),

('TVE26IE030', NULL, 'Navya R', 'IE', 5, '2023-2027', 'navya30@cet.ac.in', '8827320568'),

('TVE26IE031', NULL, 'Neeraj S', 'IE', 3, '2024-2028', 'neeraj31@cet.ac.in', '6324633301'),

('TVE26IE032', NULL, 'Parvathy V', 'IE', 5, '2023-2027', 'parvathy32@cet.ac.in', '9208779627'),

('TVE26ME025', NULL, 'Gautham S', 'ME', 6, '2023-2027', 'gautham25@cet.ac.in', '9585118189'),

('TVE26ME026', NULL, 'Hari V', 'ME', 3, '2024-2028', 'hari26@cet.ac.in', '8237850492'),

('TVE26ME027', NULL, 'Jithin P', 'ME', 6, '2023-2027', 'jithin27@cet.ac.in', '8582376511'),

('TVE26ME028', NULL, 'Krishna S', 'ME', 6, '2023-2027', 'krishna28@cet.ac.in', '8889629310');


-- BOOKS

INSERT INTO `books` (`accession_id`, `isbn`, `title`, `author`, `publisher`, `edition`, `category_id`, `status`) VALUES

('B001-1', '9780132350884', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', '1st', 1, 'AVAILABLE'),

('B001-2', '9780132350884', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', '1st', 1, 'AVAILABLE'),

('B002-1', '9780135957059', 'The Pragmatic Programmer', 'Andrew Hunt', 'Addison-Wesley', '2nd', 1, 'ISSUED'),

('B002-2', '9780135957059', 'The Pragmatic Programmer', 'Andrew Hunt', 'Addison-Wesley', '2nd', 1, 'AVAILABLE'),

('B003-1', '9780262046305', 'Introduction to Algorithms', 'Cormen et al.', 'MIT Press', '4th', 2, 'ISSUED'),

('B003-2', '9780262046305', 'Introduction to Algorithms', 'Cormen et al.', 'MIT Press', '4th', 2, 'AVAILABLE'),

('B004-1', '9780672324536', 'Data Structures and Algorithms in Java', 'Robert Lafore', 'Sams', '2nd', 2, 'AVAILABLE'),

('B004-2', '9780672324536', 'Data Structures and Algorithms in Java', 'Robert Lafore', 'Sams', '2nd', 2, 'ISSUED'),

('B005-1', '9780078022159', 'Database System Concepts', 'Silberschatz', 'McGraw-Hill', '7th', 3, 'ISSUED'),

('B005-2', '9780078022159', 'Database System Concepts', 'Silberschatz', 'McGraw-Hill', '7th', 3, 'AVAILABLE'),

('B006-1', '9780133970777', 'Fundamentals of Database Systems', 'Elmasri & Navathe', 'Pearson', '7th', 3, 'ISSUED'),

('B006-2', '9780133970777', 'Fundamentals of Database Systems', 'Elmasri & Navathe', 'Pearson', '7th', 3, 'AVAILABLE'),

('B007-1', '9780133594140', 'Computer Networking', 'Kurose & Ross', 'Pearson', '7th', 4, 'AVAILABLE'),

('B007-2', '9780133594140', 'Computer Networking', 'Kurose & Ross', 'Pearson', '7th', 4, 'ISSUED'),

('B008-1', '9780132126953', 'Computer Networks', 'Andrew Tanenbaum', 'Pearson', '5th', 4, 'ISSUED'),

('B008-2', '9780132126953', 'Computer Networks', 'Andrew Tanenbaum', 'Pearson', '5th', 4, 'AVAILABLE'),

('B009-1', '9781118063330', 'Operating System Concepts', 'Silberschatz', 'Wiley', '9th', 5, 'AVAILABLE'),

('B009-2', '9781118063330', 'Operating System Concepts', 'Silberschatz', 'Wiley', '9th', 5, 'ISSUED'),

('B010-1', '9780133591620', 'Modern Operating Systems', 'Andrew Tanenbaum', 'Pearson', '4th', 5, 'DAMAGED'),

('B010-2', '9780133591620', 'Modern Operating Systems', 'Andrew Tanenbaum', 'Pearson', '4th', 5, 'AVAILABLE'),

('B011-1', '9780134610993', 'Artificial Intelligence: A Modern Approach', 'Russell & Norvig', 'Pearson', '4th', 6, 'LOST'),

('B011-2', '9780134610993', 'Artificial Intelligence: A Modern Approach', 'Russell & Norvig', 'Pearson', '4th', 6, 'ISSUED'),

('B012-1', '9781492032649', 'Hands-On Machine Learning', 'Aurélien Géron', 'O\'Reilly', '3rd', 6, 'ISSUED'),

('B012-2', '9781492032649', 'Hands-On Machine Learning', 'Aurélien Géron', 'O\'Reilly', '3rd', 6, 'AVAILABLE'),

('B013-1', '9780134545813', 'Digital Design', 'M. Morris Mano', 'Pearson', '6th', 7, 'AVAILABLE'),

('B013-2', '9780134545813', 'Digital Design', 'M. Morris Mano', 'Pearson', '6th', 7, 'ISSUED'),

('B014-1', '9780198063245', 'Microelectronic Circuits', 'Sedra & Smith', 'Oxford University Press', '6th', 7, 'AVAILABLE'),

('B014-2', '9780198063245', 'Microelectronic Circuits', 'Sedra & Smith', 'Oxford University Press', '6th', 7, 'ISSUED'),

('B015-1', '9788131806066', 'Engineering Mechanics', 'R.S. Khurmi', 'S. Chand', '1st', 8, 'LOST'),

('B015-2', '9788131806066', 'Engineering Mechanics', 'R.S. Khurmi', 'S. Chand', '1st', 8, 'ISSUED'),

('B016-1', '9788121916290', 'Strength of Materials', 'R.K. Rajput', 'S. Chand', '5th', 8, 'DAMAGED'),

('B016-2', '9788121916290', 'Strength of Materials', 'R.K. Rajput', 'S. Chand', '5th', 8, 'AVAILABLE');


-- BORROW REQUESTS

INSERT INTO `borrow_requests` (`id`, `student_id`, `book_id`, `request_date`, `status`) VALUES

(1, 'TVE26CE021', 'B005-1', '2026-09-28 10:00:00', 'PENDING'),

(2, 'TVE26EC008', 'B015-1', '2026-10-02 10:00:00', 'PENDING'),

(3, 'TVE26EL018', 'B012-1', '2026-09-01 10:00:00', 'APPROVED'),

(4, 'TVE26EL018', 'B009-1', '2026-09-27 10:00:00', 'PENDING'),

(5, 'TVE26EC005', 'B012-2', '2026-09-24 10:00:00', 'APPROVED'),

(6, 'TVE26CS001', 'B014-1', '2026-09-04 10:00:00', 'APPROVED'),

(7, 'TVE26IE029', 'B006-2', '2026-09-15 10:00:00', 'REJECTED'),

(8, 'TVE26EE015', 'B011-2', '2026-09-16 10:00:00', 'APPROVED'),

(9, 'TVE26CS004', 'B008-2', '2026-09-08 10:00:00', 'PENDING'),

(10, 'TVE26CE024', 'B015-1', '2026-08-20 10:00:00', 'PENDING'),

(11, 'TVE25CS070', 'B001-1', '2026-10-06 23:37:40', 'PENDING');


-- BORROW TRANSACTIONS

INSERT INTO `borrow_transactions` (`id`, `student_id`, `book_id`, `librarian_id`, `issue_date`, `due_date`, `return_date`, `fine`, `status`) VALUES

(1, 'TVE26IE030', 'B003-1', 1, '2026-09-13', '2026-09-27', NULL, 20.00, 'ACTIVE'),

(2, 'TVE26AE012', 'B014-2', 1, '2026-09-01', '2026-09-15', '2026-09-15', 0.00, 'COMPLETED'),

(3, 'TVE26AE011', 'B014-2', 1, '2026-09-02', '2026-09-16', NULL, 0.00, 'ACTIVE'),

(4, 'TVE26IE031', 'B003-1', 2, '2026-09-11', '2026-09-25', '2026-09-25', 0.00, 'COMPLETED'),

(5, 'TVE26EL019', 'B002-1', 2, '2026-09-20', '2026-10-04', NULL, 30.00, 'ACTIVE'),

(6, 'TVE26EL017', 'B007-2', 1, '2026-08-31', '2026-09-14', NULL, 10.00, 'ACTIVE'),

(7, 'TVE26CS002', 'B009-2', 2, '2026-08-28', '2026-09-11', '2026-09-10', 0.00, 'COMPLETED'),

(8, 'TVE26IE030', 'B005-1', 2, '2026-09-04', '2026-09-18', NULL, 10.00, 'ACTIVE'),

(9, 'TVE26EL019', 'B014-2', 1, '2026-09-14', '2026-09-28', '2026-09-27', 0.00, 'COMPLETED'),

(10, 'TVE26CS003', 'B015-2', 1, '2026-08-22', '2026-09-05', NULL, 30.00, 'ACTIVE'),

(11, 'TVE26EL017', 'B015-2', 2, '2026-09-08', '2026-09-22', '2026-09-20', 0.00, 'COMPLETED'),

(12, 'TVE26AE012', 'B015-1', 2, '2026-09-05', '2026-09-19', '2026-09-14', 0.00, 'COMPLETED'),

(13, 'TVE26ME027', 'B002-1', 2, '2026-09-07', '2026-09-21', '2026-09-19', 0.00, 'COMPLETED'),

(14, 'TVE26CE024', 'B015-2', 2, '2026-09-21', '2026-10-05', '2026-10-05', 0.00, 'COMPLETED'),

(15, 'TVE26EL017', 'B009-2', 1, '2026-08-20', '2026-09-03', '2026-08-31', 0.00, 'COMPLETED'),

(16, 'TVE26AE011', 'B006-1', 1, '2026-09-14', '2026-09-28', NULL, 0.00, 'ACTIVE'),

(17, 'TVE26CS003', 'B014-2', 2, '2026-08-17', '2026-08-31', '2026-08-27', 0.00, 'COMPLETED'),

(18, 'TVE26EE016', 'B002-1', 1, '2026-09-01', '2026-09-15', '2026-09-14', 0.00, 'COMPLETED'),

(19, 'TVE26EL019', 'B004-2', 1, '2026-09-21', '2026-10-05', NULL, 0.00, 'ACTIVE'),

(20, 'TVE26IE029', 'B010-2', 1, '2026-09-19', '2026-10-03', '2026-09-29', 0.00, 'COMPLETED'),

(21, 'TVE26CE023', 'B004-1', 2, '2026-08-30', '2026-09-13', '2026-09-12', 0.00, 'COMPLETED'),

(22, 'TVE26IE030', 'B008-1', 1, '2026-09-19', '2026-10-03', NULL, 10.00, 'ACTIVE'),

(23, 'TVE26IE029', 'B004-2', 2, '2026-08-27', '2026-09-10', NULL, 0.00, 'ACTIVE'),

(24, 'TVE26AE009', 'B013-1', 2, '2026-09-17', '2026-10-01', '2026-09-28', 0.00, 'COMPLETED'),

(25, 'TVE26IE029', 'B002-2', 1, '2026-09-17', '2026-10-01', '2026-10-01', 0.00, 'COMPLETED'),

(26, 'TVE26EL020', 'B008-2', 1, '2026-08-21', '2026-09-04', '2026-09-01', 0.00, 'COMPLETED'),

(27, 'TVE26EE015', 'B005-2', 2, '2026-09-13', '2026-09-27', '2026-09-22', 0.00, 'COMPLETED'),

(28, 'TVE26EC006', 'B009-2', 1, '2026-09-09', '2026-09-23', NULL, 30.00, 'ACTIVE'),

(29, 'TVE26CS001', 'B008-2', 2, '2026-08-20', '2026-09-03', '2026-09-02', 0.00, 'COMPLETED'),

(30, 'TVE26AE011', 'B013-2', 1, '2026-08-31', '2026-09-14', NULL, 20.00, 'ACTIVE'),

(31, 'TVE26AE009', 'B004-1', 2, '2026-08-26', '2026-09-09', '2026-09-09', 0.00, 'COMPLETED'),

(32, 'TVE26IE032', 'B011-2', 1, '2026-08-30', '2026-09-13', '2026-09-12', 0.00, 'COMPLETED'),

(33, 'TVE26EL020', 'B001-2', 1, '2026-08-22', '2026-09-05', '2026-09-05', 0.00, 'COMPLETED'),

(34, 'TVE26IE030', 'B003-1', 2, '2026-08-17', '2026-08-31', '2026-08-28', 0.00, 'COMPLETED'),

(35, 'TVE26EL020', 'B012-2', 1, '2026-09-05', '2026-09-19', '2026-09-18', 0.00, 'COMPLETED'),

(36, 'TVE26CS004', 'B002-1', 2, '2026-09-07', '2026-09-21', '2026-09-21', 0.00, 'COMPLETED'),

(37, 'TVE26IE032', 'B012-1', 1, '2026-08-31', '2026-09-14', NULL, 0.00, 'ACTIVE'),

(38, 'TVE26CS003', 'B006-1', 2, '2026-09-01', '2026-09-15', NULL, 20.00, 'ACTIVE'),

(39, 'TVE26CS002', 'B007-2', 1, '2026-09-18', '2026-10-02', NULL, 0.00, 'ACTIVE'),

(40, 'TVE26IE032', 'B008-1', 1, '2026-09-22', '2026-10-06', '2026-10-05', 0.00, 'COMPLETED'),

(41, 'TVE26ME025', 'B006-1', 2, '2026-09-18', '2026-10-02', '2026-10-01', 0.00, 'COMPLETED'),

(42, 'TVE26EC007', 'B006-1', 2, '2026-09-15', '2026-09-29', NULL, 30.00, 'ACTIVE'),

(43, 'TVE26EE014', 'B003-1', 2, '2026-09-22', '2026-10-06', NULL, 20.00, 'ACTIVE'),

(44, 'TVE26EE013', 'B011-1', 1, '2026-09-17', '2026-10-01', '2026-09-26', 0.00, 'COMPLETED'),

(45, 'TVE26CS001', 'B002-1', 2, '2026-08-31', '2026-09-14', '2026-09-09', 0.00, 'COMPLETED'),

(46, 'TVE26IE030', 'B006-2', 2, '2026-09-18', '2026-10-02', '2026-09-27', 0.00, 'COMPLETED'),

(47, 'TVE26CS002', 'B015-1', 2, '2026-09-21', '2026-10-05', '2026-10-01', 0.00, 'COMPLETED'),

(48, 'TVE26IE029', 'B013-1', 2, '2026-09-20', '2026-10-04', '2026-10-04', 0.00, 'COMPLETED'),

(49, 'TVE26AE009', 'B013-2', 2, '2026-08-24', '2026-09-07', '2026-09-02', 0.00, 'COMPLETED'),

(50, 'TVE26EE014', 'B008-2', 1, '2026-08-21', '2026-09-04', '2026-09-02', 0.00, 'COMPLETED'),

(51, 'TVE26IE031', 'B013-2', 2, '2026-09-07', '2026-09-21', '2026-09-17', 0.00, 'COMPLETED'),

(52, 'TVE26CS003', 'B005-2', 2, '2026-09-19', '2026-10-03', '2026-09-28', 0.00, 'COMPLETED'),

(53, 'TVE26AE011', 'B011-2', 2, '2026-09-15', '2026-09-29', NULL, 0.00, 'ACTIVE'),

(54, 'TVE26AE009', 'B010-1', 2, '2026-09-05', '2026-09-19', '2026-09-15', 0.00, 'COMPLETED'),

(55, 'TVE26ME025', 'B010-1', 1, '2026-09-23', '2026-10-07', '2026-10-04', 0.00, 'COMPLETED'),

(56, 'TVE26EE015', 'B007-1', 1, '2026-09-07', '2026-09-21', '2026-09-20', 0.00, 'COMPLETED'),

(57, 'TVE26AE009', 'B002-1', 2, '2026-09-02', '2026-09-16', '2026-09-14', 0.00, 'COMPLETED'),

(58, 'TVE26IE031', 'B012-2', 2, '2026-09-18', '2026-10-02', '2026-09-29', 0.00, 'COMPLETED'),

(59, 'TVE26IE031', 'B003-1', 2, '2026-08-19', '2026-09-02', NULL, 10.00, 'ACTIVE'),

(60, 'TVE26AE010', 'B007-1', 2, '2026-09-02', '2026-09-16', '2026-09-14', 0.00, 'COMPLETED');


-- RESERVATIONS

INSERT INTO `reservations` (`id`, `student_id`, `book_isbn`, `request_date`, `status`) VALUES

(1, 'TVE26AE012', '9780134610993', '2026-09-07 04:30:00', 'PENDING'),

(2, 'TVE26ME028', '9788131806066', '2026-09-02 04:30:00', 'PENDING'),

(3, 'TVE26IE029', '9780262046305', '2026-09-05 04:30:00', 'CANCELLED'),

(4, 'TVE26CS003', '9780078022159', '2026-09-24 04:30:00', 'PENDING'),

(5, 'TVE26CE024', '9780078022159', '2026-09-20 04:30:00', 'CANCELLED'),

(6, 'TVE26IE029', '9781118063330', '2026-09-28 04:30:00', 'FULFILLED'),

(7, 'TVE26AE012', '9780132126953', '2026-09-16 04:30:00', 'PENDING'),

(8, 'TVE26AE011', '9788131806066', '2026-09-22 04:30:00', 'PENDING'),

(9, 'TVE26CE021', '9781118063330', '2026-10-03 04:30:00', 'FULFILLED'),

(10, 'TVE26CS001', '9781492032649', '2026-09-12 04:30:00', 'PENDING');
