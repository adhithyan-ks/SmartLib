package com.college.library.dao.impl;

import com.college.library.dao.BorrowRequestDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
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
