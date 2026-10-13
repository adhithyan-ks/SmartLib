package com.college.library.service;

import com.college.library.dao.BorrowRequestDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Reservation;
import com.college.library.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    private StudentDAO mockStudentDAO;
    private BorrowTransactionDAO mockTransactionDAO;
    private ReservationDAO mockReservationDAO;
    private BorrowRequestDAO mockBorrowRequestDAO;
    private StudentService studentService;

    private boolean deleteCalled = false;
    private Map<String, List<BorrowTransaction>> studentTransactions;
    private Map<String, List<BorrowRequest>> studentRequests;
    private Map<String, List<Reservation>> studentReservations;
    private Set<String> existingStudents;
    private Map<String, Student> studentStore;

    @BeforeEach
    void setUp() {
        deleteCalled = false;
        studentTransactions = new HashMap<>();
        studentRequests = new HashMap<>();
        studentReservations = new HashMap<>();
        existingStudents = new HashSet<>();
        studentStore = new HashMap<>();

        Student validStudent = new Student();
        validStudent.setKtuId("VALID_STU");
        validStudent.setName("Test Student");
        validStudent.setStatus("ACTIVE");
        existingStudents.add("VALID_STU");
        studentStore.put("VALID_STU", validStudent);

        mockStudentDAO = new StudentDAO() {
            @Override
            public void create(Student entity) throws LibraryException {
                existingStudents.add(entity.getKtuId());
                studentStore.put(entity.getKtuId(), entity);
            }

            @Override
            public Optional<Student> findById(String id) throws LibraryException {
                if (studentStore.containsKey(id)) {
                    return Optional.of(studentStore.get(id));
                }
                return Optional.empty();
            }

            @Override
            public List<Student> findAll() throws LibraryException {
                return new ArrayList<>(studentStore.values());
            }

            @Override
            public void update(Student entity) throws LibraryException {
                studentStore.put(entity.getKtuId(), entity);
            }

            @Override
            public void delete(String id) throws LibraryException {
                deleteCalled = true;
                existingStudents.remove(id);
                studentStore.remove(id);
            }
        };

        mockTransactionDAO = new BorrowTransactionDAO() {
            @Override
            public void createWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException {}

            @Override
            public void updateWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException {}

            @Override
            public Optional<BorrowTransaction> findActiveByBookId(String bookId) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public List<BorrowTransaction> findByStudentId(String studentId) throws LibraryException {
                return studentTransactions.getOrDefault(studentId, Collections.emptyList());
            }

            @Override
            public int countActiveTransactions() throws LibraryException {
                return 0;
            }

            @Override
            public void create(BorrowTransaction entity) throws LibraryException {}

            @Override
            public Optional<BorrowTransaction> findById(Integer id) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public List<BorrowTransaction> findAll() throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public void update(BorrowTransaction entity) throws LibraryException {}

            @Override
            public void delete(Integer id) throws LibraryException {}
        };

        mockReservationDAO = new ReservationDAO() {
            @Override
            public void createWithConnection(Reservation reservation, Connection conn) throws SQLException {}

            @Override
            public void updateWithConnection(Reservation reservation, Connection conn) throws SQLException {}

            @Override
            public Optional<Reservation> findOldestPendingByIsbn(String isbn, Connection conn) throws SQLException {
                return Optional.empty();
            }

            @Override
            public List<Reservation> findPendingByStudentId(String studentId) throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public List<Reservation> findPendingByIsbn(String isbn) throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public List<Reservation> findByStudentId(String studentId) throws LibraryException {
                return studentReservations.getOrDefault(studentId, Collections.emptyList());
            }

            @Override
            public void create(Reservation entity) throws LibraryException {}

            @Override
            public Optional<Reservation> findById(Integer id) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public List<Reservation> findAll() throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public void update(Reservation entity) throws LibraryException {}

            @Override
            public void delete(Integer id) throws LibraryException {}
        };

        mockBorrowRequestDAO = new BorrowRequestDAO() {
            @Override
            public List<BorrowRequest> findByStudentId(String studentId) throws LibraryException {
                return studentRequests.getOrDefault(studentId, Collections.emptyList());
            }

            @Override
            public List<BorrowRequest> findByStatus(String status) throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public Optional<BorrowRequest> findPendingByStudentAndBook(String studentId, String bookId) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public void create(BorrowRequest entity) throws LibraryException {}

            @Override
            public Optional<BorrowRequest> findById(Integer id) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public List<BorrowRequest> findAll() throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public void update(BorrowRequest entity) throws LibraryException {}

            @Override
            public void delete(Integer id) throws LibraryException {}
        };

        studentService = new StudentService(mockStudentDAO, mockTransactionDAO, mockReservationDAO, mockBorrowRequestDAO);
    }

    @Test
    void testDeleteStudentSuccessWhenNoRelatedRecords() throws Exception {
        studentService.deleteStudent("VALID_STU");
        assertTrue(deleteCalled, "DAO delete should have been invoked when no related records exist.");
    }

    @Test
    void testDeleteStudentBlockedByActiveBorrowTransaction() {
        BorrowTransaction activeTx = new BorrowTransaction();
        activeTx.setStudentId("VALID_STU");
        activeTx.setStatus("ACTIVE");
        activeTx.setIssueDate(LocalDate.now());
        studentTransactions.put("VALID_STU", List.of(activeTx));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("Related records exist"));
        assertTrue(ex.getMessage().contains("active borrowed book(s)"));
        assertTrue(ex.getMessage().contains("preserve borrowing history"));
        assertFalse(deleteCalled, "Student must not be deleted when active borrow transactions exist.");
    }

    @Test
    void testDeleteStudentBlockedByCompletedBorrowTransactionPreservingHistory() {
        BorrowTransaction completedTx = new BorrowTransaction();
        completedTx.setStudentId("VALID_STU");
        completedTx.setStatus("COMPLETED");
        completedTx.setIssueDate(LocalDate.now().minusDays(30));
        completedTx.setReturnDate(LocalDate.now().minusDays(16));
        studentTransactions.put("VALID_STU", List.of(completedTx));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("Related records exist"));
        assertTrue(ex.getMessage().contains("borrowing history record(s)"));
        assertTrue(ex.getMessage().contains("preserve borrowing history"));
        assertFalse(deleteCalled, "Student must not be deleted when borrowing history exists.");
    }

    @Test
    void testDeleteStudentBlockedByBorrowRequest() {
        BorrowRequest req = new BorrowRequest();
        req.setStudentId("VALID_STU");
        req.setBookId("B1001");
        req.setStatus("PENDING");
        req.setRequestDate(LocalDateTime.now());
        studentRequests.put("VALID_STU", List.of(req));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("Related records exist"));
        assertTrue(ex.getMessage().contains("borrow request(s)"));
        assertFalse(deleteCalled, "Student must not be deleted when borrow requests exist.");
    }

    @Test
    void testDeleteStudentBlockedByReservation() {
        Reservation res = new Reservation();
        res.setStudentId("VALID_STU");
        res.setBookIsbn("978-0134685991");
        res.setStatus("PENDING");
        studentReservations.put("VALID_STU", List.of(res));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("Related records exist"));
        assertTrue(ex.getMessage().contains("reservation(s)"));
        assertFalse(deleteCalled, "Student must not be deleted when reservations exist.");
    }

    @Test
    void testDeleteStudentBlockedByMultipleRelatedRecords() {
        BorrowTransaction activeTx = new BorrowTransaction();
        activeTx.setStatus("ACTIVE");
        BorrowTransaction pastTx = new BorrowTransaction();
        pastTx.setStatus("COMPLETED");
        studentTransactions.put("VALID_STU", List.of(activeTx, pastTx));

        BorrowRequest req = new BorrowRequest();
        req.setStatus("PENDING");
        studentRequests.put("VALID_STU", List.of(req));

        Reservation res = new Reservation();
        res.setStatus("PENDING");
        studentReservations.put("VALID_STU", List.of(res));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        String msg = ex.getMessage();
        assertTrue(msg.contains("active borrowed book(s)"));
        assertTrue(msg.contains("borrowing history"));
        assertTrue(msg.contains("borrow request(s)"));
        assertTrue(msg.contains("reservation(s)"));
        assertFalse(deleteCalled);
    }

    @Test
    void testDeleteStudentFallbackToStudentDAOSummary() {
        StudentDAO customDAO = new StudentDAO() {
            @Override
            public Optional<Student> findById(String id) {
                return Optional.of(new Student());
            }
            @Override
            public void create(Student s) {}
            @Override
            public List<Student> findAll() { return Collections.emptyList(); }
            @Override
            public void update(Student s) {}
            @Override
            public void delete(String id) {}
            @Override
            public List<String> getRelatedRecordsSummary(String id) {
                return List.of("1 active transaction(s) via DAO fallback");
            }
        };

        StudentService singleDaoService = new StudentService(customDAO);
        LibraryException ex = assertThrows(LibraryException.class, () -> singleDaoService.deleteStudent("ANY_ID"));
        assertTrue(ex.getMessage().contains("active transaction(s) via DAO fallback"));
    }

    @Test
    void testDeleteStudentNotFound() {
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("NON_EXISTENT"));
        assertTrue(ex.getMessage().contains("Student not found"));
        assertFalse(deleteCalled);
    }

    @Test
    void testDeleteStudentEmptyOrNullId() {
        assertThrows(LibraryException.class, () -> studentService.deleteStudent(null));
        assertThrows(LibraryException.class, () -> studentService.deleteStudent("   "));
        assertFalse(deleteCalled);
    }

    @Test
    void testAddStudentValidation() throws Exception {
        Student newStu = new Student("NEW_KTU", "hash", "New Student", "CSE", 1, "2024", "new@test.com", "1234");
        studentService.addStudent(newStu);
        assertTrue(existingStudents.contains("NEW_KTU"));

        // Duplicate
        assertThrows(LibraryException.class, () -> studentService.addStudent(newStu));
    }

    @Test
    void testDeactivateStudentSuccess() throws Exception {
        studentService.deactivateStudent("VALID_STU");
        Student s = studentStore.get("VALID_STU");
        assertNotNull(s);
        assertEquals("INACTIVE", s.getStatus());
        assertFalse(s.isActive());
    }

    @Test
    void testDeactivateStudentAlreadyInactive() throws Exception {
        studentService.deactivateStudent("VALID_STU");
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deactivateStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("already deactivated"));
    }

    @Test
    void testActivateStudentSuccess() throws Exception {
        studentService.deactivateStudent("VALID_STU");
        studentService.activateStudent("VALID_STU");
        Student s = studentStore.get("VALID_STU");
        assertNotNull(s);
        assertEquals("ACTIVE", s.getStatus());
        assertTrue(s.isActive());
    }

    @Test
    void testActivateStudentAlreadyActive() {
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.activateStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("already active"));
    }

    @Test
    void testDeactivateStudentNotFound() {
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deactivateStudent("UNKNOWN_STU"));
        assertTrue(ex.getMessage().contains("Student not found"));
    }

    @Test
    void testActivateStudentNotFound() {
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.activateStudent("UNKNOWN_STU"));
        assertTrue(ex.getMessage().contains("Student not found"));
    }

    @Test
    void testDeactivateStudentEmptyOrNullId() {
        assertThrows(LibraryException.class, () -> studentService.deactivateStudent(null));
        assertThrows(LibraryException.class, () -> studentService.deactivateStudent("   "));
    }

    @Test
    void testActivateStudentEmptyOrNullId() {
        assertThrows(LibraryException.class, () -> studentService.activateStudent(null));
        assertThrows(LibraryException.class, () -> studentService.activateStudent("   "));
    }
}
