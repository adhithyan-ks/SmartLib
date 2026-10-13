package com.college.library.dao.impl;

import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dto.ActiveBorrowingDTO;
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

    @Override
    public List<ActiveBorrowingDTO> findAllActiveBorrowings() throws LibraryException {
        List<ActiveBorrowingDTO> list = new ArrayList<>();
        String sql = "SELECT bt.id, bt.student_id, s.name AS student_name, bt.book_id, b.title AS book_title, " +
                     "bt.issue_date, bt.due_date, bt.status " +
                     "FROM borrow_transactions bt " +
                     "JOIN students s ON bt.student_id = s.ktu_id " +
                     "JOIN books b ON bt.book_id = b.accession_id " +
                     "WHERE bt.status = 'ACTIVE' " +
                     "ORDER BY bt.due_date ASC, bt.id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ActiveBorrowingDTO dto = new ActiveBorrowingDTO();
                dto.setTransactionId(rs.getInt("id"));
                dto.setStudentId(rs.getString("student_id"));
                dto.setStudentName(rs.getString("student_name"));
                dto.setBookAccessionId(rs.getString("book_id"));
                dto.setBookTitle(rs.getString("book_title"));

                Date issueDate = rs.getDate("issue_date");
                if (issueDate != null) {
                    dto.setIssueDate(issueDate.toLocalDate());
                }

                Date dueDate = rs.getDate("due_date");
                if (dueDate != null) {
                    dto.setDueDate(dueDate.toLocalDate());
                }

                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving active borrowings: " + e.getMessage(), e);
        }
        return list;
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
