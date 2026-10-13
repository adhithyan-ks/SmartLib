package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.dao.BorrowRequestDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.dto.ActiveBorrowingDTO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.BorrowRequest;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Category;
import com.college.library.model.Reservation;
import com.college.library.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BorrowServiceTest {

    private BorrowService borrowService;
    private boolean transactionCreated = false;
    private boolean transactionUpdated = false;
    private boolean bookStatusUpdated = false;
    private boolean reservationFulfilled = false;

    private List<ActiveBorrowingDTO> mockActiveList;
    private BorrowTransaction activeTx;
    private Book issuedBook;
    private final LocalDate fixedToday = LocalDate.of(2026, 10, 13);
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        transactionCreated = false;
        transactionUpdated = false;
        bookStatusUpdated = false;
        reservationFulfilled = false;

        fixedClock = Clock.fixed(fixedToday.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        mockActiveList = new ArrayList<>();

        activeTx = new BorrowTransaction();
        activeTx.setId(101);
        activeTx.setStudentId("VALID_STU");
        activeTx.setBookId("ISSUED_BOOK");
        activeTx.setIssueDate(fixedToday.minusDays(5));
        activeTx.setDueDate(fixedToday.minusDays(2)); // 2 days overdue
        activeTx.setStatus("ACTIVE");

        issuedBook = new Book();
        issuedBook.setAccessionId("ISSUED_BOOK");
        issuedBook.setIsbn("ISBN123");
        issuedBook.setTitle("Domain-Driven Design");
        issuedBook.setStatus("ISSUED");

        BorrowTransactionDAO mockTxDAO = new BorrowTransactionDAO() {
            @Override public void createWithConnection(BorrowTransaction t, Connection c) { transactionCreated = true; }
            @Override public void updateWithConnection(BorrowTransaction t, Connection c) {
                transactionUpdated = true;
                if (activeTx != null && activeTx.getId() == t.getId()) {
                    activeTx.setStatus(t.getStatus());
                    activeTx.setReturnDate(t.getReturnDate());
                    activeTx.setFine(t.getFine());
                }
                mockActiveList.removeIf(b -> b.getTransactionId() == t.getId() || b.getBookAccessionId().equals(t.getBookId()));
            }
            @Override public Optional<BorrowTransaction> findActiveByBookId(String id) {
                if ("ISSUED_BOOK".equals(id) && activeTx != null && "ACTIVE".equals(activeTx.getStatus())) {
                    return Optional.of(activeTx);
                }
                return Optional.empty();
            }
            @Override public List<BorrowTransaction> findByStudentId(String id) { return null; }
            @Override public int countActiveTransactions() { return mockActiveList.size(); }
            @Override public void create(BorrowTransaction t) {}
            @Override public Optional<BorrowTransaction> findById(Integer id) { return Optional.empty(); }
            @Override public List<BorrowTransaction> findAll() { return null; }
            @Override public void update(BorrowTransaction t) {}
            @Override public void delete(Integer id) {}
            @Override public List<ActiveBorrowingDTO> findAllActiveBorrowings() {
                return new ArrayList<>(mockActiveList);
            }
        };

        BookDAO mockBookDAO = new BookDAO() {
            @Override public void updateStatus(String id, String s, Connection c) {
                bookStatusUpdated = true;
                if (issuedBook != null && issuedBook.getAccessionId().equals(id)) {
                    issuedBook.setStatus(s);
                }
            }
            @Override public int updateStatusIfCondition(String i, String n, String o, Connection c) { return "AVAILABLE_BOOK".equals(i) ? 1 : 0; }
            @Override public List<Category> getAllCategories() { return new ArrayList<>(); }
            @Override public List<Book> findByIsbn(String isbn) { return new ArrayList<>(); }
            @Override public void create(Book b) {}
            @Override public Optional<Book> findById(String id) {
                if ("ISSUED_BOOK".equals(id)) {
                    return Optional.of(issuedBook);
                }
                Book b = new Book();
                b.setAccessionId(id);
                b.setIsbn("ISBN123");
                b.setStatus("AVAILABLE_BOOK".equals(id) ? "AVAILABLE" : "ISSUED");
                return Optional.of(b);
            }
            @Override public List<Book> findAll() { return null; }
            @Override public void update(Book b) {}
            @Override public void delete(String id) {}
        };

        StudentDAO mockStudentDAO = new StudentDAO() {
            @Override public void create(Student s) {}
            @Override public Optional<Student> findById(String id) {
                if ("INACTIVE_STU".equals(id)) {
                    Student s = new Student();
                    s.setStatus("INACTIVE");
                    return Optional.of(s);
                }
                if ("VALID_STU".equals(id)) {
                    Student s = new Student();
                    s.setKtuId("VALID_STU");
                    s.setName("Alice Smith");
                    s.setStatus("ACTIVE");
                    return Optional.of(s);
                }
                return Optional.empty();
            }
            @Override public List<Student> findAll() { return null; }
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
            @Override public List<Reservation> findPendingByStudentId(String s) { return null; }
            @Override public List<Reservation> findPendingByIsbn(String isbn) { return new ArrayList<>(); }
            @Override public void create(Reservation r) {}
            @Override public Optional<Reservation> findById(Integer id) { return Optional.empty(); }
            @Override public List<Reservation> findAll() { return null; }
            @Override public void update(Reservation r) {}
            @Override public void delete(Integer id) {}
        };

        BorrowRequestDAO mockBorrowReqDAO = new BorrowRequestDAO() {
            @Override public void create(BorrowRequest r) {}
            @Override public Optional<BorrowRequest> findById(Integer id) { return Optional.empty(); }
            @Override public List<BorrowRequest> findAll() { return null; }
            @Override public void update(BorrowRequest r) {}
            @Override public void delete(Integer id) {}
            @Override public List<BorrowRequest> findByStudentId(String id) { return null; }
            @Override public List<BorrowRequest> findByStatus(String status) { return null; }
            @Override public Optional<BorrowRequest> findPendingByStudentAndBook(String s, String b) { return Optional.empty(); }
        };

        borrowService = new BorrowService(mockTxDAO, mockBookDAO, mockStudentDAO, mockResDAO, mockBorrowReqDAO, fixedClock);
    }

    // --- Existing Core Borrow Workflow Tests ---

    @Test
    void testIssueBookSuccess() throws Exception {
        borrowService.issueBook("VALID_STU", "AVAILABLE_BOOK", 1);
        assertTrue(transactionCreated);
    }

    @Test
    void testIssueBookFailsIfNotAvailable() {
        assertThrows(LibraryException.class, () -> borrowService.issueBook("VALID_STU", "ISSUED_BOOK", 1));
    }

    @Test
    void testIssueBookFailsIfStudentInactive() {
        LibraryException ex = assertThrows(LibraryException.class, () -> borrowService.issueBook("INACTIVE_STU", "AVAILABLE_BOOK", 1));
        assertTrue(ex.getMessage().contains("Student account is inactive"));
    }

    @Test
    void testRequestToBorrowFailsIfStudentInactive() {
        LibraryException ex = assertThrows(LibraryException.class, () -> borrowService.requestToBorrow("INACTIVE_STU", "AVAILABLE_BOOK"));
        assertTrue(ex.getMessage().contains("Student account is inactive"));
    }

    @Test
    void testRequestToBorrowSuccessForActiveStudent() throws Exception {
        borrowService.requestToBorrow("VALID_STU", "AVAILABLE_BOOK");
    }

    // --- 1. Retrieving All Active Borrowings ---

    @Test
    void testGetAllActiveBorrowings() throws Exception {
        ActiveBorrowingDTO item1 = new ActiveBorrowingDTO(1, "TVE23CS001", "Alice Smith", "B1001", "Effective Java",
                fixedToday.minusDays(10), fixedToday.plusDays(4), "ACTIVE");
        ActiveBorrowingDTO item2 = new ActiveBorrowingDTO(2, "TVE23CS002", "Bob Jones", "B1002", "Design Patterns",
                fixedToday.minusDays(5), fixedToday.plusDays(9), "ACTIVE");
        mockActiveList.add(item1);
        mockActiveList.add(item2);

        List<ActiveBorrowingDTO> result = borrowService.getAllActiveBorrowings();
        assertEquals(2, result.size());
        assertEquals("TVE23CS001", result.get(0).getStudentId());
        assertEquals("TVE23CS002", result.get(1).getStudentId());
    }

    // --- 2. Excluding Completed Transactions from Active List ---

    @Test
    void testExcludingCompletedTransactionsFromActiveList() throws Exception {
        ActiveBorrowingDTO activeItem = new ActiveBorrowingDTO(1, "TVE23CS001", "Alice Smith", "B1001", "Effective Java",
                fixedToday.minusDays(10), fixedToday.minusDays(2), "ACTIVE");
        ActiveBorrowingDTO completedItem = new ActiveBorrowingDTO(2, "TVE23CS002", "Bob Jones", "B1002", "Design Patterns",
                fixedToday.minusDays(20), fixedToday.minusDays(6), "COMPLETED");
        mockActiveList.add(activeItem);
        mockActiveList.add(completedItem);

        List<ActiveBorrowingDTO> result = borrowService.getAllActiveBorrowings();
        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
        assertEquals(1, result.get(0).getTransactionId());
        // Verify no fines calculated for completed transaction
        assertEquals(0.0, completedItem.getCurrentFine());
    }

    // --- 3. Correctly Resolving Student Names and Book Titles ---

    @Test
    void testResolvingStudentNamesAndBookTitles() throws Exception {
        ActiveBorrowingDTO item = new ActiveBorrowingDTO(1, "TVE23CS001", "Alice Smith", "B1003", "Domain-Driven Design",
                fixedToday.minusDays(7), fixedToday.plusDays(7), "ACTIVE");
        mockActiveList.add(item);

        List<ActiveBorrowingDTO> result = borrowService.getAllActiveBorrowings();
        assertEquals(1, result.size());
        assertEquals("Alice Smith", result.get(0).getStudentName());
        assertEquals("Domain-Driven Design", result.get(0).getBookTitle());
    }

    // --- 4. Zero Fine on the Due Date ---

    @Test
    void testZeroFineOnDueDate() throws Exception {
        ActiveBorrowingDTO dueTodayItem = new ActiveBorrowingDTO(1, "TVE23CS001", "Alice Smith", "B1001", "Effective Java",
                fixedToday.minusDays(14), fixedToday, "ACTIVE"); // dueDate == fixedToday (2026-10-13)
        mockActiveList.add(dueTodayItem);

        List<ActiveBorrowingDTO> result = borrowService.getAllActiveBorrowings();
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getDaysOverdue());
        assertEquals(0.0, result.get(0).getCurrentFine(), 0.001);
    }

    // --- 5. ₹10 Fine for One Overdue Day ---

    @Test
    void testFineForOneOverdueDay() throws Exception {
        ActiveBorrowingDTO oneDayOverdue = new ActiveBorrowingDTO(1, "TVE23CS001", "Alice Smith", "B1001", "Effective Java",
                fixedToday.minusDays(15), fixedToday.minusDays(1), "ACTIVE"); // due 2026-10-12, today is 2026-10-13
        mockActiveList.add(oneDayOverdue);

        List<ActiveBorrowingDTO> result = borrowService.getAllActiveBorrowings();
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getDaysOverdue());
        assertEquals(10.0, result.get(0).getCurrentFine(), 0.001);
    }

    // --- 6. ₹30 Fine for Three Overdue Days ---

    @Test
    void testFineForThreeOverdueDays() throws Exception {
        ActiveBorrowingDTO threeDaysOverdue = new ActiveBorrowingDTO(1, "TVE23CS001", "Alice Smith", "B1001", "Effective Java",
                fixedToday.minusDays(17), fixedToday.minusDays(3), "ACTIVE"); // due 2026-10-10, today is 2026-10-13
        mockActiveList.add(threeDaysOverdue);

        List<ActiveBorrowingDTO> result = borrowService.getAllActiveBorrowings();
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getDaysOverdue());
        assertEquals(30.0, result.get(0).getCurrentFine(), 0.001);
    }

    // --- 7. Correct Overdue, Due Today, and All Active Filters ---

    @Test
    void testFiltersOverdueDueTodayAllActiveAndSearch() throws Exception {
        ActiveBorrowingDTO overdueItem = new ActiveBorrowingDTO(1, "TVE23CS001", "Alice Smith", "B1001", "Effective Java",
                fixedToday.minusDays(16), fixedToday.minusDays(2), "ACTIVE"); // due 2026-10-11
        ActiveBorrowingDTO dueTodayItem = new ActiveBorrowingDTO(2, "TVE23CS002", "Bob Jones", "B1002", "Design Patterns",
                fixedToday.minusDays(14), fixedToday, "ACTIVE"); // due 2026-10-13
        ActiveBorrowingDTO upcomingItem = new ActiveBorrowingDTO(3, "TVE23CS003", "Charlie Brown", "B1003", "Clean Architecture",
                fixedToday.minusDays(4), fixedToday.plusDays(10), "ACTIVE"); // due 2026-10-23
        mockActiveList.add(upcomingItem);
        mockActiveList.add(overdueItem);
        mockActiveList.add(dueTodayItem);

        // 1. All Active (sorted: overdue first, then earliest upcoming due dates next)
        List<ActiveBorrowingDTO> all = borrowService.getAllActiveBorrowings();
        assertEquals(3, all.size());
        assertEquals(1, all.get(0).getTransactionId()); // overdue item first
        assertEquals(2, all.get(1).getTransactionId()); // due today next
        assertEquals(3, all.get(2).getTransactionId()); // upcoming next

        // 2. Filter: All Active
        List<ActiveBorrowingDTO> filterAll = borrowService.filterActiveBorrowings(all, "All Active", "");
        assertEquals(3, filterAll.size());

        // 3. Filter: Overdue
        List<ActiveBorrowingDTO> filterOverdue = borrowService.filterActiveBorrowings(all, "Overdue", "");
        assertEquals(1, filterOverdue.size());
        assertEquals("B1001", filterOverdue.get(0).getBookAccessionId());

        // 4. Filter: Due Today
        List<ActiveBorrowingDTO> filterDueToday = borrowService.filterActiveBorrowings(all, "Due Today", "");
        assertEquals(1, filterDueToday.size());
        assertEquals("B1002", filterDueToday.get(0).getBookAccessionId());

        // 5. Search by Student ID
        List<ActiveBorrowingDTO> searchStudentId = borrowService.filterActiveBorrowings(all, "All Active", "TVE23CS002");
        assertEquals(1, searchStudentId.size());
        assertEquals("Bob Jones", searchStudentId.get(0).getStudentName());

        // 6. Search by Student Name
        List<ActiveBorrowingDTO> searchStudentName = borrowService.filterActiveBorrowings(all, "All Active", "charlie");
        assertEquals(1, searchStudentName.size());
        assertEquals("Charlie Brown", searchStudentName.get(0).getStudentName());

        // 7. Search by Book Accession ID
        List<ActiveBorrowingDTO> searchBookId = borrowService.filterActiveBorrowings(all, "All Active", "b1001");
        assertEquals(1, searchBookId.size());
        assertEquals("Effective Java", searchBookId.get(0).getBookTitle());

        // 8. Search by Book Title
        List<ActiveBorrowingDTO> searchTitle = borrowService.filterActiveBorrowings(all, "All Active", "design patterns");
        assertEquals(1, searchTitle.size());
        assertEquals("Bob Jones", searchTitle.get(0).getStudentName());
    }

    // --- 8. Successful Return Removing Transaction from Active List ---

    @Test
    void testSuccessfulReturnRemovesTransactionFromActiveList() throws Exception {
        ActiveBorrowingDTO activeDTO = new ActiveBorrowingDTO(101, "VALID_STU", "Alice Smith", "ISSUED_BOOK", "Domain-Driven Design",
                fixedToday.minusDays(5), fixedToday.minusDays(2), "ACTIVE");
        mockActiveList.add(activeDTO);

        assertEquals(1, borrowService.getAllActiveBorrowings().size());

        String resultMsg = borrowService.returnBook("ISSUED_BOOK", 1);
        assertNotNull(resultMsg);
        assertTrue(resultMsg.contains("successfully returned"));
        assertTrue(transactionUpdated);
        assertTrue(bookStatusUpdated);
        assertEquals("COMPLETED", activeTx.getStatus());
        assertEquals("AVAILABLE", issuedBook.getStatus());

        // After return, transaction is no longer in active list
        List<ActiveBorrowingDTO> remainingActive = borrowService.getAllActiveBorrowings();
        assertTrue(remainingActive.isEmpty());
    }

    // --- 9. Failed Return Handling Without Corrupting Transaction or Book Status ---

    @Test
    void testFailedReturnDoesNotCorruptStatus() {
        ActiveBorrowingDTO activeDTO = new ActiveBorrowingDTO(101, "VALID_STU", "Alice Smith", "ISSUED_BOOK", "Domain-Driven Design",
                fixedToday.minusDays(5), fixedToday.minusDays(2), "ACTIVE");
        mockActiveList.add(activeDTO);

        // Attempting to return a book that has no active borrowing
        LibraryException ex = assertThrows(LibraryException.class, () -> borrowService.returnBook("NON_EXISTENT_BOOK", 1));
        assertTrue(ex.getMessage().contains("No active borrow transaction found"));

        // Verify existing active transaction and book status remain uncorrupted
        assertEquals("ACTIVE", activeTx.getStatus());
        assertEquals("ISSUED", issuedBook.getStatus());
        assertEquals(1, mockActiveList.size());
    }

    // --- 10. Empty Active-Borrowings Result Handling ---

    @Test
    void testEmptyActiveBorrowingsResultHandling() throws Exception {
        mockActiveList.clear();

        List<ActiveBorrowingDTO> result = borrowService.getAllActiveBorrowings();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        List<ActiveBorrowingDTO> filtered = borrowService.filterActiveBorrowings(result, "Overdue", "test");
        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }
}
