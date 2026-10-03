package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Reservation;
import com.college.library.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BorrowServiceTest {

    private BorrowService borrowService;
    private boolean transactionCreated = false;
    private boolean transactionUpdated = false;
    private boolean bookStatusUpdated = false;
    private boolean reservationFulfilled = false;

    @BeforeEach
    void setUp() {
        transactionCreated = false;
        transactionUpdated = false;
        bookStatusUpdated = false;
        reservationFulfilled = false;

        BorrowTransactionDAO mockTxDAO = new BorrowTransactionDAO() {
            @Override public void createWithConnection(BorrowTransaction t, Connection c) { transactionCreated = true; }
            @Override public void updateWithConnection(BorrowTransaction t, Connection c) { transactionUpdated = true; }
            @Override public Optional<BorrowTransaction> findActiveByBookId(String id) {
                if ("ISSUED_BOOK".equals(id)) {
                    BorrowTransaction bt = new BorrowTransaction();
                    bt.setDueDate(LocalDate.now().minusDays(2)); // 2 days overdue
                    return Optional.of(bt);
                }
                return Optional.empty();
            }
            @Override public java.util.List<BorrowTransaction> findByStudentId(String id) { return null; }
            @Override public int countActiveTransactions() { return 0; }
            @Override public void create(BorrowTransaction t) {}
            @Override public Optional<BorrowTransaction> findById(Integer id) { return Optional.empty(); }
            @Override public java.util.List<BorrowTransaction> findAll() { return null; }
            @Override public void update(BorrowTransaction t) {}
            @Override public void delete(Integer id) {}
        };

        BookDAO mockBookDAO = new BookDAO() {
            @Override public void updateStatus(String id, String s, Connection c) { bookStatusUpdated = true; }
            @Override public int updateStatusIfCondition(String i, String n, String o, Connection c) { return "AVAILABLE_BOOK".equals(i) ? 1 : 0; }
            @Override public java.util.List<com.college.library.model.Category> getAllCategories() { return new java.util.ArrayList<>(); }
            @Override public java.util.List<Book> findByIsbn(String isbn) { return new java.util.ArrayList<>(); }
            @Override public void create(Book b) {}
            @Override public Optional<Book> findById(String id) {
                Book b = new Book();
                b.setIsbn("ISBN123");
                b.setStatus("AVAILABLE_BOOK".equals(id) ? "AVAILABLE" : "ISSUED");
                return Optional.of(b);
            }
            @Override public java.util.List<Book> findAll() { return null; }
            @Override public void update(Book b) {}
            @Override public void delete(String id) {}
        };

        StudentDAO mockStudentDAO = new StudentDAO() {
            @Override public void create(Student s) {}
            @Override public Optional<Student> findById(String id) {
                return "VALID_STU".equals(id) ? Optional.of(new Student()) : Optional.empty();
            }
            @Override public java.util.List<Student> findAll() { return null; }
            @Override public void update(Student s) {}
            @Override public void delete(String id) {}
        };

        ReservationDAO mockResDAO = new ReservationDAO() {
            @Override public void createWithConnection(Reservation r, Connection c) {}
            @Override public void updateWithConnection(Reservation r, Connection c) { reservationFulfilled = true; }
            @Override public Optional<Reservation> findOldestPendingByIsbn(String isbn, Connection c) {
                if ("RESERVE_ME".equals(isbn)) {
                    Reservation r = new Reservation();
                    r.setStudentId("WAITING_STU");
                    return Optional.of(r);
                }
                return Optional.empty();
            }
            @Override public java.util.List<Reservation> findPendingByStudentId(String s) { return null; }
            @Override public java.util.List<Reservation> findPendingByIsbn(String isbn) { return new java.util.ArrayList<>(); }
            @Override public void create(Reservation r) {}
            @Override public Optional<Reservation> findById(Integer id) { return Optional.empty(); }
            @Override public java.util.List<Reservation> findAll() { return null; }
            @Override public void update(Reservation r) {}
            @Override public void delete(Integer id) {}
        };

        borrowService = new BorrowService(mockTxDAO, mockBookDAO, mockStudentDAO, mockResDAO);
    }

    @Test
    void testIssueBookSuccess() throws Exception {
        borrowService.issueBook("VALID_STU", "AVAILABLE_BOOK", 1);
        assertTrue(transactionCreated);
    }

    @Test
    void testIssueBookFailsIfNotAvailable() {
        assertThrows(LibraryException.class, () -> borrowService.issueBook("VALID_STU", "ISSUED_BOOK", 1));
    }
}
