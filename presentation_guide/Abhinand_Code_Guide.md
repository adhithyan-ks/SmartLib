# BorrowTransactionDAO.java
**Path:** `src/main/java/com/college/library/dao/BorrowTransactionDAO.java`

```java
package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BorrowTransactionDAO extends CrudDAO<BorrowTransaction, Integer> {
    void createWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException;
    void updateWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException;
    Optional<BorrowTransaction> findActiveByBookId(String bookId) throws LibraryException;
    List<BorrowTransaction> findByStudentId(String studentId) throws LibraryException;
    int countActiveTransactions() throws LibraryException;
}
```

# BorrowTransactionDAOImpl.java
**Path:** `src/main/java/com/college/library/dao/impl/BorrowTransactionDAOImpl.java`

```java
package com.college.library.dao.impl;

import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowTransaction;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowTransactionDAOImpl implements BorrowTransactionDAO {

    @Override
    public void create(BorrowTransaction transaction) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            createWithConnection(transaction, conn);
        } catch (SQLException e) {
            throw new LibraryException("Error creating borrow transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public void createWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException {
        String sql = "INSERT INTO borrow_transactions (student_id, book_id, librarian_id, issue_date, due_date, return_date, fine, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, transaction.getStudentId());
            pstmt.setString(2, transaction.getBookId());
            pstmt.setInt(3, transaction.getLibrarianId());
            pstmt.setDate(4, transaction.getIssueDate() != null ? Date.valueOf(transaction.getIssueDate()) : null);
            pstmt.setDate(5, transaction.getDueDate() != null ? Date.valueOf(transaction.getDueDate()) : null);
            pstmt.setDate(6, transaction.getReturnDate() != null ? Date.valueOf(transaction.getReturnDate()) : null);
            pstmt.setDouble(7, transaction.getFine());
            pstmt.setString(8, transaction.getStatus());
            
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Optional<BorrowTransaction> findById(Integer id) throws LibraryException {
        String sql = "SELECT * FROM borrow_transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBorrowTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding transaction by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<BorrowTransaction> findAll() throws LibraryException {
        List<BorrowTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM borrow_transactions";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToBorrowTransaction(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving all transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    @Override
    public void update(BorrowTransaction transaction) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            updateWithConnection(transaction, conn);
        } catch (SQLException e) {
            throw new LibraryException("Error updating transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException {
        String sql = "UPDATE borrow_transactions SET student_id=?, book_id=?, librarian_id=?, issue_date=?, due_date=?, return_date=?, fine=?, status=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getStudentId());
            pstmt.setString(2, transaction.getBookId());
            pstmt.setInt(3, transaction.getLibrarianId());
            pstmt.setDate(4, transaction.getIssueDate() != null ? Date.valueOf(transaction.getIssueDate()) : null);
            pstmt.setDate(5, transaction.getDueDate() != null ? Date.valueOf(transaction.getDueDate()) : null);
            pstmt.setDate(6, transaction.getReturnDate() != null ? Date.valueOf(transaction.getReturnDate()) : null);
            pstmt.setDouble(7, transaction.getFine());
            pstmt.setString(8, transaction.getStatus());
            pstmt.setInt(9, transaction.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws LibraryException {
        String sql = "DELETE FROM borrow_transactions WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<BorrowTransaction> findActiveByBookId(String bookId) throws LibraryException {
        String sql = "SELECT * FROM borrow_transactions WHERE book_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBorrowTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding active transaction by book ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<BorrowTransaction> findByStudentId(String studentId) throws LibraryException {
        List<BorrowTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM borrow_transactions WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToBorrowTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving transactions for student: " + e.getMessage(), e);
        }
        return transactions;
    }

    @Override
    public int countActiveTransactions() throws LibraryException {
        String sql = "SELECT COUNT(*) FROM borrow_transactions WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error counting active transactions: " + e.getMessage(), e);
        }
        return 0;
    }

    private BorrowTransaction mapResultSetToBorrowTransaction(ResultSet rs) throws SQLException {
        BorrowTransaction bt = new BorrowTransaction();
        bt.setId(rs.getInt("id"));
        bt.setStudentId(rs.getString("student_id"));
        bt.setBookId(rs.getString("book_id"));
        bt.setLibrarianId(rs.getInt("librarian_id"));
        
        Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) bt.setIssueDate(issueDate.toLocalDate());
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) bt.setDueDate(dueDate.toLocalDate());
        
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) bt.setReturnDate(returnDate.toLocalDate());
        
        bt.setFine(rs.getDouble("fine"));
        bt.setStatus(rs.getString("status"));
        return bt;
    }
}
```

# BorrowRequestDAO.java
**Path:** `src/main/java/com/college/library/dao/BorrowRequestDAO.java`

```java
package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;

import java.util.List;
import java.util.Optional;

public interface BorrowRequestDAO extends CrudDAO<BorrowRequest, Integer> {
    List<BorrowRequest> findByStudentId(String studentId) throws LibraryException;
    List<BorrowRequest> findByStatus(String status) throws LibraryException;
    Optional<BorrowRequest> findPendingByStudentAndBook(String studentId, String bookId) throws LibraryException;
}
```

# BorrowRequestDAOImpl.java
**Path:** `src/main/java/com/college/library/dao/impl/BorrowRequestDAOImpl.java`

```java
package com.college.library.dao.impl;

import com.college.library.dao.BorrowRequestDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowRequestDAOImpl implements BorrowRequestDAO {

    @Override
    public void create(BorrowRequest req) throws LibraryException {
        String sql = "INSERT INTO borrow_requests (student_id, book_id, request_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            pstmt.setString(1, req.getStudentId());
            pstmt.setString(2, req.getBookId());
            pstmt.setTimestamp(3, Timestamp.valueOf(req.getRequestDate()));
            pstmt.setString(4, req.getStatus());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    req.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error creating borrow request: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<BorrowRequest> findById(Integer id) throws LibraryException {
        String sql = "SELECT * FROM borrow_requests WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding borrow request: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<BorrowRequest> findAll() throws LibraryException {
        List<BorrowRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_requests";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding all borrow requests: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(BorrowRequest req) throws LibraryException {
        String sql = "UPDATE borrow_requests SET student_id=?, book_id=?, request_date=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, req.getStudentId());
            pstmt.setString(2, req.getBookId());
            pstmt.setTimestamp(3, Timestamp.valueOf(req.getRequestDate()));
            pstmt.setString(4, req.getStatus());
            pstmt.setInt(5, req.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error updating borrow request: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws LibraryException {
        String sql = "DELETE FROM borrow_requests WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting borrow request: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowRequest> findByStudentId(String studentId) throws LibraryException {
        List<BorrowRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_requests WHERE student_id = ? ORDER BY request_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding borrow requests by student: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<BorrowRequest> findByStatus(String status) throws LibraryException {
        List<BorrowRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_requests WHERE status = ? ORDER BY request_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding borrow requests by status: " + e.getMessage(), e);
        }
        return list;
    }
    
    @Override
    public Optional<BorrowRequest> findPendingByStudentAndBook(String studentId, String bookId) throws LibraryException {
        String sql = "SELECT * FROM borrow_requests WHERE student_id = ? AND book_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding pending request: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private BorrowRequest mapRow(ResultSet rs) throws SQLException {
        BorrowRequest r = new BorrowRequest();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getString("student_id"));
        r.setBookId(rs.getString("book_id"));
        r.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
        r.setStatus(rs.getString("status"));
        return r;
    }
}
```

# ReservationDAO.java
**Path:** `src/main/java/com/college/library/dao/ReservationDAO.java`

```java
package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.Reservation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO extends CrudDAO<Reservation, Integer> {
    void createWithConnection(Reservation reservation, Connection conn) throws SQLException;
    void updateWithConnection(Reservation reservation, Connection conn) throws SQLException;
    Optional<Reservation> findOldestPendingByIsbn(String isbn, Connection conn) throws SQLException;
    List<Reservation> findPendingByStudentId(String studentId) throws LibraryException;
    List<Reservation> findPendingByIsbn(String isbn) throws LibraryException;
    default List<Reservation> findByStudentId(String studentId) throws LibraryException {
        return Collections.emptyList();
    }
}
```

# ReservationDAOImpl.java
**Path:** `src/main/java/com/college/library/dao/impl/ReservationDAOImpl.java`

```java
package com.college.library.dao.impl;

import com.college.library.dao.ReservationDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Reservation;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAOImpl implements ReservationDAO {

    @Override
    public void create(Reservation reservation) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            createWithConnection(reservation, conn);
        } catch (SQLException e) {
            throw new LibraryException("Error creating reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public void createWithConnection(Reservation reservation, Connection conn) throws SQLException {
        String sql = "INSERT INTO reservations (student_id, book_isbn, request_date, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, reservation.getStudentId());
            pstmt.setString(2, reservation.getBookIsbn());
            pstmt.setTimestamp(3, reservation.getRequestDate() != null ? Timestamp.valueOf(reservation.getRequestDate()) : new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, reservation.getStatus());
            
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Optional<Reservation> findById(Integer id) throws LibraryException {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding reservation: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAll() throws LibraryException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving reservations: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Reservation reservation) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            updateWithConnection(reservation, conn);
        } catch (SQLException e) {
            throw new LibraryException("Error updating reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateWithConnection(Reservation reservation, Connection conn) throws SQLException {
        String sql = "UPDATE reservations SET student_id=?, book_isbn=?, request_date=?, status=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getStudentId());
            pstmt.setString(2, reservation.getBookIsbn());
            pstmt.setTimestamp(3, reservation.getRequestDate() != null ? Timestamp.valueOf(reservation.getRequestDate()) : null);
            pstmt.setString(4, reservation.getStatus());
            pstmt.setInt(5, reservation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws LibraryException {
        String sql = "DELETE FROM reservations WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Reservation> findOldestPendingByIsbn(String isbn, Connection conn) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE book_isbn = ? AND status = 'PENDING' ORDER BY request_date ASC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReservation(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findPendingByStudentId(String studentId) throws LibraryException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE student_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding pending reservations for student: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Reservation> findPendingByIsbn(String isbn) throws LibraryException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE book_isbn = ? AND status = 'PENDING' ORDER BY request_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding pending reservations by ISBN: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Reservation> findByStudentId(String studentId) throws LibraryException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE student_id = ? ORDER BY request_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding reservations for student: " + e.getMessage(), e);
        }
        return list;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation res = new Reservation();
        res.setId(rs.getInt("id"));
        res.setStudentId(rs.getString("student_id"));
        res.setBookIsbn(rs.getString("book_isbn"));
        Timestamp ts = rs.getTimestamp("request_date");
        if (ts != null) {
            res.setRequestDate(ts.toLocalDateTime());
        }
        res.setStatus(rs.getString("status"));
        return res;
    }
}
```

# BorrowService.java
**Path:** `src/main/java/com/college/library/service/BorrowService.java`

```java
package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.dao.BorrowRequestDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.BorrowRequest;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Reservation;
import com.college.library.model.Student;
import com.college.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class BorrowService {
    private final BorrowTransactionDAO transactionDAO;
    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final ReservationDAO reservationDAO;
    private final BorrowRequestDAO borrowRequestDAO;
    
    private static final double FINE_PER_DAY = 10.0;
    private static final int MAX_BORROW_DAYS = 14;

    public BorrowService(BorrowTransactionDAO transactionDAO, BookDAO bookDAO, StudentDAO studentDAO, ReservationDAO reservationDAO, BorrowRequestDAO borrowRequestDAO) {
        this.transactionDAO = transactionDAO;
        this.bookDAO = bookDAO;
        this.studentDAO = studentDAO;
        this.reservationDAO = reservationDAO;
        this.borrowRequestDAO = borrowRequestDAO;
    }

    public void issueBook(String studentId, String bookId, int librarianId) throws LibraryException {
        Student student = studentDAO.findById(studentId)
                .orElseThrow(() -> new LibraryException("Student not found."));
        if (!student.isActive()) {
            throw new LibraryException("Cannot issue book: Student account is inactive.");
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Optional<Book> bookOpt = bookDAO.findById(bookId);
                if (bookOpt.isEmpty()) {
                    throw new LibraryException("Book not found.");
                }

                int updatedRows = bookDAO.updateStatusIfCondition(bookId, "ISSUED", "AVAILABLE", conn);
                if (updatedRows == 0) {
                    throw new LibraryException("Book is not currently available for issue (it may have been issued concurrently).");
                }
                
                BorrowTransaction bt = new BorrowTransaction();
                bt.setStudentId(studentId);
                bt.setBookId(bookId);
                bt.setLibrarianId(librarianId);
                bt.setIssueDate(LocalDate.now());
                bt.setDueDate(LocalDate.now().plusDays(MAX_BORROW_DAYS));
                bt.setFine(0.0);
                bt.setStatus("ACTIVE");
                
                transactionDAO.createWithConnection(bt, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new LibraryException("Failed to issue book: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new LibraryException("Database error during issue: " + e.getMessage(), e);
        }
    }

    public String returnBook(String bookId, int librarianId) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                BorrowTransaction bt = transactionDAO.findActiveByBookId(bookId)
                        .orElseThrow(() -> new LibraryException("No active borrow transaction found for this book."));
                
                LocalDate now = LocalDate.now();
                double fine = 0.0;
                if (now.isAfter(bt.getDueDate())) {
                    long daysOverdue = ChronoUnit.DAYS.between(bt.getDueDate(), now);
                    fine = daysOverdue * FINE_PER_DAY;
                }
                
                bt.setReturnDate(now);
                bt.setFine(fine);
                bt.setStatus("COMPLETED");
                transactionDAO.updateWithConnection(bt, conn);
                
                // Mark book as available again
                bookDAO.updateStatus(bookId, "AVAILABLE", conn);
                
                Book book = bookDAO.findById(bookId).get();
                Optional<Reservation> resOpt = reservationDAO.findOldestPendingByIsbn(book.getIsbn(), conn);
                
                String msg = "Book successfully returned!";
                if (fine > 0) {
                    msg += "\nLate Fine Collected: Rs. " + fine;
                }
                
                if (resOpt.isPresent()) {
                    Reservation res = resOpt.get();
                    Student reservedStudent = studentDAO.findById(res.getStudentId()).orElse(null);
                    String studentName = (reservedStudent != null) ? reservedStudent.getName() : res.getStudentId();
                    msg += "\n\nATTENTION: This book is reserved!\nNext Student in Queue: " + studentName + " (" + res.getStudentId() + ")\nPlease set this book aside.";
                }
                
                conn.commit();
                return msg;
            } catch (Exception e) {
                conn.rollback();
                throw new LibraryException("Failed to return book: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new LibraryException("Database error during return: " + e.getMessage(), e);
        }
    }

    public String fulfilReservation(int reservationId, int librarianId) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Reservation res = reservationDAO.findById(reservationId)
                        .orElseThrow(() -> new LibraryException("Reservation not found."));
                
                if (!"PENDING".equals(res.getStatus())) {
                    throw new LibraryException("Reservation is not in PENDING state.");
                }
                
                // Find an available book matching the ISBN
                Book availableBook = bookDAO.findByIsbn(res.getBookIsbn()).stream()
                        .filter(b -> "AVAILABLE".equals(b.getStatus()))
                        .findFirst()
                        .orElseThrow(() -> new LibraryException("No copy of this book is currently available for collection."));
                
                // Issue the book atomically
                int updatedRows = bookDAO.updateStatusIfCondition(availableBook.getAccessionId(), "ISSUED", "AVAILABLE", conn);
                if (updatedRows == 0) {
                    throw new LibraryException("Book is not currently available for issue (concurrently modified).");
                }
                
                BorrowTransaction bt = new BorrowTransaction();
                bt.setStudentId(res.getStudentId());
                bt.setBookId(availableBook.getAccessionId());
                bt.setLibrarianId(librarianId);
                bt.setIssueDate(LocalDate.now());
                bt.setDueDate(LocalDate.now().plusDays(MAX_BORROW_DAYS));
                bt.setFine(0.0);
                bt.setStatus("ACTIVE");
                
                transactionDAO.createWithConnection(bt, conn);
                
                // Update reservation to FULFILLED
                res.setStatus("FULFILLED");
                reservationDAO.updateWithConnection(res, conn);
                
                conn.commit();
                return String.format("Reservation fulfilled successfully! Book '%s' issued to Student '%s'.", 
                        availableBook.getTitle(), res.getStudentId());
            } catch (Exception e) {
                conn.rollback();
                throw new LibraryException("Failed to fulfil reservation: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new LibraryException("Database error during reservation fulfilment: " + e.getMessage(), e);
        }
    }

    public List<BorrowTransaction> getStudentHistory(String studentId) throws LibraryException {
        return transactionDAO.findByStudentId(studentId);
    }
    
    // Borrow Request Methods
    public void requestToBorrow(String studentId, String bookId) throws LibraryException {
        Student student = studentDAO.findById(studentId)
                .orElseThrow(() -> new LibraryException("Student not found."));
        if (!student.isActive()) {
            throw new LibraryException("Cannot request book: Student account is inactive.");
        }

        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new LibraryException("Book not found."));
        if (!"AVAILABLE".equals(book.getStatus())) {
            throw new LibraryException("This book is not available. Please use the reservation feature instead.");
        }
        
        Optional<BorrowRequest> pending = borrowRequestDAO.findPendingByStudentAndBook(studentId, bookId);
        if (pending.isPresent()) {
            throw new LibraryException("You already have a pending request for this book.");
        }
        
        BorrowRequest req = new BorrowRequest();
        req.setStudentId(studentId);
        req.setBookId(bookId);
        req.setRequestDate(LocalDateTime.now());
        req.setStatus("PENDING");
        borrowRequestDAO.create(req);
    }
    
    public List<BorrowRequest> getStudentBorrowRequests(String studentId) throws LibraryException {
        return borrowRequestDAO.findByStudentId(studentId);
    }
    
    public List<BorrowRequest> getAllPendingBorrowRequests() throws LibraryException {
        return borrowRequestDAO.findByStatus("PENDING");
    }
    
    public String approveBorrowRequest(int requestId, int librarianId) throws LibraryException {
        BorrowRequest req = borrowRequestDAO.findById(requestId)
            .orElseThrow(() -> new LibraryException("Request not found."));
            
        if (!"PENDING".equals(req.getStatus())) {
            throw new LibraryException("Request is not pending.");
        }
        
        issueBook(req.getStudentId(), req.getBookId(), librarianId);
        
        req.setStatus("APPROVED");
        borrowRequestDAO.update(req);
        return "Request approved and book issued.";
    }
    
    public void rejectBorrowRequest(int requestId) throws LibraryException {
        BorrowRequest req = borrowRequestDAO.findById(requestId)
            .orElseThrow(() -> new LibraryException("Request not found."));
            
        if (!"PENDING".equals(req.getStatus())) {
            throw new LibraryException("Request is not pending.");
        }
        
        req.setStatus("REJECTED");
        borrowRequestDAO.update(req);
    }
}
```

# ReservationService.java
**Path:** `src/main/java/com/college/library/service/ReservationService.java`

```java
package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.Reservation;
import com.college.library.model.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final StudentDAO studentDAO;
    private final BookDAO bookDAO;

    public ReservationService(ReservationDAO reservationDAO, StudentDAO studentDAO, BookDAO bookDAO) {
        this.reservationDAO = reservationDAO;
        this.studentDAO = studentDAO;
        this.bookDAO = bookDAO;
    }

    public String addReservation(String studentId, String bookIsbn) throws LibraryException {
        Student student = studentDAO.findById(studentId)
            .orElseThrow(() -> new LibraryException("Student not found."));
        if (!student.isActive()) {
            throw new LibraryException("Cannot place reservation: Student account is inactive.");
        }
            
        List<Book> books = bookDAO.findByIsbn(bookIsbn);
        if (books.isEmpty()) {
            throw new LibraryException("No books found with ISBN: " + bookIsbn);
        }
        
        boolean hasAvailable = books.stream().anyMatch(b -> "AVAILABLE".equals(b.getStatus()));
        if (hasAvailable) {
            throw new LibraryException("This book is available. Please contact the librarian to borrow it directly.");
        }
        
        List<Reservation> pending = reservationDAO.findPendingByIsbn(bookIsbn);
        boolean alreadyReserved = pending.stream().anyMatch(r -> r.getStudentId().equals(studentId));
        if (alreadyReserved) {
            throw new LibraryException("Student already has an active reservation for this book.");
        }
        
        Reservation res = new Reservation();
        res.setStudentId(studentId);
        res.setBookIsbn(bookIsbn);
        res.setRequestDate(LocalDateTime.now());
        res.setStatus("PENDING");
        
        reservationDAO.create(res);
        
        int queuePosition = pending.size() + 1;
        return String.format("Reservation successful!\nStudent: %s\nBook: %s\nQueue Position: %d\nStatus: PENDING",
                student.getName(), books.get(0).getTitle(), queuePosition);
    }
    
    public void cancelReservation(int reservationId) throws LibraryException {
        Reservation res = reservationDAO.findById(reservationId)
            .orElseThrow(() -> new LibraryException("Reservation not found."));
        if (!"PENDING".equals(res.getStatus())) {
            throw new LibraryException("Only pending reservations can be cancelled.");
        }
        res.setStatus("CANCELLED");
        reservationDAO.update(res);
    }
    
    public List<Object[]> getAllReservationsWithDetails() throws LibraryException {
        List<Object[]> result = new ArrayList<>();
        List<Reservation> allReservations = reservationDAO.findAll();
        
        for (Reservation r : allReservations) {
            String studentName = studentDAO.findById(r.getStudentId())
                    .map(Student::getName)
                    .orElse("Unknown");
            
            List<Book> books = bookDAO.findByIsbn(r.getBookIsbn());
            String bookTitle = !books.isEmpty() ? books.get(0).getTitle() : "Unknown";
            
            String queuePosition = "-";
            if ("PENDING".equals(r.getStatus())) {
                List<Reservation> pending = reservationDAO.findPendingByIsbn(r.getBookIsbn());
                for (int i = 0; i < pending.size(); i++) {
                    if (pending.get(i).getId() == r.getId()) {
                        queuePosition = String.valueOf(i + 1);
                        break;
                    }
                }
            }
            
            String reqDateStr = r.getRequestDate() != null ? r.getRequestDate().toString().replace("T", " ") : "";
            
            result.add(new Object[]{
                r.getId(),
                r.getStudentId(),
                studentName,
                r.getBookIsbn(),
                bookTitle,
                reqDateStr,
                queuePosition,
                r.getStatus()
            });
        }
        
        return result;
    }
    
    public List<Object[]> getStudentReservationsWithDetails(String studentId) throws LibraryException {
        List<Object[]> result = new ArrayList<>();
        for (Object[] row : getAllReservationsWithDetails()) {
            if (studentId.equals(row[1])) {
                result.add(row);
            }
        }
        return result;
    }
}
```

# BorrowPanel.java
**Path:** `src/main/java/com/college/library/gui/BorrowPanel.java`

```java
package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.service.BorrowService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BorrowPanel extends JPanel {
    private MainApplication app;
    private BorrowService borrowService;
    
    private JTextField issueStudentIdField;
    private JTextField issueBookIdField;
    private JTextField returnBookIdField;

    private JTable requestsTable;
    private DefaultTableModel requestsTableModel;
    private JButton approveBtn;
    private JButton rejectBtn;
    private JButton issueBtn;
    private JButton returnBtn;

    public BorrowPanel(MainApplication app, BorrowService borrowService) {
        this.app = app;
        this.borrowService = borrowService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);

        // 1. Top Header Navbar
        JButton backBtn = UITheme.createBackButton("Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));

        JPanel header = UITheme.createHeaderPanel(
                "Circulation Desk",
                "Issue books, process returns, and approve student requests",
                backBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Main Content Container
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        
        // --- Forms Section (Issue & Return Cards) ---
        JPanel formsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        formsPanel.setOpaque(false);
        formsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));
        
        // Manual Issue Card
        UITheme.CardPanel issueCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        JLabel issueTitle = UITheme.createSectionHeader("Direct Book Issue");
        issueCard.add(issueTitle, BorderLayout.NORTH);

        JPanel issueFields = new JPanel(new GridLayout(2, 2, 12, 10));
        issueFields.setOpaque(false);
        issueFields.add(new JLabel("Student KTU ID:"));
        issueStudentIdField = UITheme.createTextField(15);
        issueFields.add(issueStudentIdField);
        issueFields.add(new JLabel("Book Accession ID:"));
        issueBookIdField = UITheme.createTextField(15);
        issueFields.add(issueBookIdField);
        issueCard.add(issueFields, BorderLayout.CENTER);

        JPanel issueActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        issueActionPanel.setOpaque(false);
        issueBtn = UITheme.createPrimaryButton("Issue Book");
        issueBtn.addActionListener(e -> attemptIssue());
        issueActionPanel.add(issueBtn);
        issueCard.add(issueActionPanel, BorderLayout.SOUTH);

        // Manual Return Card
        UITheme.CardPanel returnCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        JLabel returnTitle = UITheme.createSectionHeader("Direct Book Return");
        returnCard.add(returnTitle, BorderLayout.NORTH);

        JPanel returnFields = new JPanel(new GridLayout(2, 2, 12, 10));
        returnFields.setOpaque(false);
        returnFields.add(new JLabel("Book Accession ID:"));
        returnBookIdField = UITheme.createTextField(15);
        returnFields.add(returnBookIdField);
        returnFields.add(new JLabel("")); // spacer
        returnFields.add(new JLabel("")); // spacer
        returnCard.add(returnFields, BorderLayout.CENTER);

        JPanel returnActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        returnActionPanel.setOpaque(false);
        returnBtn = UITheme.createSuccessButton("Return Book");
        returnBtn.addActionListener(e -> attemptReturn());
        returnActionPanel.add(returnBtn);
        returnCard.add(returnActionPanel, BorderLayout.SOUTH);

        formsPanel.add(issueCard);
        formsPanel.add(returnCard);
        mainContentPanel.add(formsPanel);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Bottom section: Pending Requests Card ---
        UITheme.CardPanel requestsCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        
        JPanel reqHeaderPanel = new JPanel(new BorderLayout());
        reqHeaderPanel.setOpaque(false);
        JLabel reqTitle = UITheme.createSectionHeader("Pending Student Borrow Requests");
        reqHeaderPanel.add(reqTitle, BorderLayout.WEST);

        JPanel reqActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        reqActionsPanel.setOpaque(false);
        approveBtn = UITheme.createSuccessButton("Approve Request");
        approveBtn.addActionListener(e -> approveRequest());
        rejectBtn = UITheme.createDangerButton("Reject Request");
        rejectBtn.addActionListener(e -> rejectRequest());
        reqActionsPanel.add(approveBtn);
        reqActionsPanel.add(rejectBtn);
        reqHeaderPanel.add(reqActionsPanel, BorderLayout.EAST);

        requestsCard.add(reqHeaderPanel, BorderLayout.NORTH);

        String[] reqCols = {"Request ID", "Student ID", "Book ID", "Request Date"};
        requestsTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestsTable = new JTable(requestsTableModel);
        UITheme.styleTable(requestsTable);

        requestsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        requestsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        requestsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        requestsTable.getColumnModel().getColumn(3).setPreferredWidth(220);

        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);

        requestsCard.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(requestsCard);

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(requestsTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{requestsTable}, approveBtn, rejectBtn, issueBtn, returnBtn);
    }
    
    public void refreshData() {
        issueStudentIdField.setText("");
        issueBookIdField.setText("");
        returnBookIdField.setText("");
        
        loadRequests();
    }
    
    private void loadRequests() {
        requestsTableModel.setRowCount(0);
        try {
            List<BorrowRequest> pending = borrowService.getAllPendingBorrowRequests();
            for (BorrowRequest req : pending) {
                requestsTableModel.addRow(new Object[]{
                    req.getId(),
                    req.getStudentId(),
                    req.getBookId(),
                    req.getRequestDate().toString().replace("T", " ")
                });
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load requests: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attemptIssue() {
        try {
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            borrowService.issueBook(issueStudentIdField.getText().trim(), issueBookIdField.getText().trim(), librarianId);
            JOptionPane.showMessageDialog(this, "Book successfully issued!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attemptReturn() {
        try {
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            String msg = borrowService.returnBook(returnBookIdField.getText().trim(), librarianId);
            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void approveRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to approve.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int requestId = (Integer) requestsTableModel.getValueAt(selectedRow, 0);
        try {
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            String msg = borrowService.approveBorrowRequest(requestId, librarianId);
            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            requestsTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rejectRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to reject.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int requestId = (Integer) requestsTableModel.getValueAt(selectedRow, 0);
        try {
            borrowService.rejectBorrowRequest(requestId);
            JOptionPane.showMessageDialog(this, "Request rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            requestsTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
```

# ReservationPanel.java
**Path:** `src/main/java/com/college/library/gui/ReservationPanel.java`

```java
package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.service.ReservationService;
import com.college.library.service.BorrowService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReservationPanel extends JPanel {
    private MainApplication app;
    private ReservationService reservationService;
    private BorrowService borrowService;
    
    private JTextField studentIdField;
    private JTextField isbnField;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    private JButton fulfilBtn;
    private JButton reserveBtn;

    public ReservationPanel(MainApplication app, ReservationService reservationService, BorrowService borrowService) {
        this.app = app;
        this.reservationService = reservationService;
        this.borrowService = borrowService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);

        // 1. Top Header Navbar
        JButton backBtn = UITheme.createBackButton("Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));

        JPanel header = UITheme.createHeaderPanel(
                "Reservation Desk",
                "Place student book reservations and fulfil queued waitlists",
                backBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Main Content Container
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        
        // --- Form Card: Place Reservation ---
        UITheme.CardPanel formCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        JLabel formTitle = UITheme.createSectionHeader("Place New Reservation");
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel fieldsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        fieldsRow.setOpaque(false);

        fieldsRow.add(new JLabel("Student KTU ID:"));
        studentIdField = UITheme.createTextField(12);
        fieldsRow.add(studentIdField);

        fieldsRow.add(new JLabel("Book ISBN:"));
        isbnField = UITheme.createTextField(12);
        fieldsRow.add(isbnField);

        reserveBtn = UITheme.createPrimaryButton("Place Reservation");
        reserveBtn.addActionListener(e -> attemptReserve());
        fieldsRow.add(reserveBtn);

        formCard.add(fieldsRow, BorderLayout.CENTER);
        mainContentPanel.add(formCard);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Table Card: Existing Reservations ---
        UITheme.CardPanel tableCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        JLabel tableTitle = UITheme.createSectionHeader("Active Reservations Queue");
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);

        fulfilBtn = UITheme.createSuccessButton("Fulfil Selected Reservation");
        fulfilBtn.addActionListener(e -> attemptFulfil());
        tableHeaderPanel.add(fulfilBtn, BorderLayout.EAST);
        tableCard.add(tableHeaderPanel, BorderLayout.NORTH);

        String[] columns = {"Reservation ID", "Student KTU ID", "Student Name", "Book ISBN", "Book Title", "Request Date", "Queue Position", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(tableModel);
        UITheme.styleTable(reservationsTable);

        reservationsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        reservationsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        reservationsTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        reservationsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        reservationsTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        reservationsTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        reservationsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        reservationsTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(tableCard);

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(reservationsTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{reservationsTable}, fulfilBtn, reserveBtn);
    }
    
    public void refreshData() {
        studentIdField.setText("");
        isbnField.setText("");
        tableModel.setRowCount(0);
        try {
            List<Object[]> rows = reservationService.getAllReservationsWithDetails();
            for (Object[] row : rows) {
                tableModel.addRow(row);
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load reservations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attemptReserve() {
        try {
            String message = reservationService.addReservation(studentIdField.getText().trim(), isbnField.getText().trim());
            JOptionPane.showMessageDialog(this, message, "Reservation Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptFulfil() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to fulfil.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            
            String msg = borrowService.fulfilReservation(reservationId, librarianId);
            JOptionPane.showMessageDialog(this, msg, "Fulfilment Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            reservationsTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
```

# StudentDashboardPanel.java
**Path:** `src/main/java/com/college/library/gui/StudentDashboardPanel.java`

```java
package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Student;
import com.college.library.service.BorrowService;
import com.college.library.service.ReservationService;
import com.college.library.service.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboardPanel extends JPanel {
    private MainApplication app;
    private BorrowService borrowService;
    private ReservationService reservationService;
    private BookService bookService;

    private JTable searchTable;
    private DefaultTableModel searchTableModel;
    private JTextField searchField;
    private JButton requestBtn;
    private JLabel studentGreetingLbl;

    private JTable requestsTable;
    private DefaultTableModel requestsTableModel;

    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    
    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;

    public StudentDashboardPanel(MainApplication app, BorrowService borrowService, ReservationService reservationService, BookService bookService) {
        this.app = app;
        this.borrowService = borrowService;
        this.reservationService = reservationService;
        this.bookService = bookService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);

        // 1. Top Navbar Header
        JButton logoutBtn = UITheme.createSecondaryButton("Sign Out");
        logoutBtn.addActionListener(e -> {
            app.setLoggedInStudent(null);
            app.navigateTo("LOGIN");
        });

        studentGreetingLbl = new JLabel("Student Library Portal");
        studentGreetingLbl.setFont(UITheme.FONT_SUBTITLE);
        studentGreetingLbl.setForeground(UITheme.TEXT_SECONDARY);

        JPanel header = UITheme.createHeaderPanel(
                "Student Portal",
                "Search books, track issued books, view requests, and check reservations",
                null,
                studentGreetingLbl,
                logoutBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Center 2x2 Grid for Content Cards
        JPanel centerGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        centerGrid.setOpaque(false);
        centerGrid.setBorder(new EmptyBorder(16, 20, 16, 20));

        // --- Card 1: Search & Request to Borrow ---
        UITheme.CardPanel searchCard = new UITheme.CardPanel(new BorderLayout(0, 8));
        searchCard.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel searchTitle = UITheme.createSectionHeader("Book Search & Availability");
        searchTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(searchTitle);
        topPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchControls.setOpaque(false);
        searchControls.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchField = UITheme.createTextField(10);
        searchField.setPreferredSize(new Dimension(130, 30));
        searchField.addActionListener(e -> performSearch());

        JButton searchBtn = UITheme.createSecondaryButton("Search");
        searchBtn.addActionListener(e -> performSearch());

        requestBtn = UITheme.createPrimaryButton("Request Borrow");
        requestBtn.addActionListener(e -> requestToBorrow());

        searchControls.add(searchField);
        searchControls.add(searchBtn);
        searchControls.add(requestBtn);
        topPanel.add(searchControls);
        searchCard.add(topPanel, BorderLayout.NORTH);

        String[] searchCols = {"ISBN", "Title", "Author", "Category", "Available"};
        searchTableModel = new DefaultTableModel(searchCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        searchTable = new JTable(searchTableModel);
        UITheme.styleTable(searchTable);
        searchTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        searchTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        searchTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        searchTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        searchTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        JScrollPane searchScroll = new JScrollPane(searchTable);
        searchScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        searchScroll.getViewport().setBackground(UITheme.BG_CARD);
        searchCard.add(searchScroll, BorderLayout.CENTER);
        centerGrid.add(searchCard);

        // --- Card 2: My Borrow Requests ---
        UITheme.CardPanel reqCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        reqCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel reqTitle = UITheme.createSectionHeader("My Borrow Requests");
        reqCard.add(reqTitle, BorderLayout.NORTH);

        String[] reqCols = {"Title", "Request Date", "Status"};
        requestsTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestsTable = new JTable(requestsTableModel);
        UITheme.styleTable(requestsTable);
        requestsTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        requestsTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        requestsTable.getColumnModel().getColumn(2).setPreferredWidth(90);

        JScrollPane reqScroll = new JScrollPane(requestsTable);
        reqScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        reqScroll.getViewport().setBackground(UITheme.BG_CARD);
        reqCard.add(reqScroll, BorderLayout.CENTER);
        centerGrid.add(reqCard);

        // --- Card 3: My History & Issued Books ---
        UITheme.CardPanel historyCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        historyCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel histTitle = UITheme.createSectionHeader("My Borrowing History & Issued Books");
        historyCard.add(histTitle, BorderLayout.NORTH);

        String[] historyCols = {"Tx ID", "Book ID", "Issue Date", "Due Date", "Return Date", "Fine", "Status"};
        historyTableModel = new DefaultTableModel(historyCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        UITheme.styleTable(historyTable);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(85);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(85);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(85);

        JScrollPane histScroll = new JScrollPane(historyTable);
        histScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        histScroll.getViewport().setBackground(UITheme.BG_CARD);
        historyCard.add(histScroll, BorderLayout.CENTER);
        centerGrid.add(historyCard);

        // --- Card 4: My Reservations ---
        UITheme.CardPanel resCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        resCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel resTitle = UITheme.createSectionHeader("My Book Reservations");
        resCard.add(resTitle, BorderLayout.NORTH);

        String[] resCols = {"Res ID", "ISBN", "Book Title", "Request Date", "Queue", "Status"};
        reservationTableModel = new DefaultTableModel(resCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reservationTable = new JTable(reservationTableModel);
        UITheme.styleTable(reservationTable);
        reservationTable.getColumnModel().getColumn(0).setPreferredWidth(55);
        reservationTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        reservationTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        reservationTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        reservationTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        reservationTable.getColumnModel().getColumn(5).setPreferredWidth(85);

        JScrollPane resScroll = new JScrollPane(reservationTable);
        resScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        resScroll.getViewport().setBackground(UITheme.BG_CARD);
        resCard.add(resScroll, BorderLayout.CENTER);
        centerGrid.add(resCard);

        add(centerGrid, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(searchTable, requestsTable, historyTable, reservationTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{searchTable, requestsTable, historyTable, reservationTable}, requestBtn, searchBtn);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        searchTableModel.setRowCount(0);
        try {
            List<Object[]> results = bookService.searchAvailableBooks(query);
            for (Object[] r : results) {
                searchTableModel.addRow(r);
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to search books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void requestToBorrow() {
        int selectedRow = searchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the search results to request.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String isbn = (String) searchTableModel.getValueAt(selectedRow, 0);
        int availableCopies = (Integer) searchTableModel.getValueAt(selectedRow, 4);
        
        if (availableCopies <= 0) {
            JOptionPane.showMessageDialog(this, "This book is currently unavailable. Please use the reservation feature instead.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            Student student = app.getLoggedInStudent();
            if (student == null) return;
            if (!student.isActive()) {
                JOptionPane.showMessageDialog(this, "Your account is inactive. You cannot initiate borrowing requests.", "Account Inactive", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String accessionId = bookService.getFirstAvailableBookId(isbn);
            if (accessionId == null) {
                JOptionPane.showMessageDialog(this, "Failed to find an available copy. Please refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            borrowService.requestToBorrow(student.getKtuId(), accessionId);
            JOptionPane.showMessageDialog(this, "Borrow request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            searchTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Request Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        Student student = app.getLoggedInStudent();
        if (student == null) return;
        studentGreetingLbl.setText("Logged in as " + student.getName() + " (" + student.getKtuId() + ")");

        searchField.setText("");
        performSearch();

        requestsTableModel.setRowCount(0);
        historyTableModel.setRowCount(0);
        reservationTableModel.setRowCount(0);

        try {
            List<com.college.library.model.BorrowRequest> requests = borrowService.getStudentBorrowRequests(student.getKtuId());
            for (com.college.library.model.BorrowRequest r : requests) {
                String title = bookService.getBook(r.getBookId()).getTitle();
                requestsTableModel.addRow(new Object[]{
                    title,
                    r.getRequestDate().toString().replace("T", " "),
                    r.getStatus()
                });
            }
            
            List<BorrowTransaction> history = borrowService.getStudentHistory(student.getKtuId());
            for (BorrowTransaction t : history) {
                historyTableModel.addRow(new Object[]{
                        t.getId(),
                        t.getBookId(),
                        t.getIssueDate(),
                        t.getDueDate(),
                        t.getReturnDate() != null ? t.getReturnDate() : "-",
                        String.format("₹%.2f", t.getFine()),
                        t.getStatus()
                });
            }

            List<Object[]> reservations = reservationService.getStudentReservationsWithDetails(student.getKtuId());
            for (Object[] r : reservations) {
                reservationTableModel.addRow(new Object[]{
                        r[0], r[3], r[4], r[5], r[6], r[7]
                });
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load student data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
```

# BorrowServiceTest.java
**Path:** `src/test/java/com/college/library/service/BorrowServiceTest.java`

```java
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
                if ("INACTIVE_STU".equals(id)) {
                    Student s = new Student();
                    s.setStatus("INACTIVE");
                    return Optional.of(s);
                }
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

        com.college.library.dao.BorrowRequestDAO mockBorrowReqDAO = new com.college.library.dao.BorrowRequestDAO() {
            @Override public void create(com.college.library.model.BorrowRequest r) {}
            @Override public Optional<com.college.library.model.BorrowRequest> findById(Integer id) { return Optional.empty(); }
            @Override public java.util.List<com.college.library.model.BorrowRequest> findAll() { return null; }
            @Override public void update(com.college.library.model.BorrowRequest r) {}
            @Override public void delete(Integer id) {}
            @Override public java.util.List<com.college.library.model.BorrowRequest> findByStudentId(String id) { return null; }
            @Override public java.util.List<com.college.library.model.BorrowRequest> findByStatus(String status) { return null; }
            @Override public Optional<com.college.library.model.BorrowRequest> findPendingByStudentAndBook(String s, String b) { return Optional.empty(); }
        };

        borrowService = new BorrowService(mockTxDAO, mockBookDAO, mockStudentDAO, mockResDAO, mockBorrowReqDAO);
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
}
```
