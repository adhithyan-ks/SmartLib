package com.college.library.dao.impl;

import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Student;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public void create(Student student) throws LibraryException {
        String status = student.getStatus() != null ? student.getStatus() : "ACTIVE";
        String sql = "INSERT INTO students (ktu_id, password_hash, name, branch, semester, batch, email, phone, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getKtuId());
            pstmt.setString(2, student.getPasswordHash());
            pstmt.setString(3, student.getName());
            pstmt.setString(4, student.getBranch());
            pstmt.setInt(5, student.getSemester());
            pstmt.setString(6, student.getBatch());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getPhone());
            pstmt.setString(9, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error creating student: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> findById(String ktuId) throws LibraryException {
        String sql = "SELECT * FROM students WHERE ktu_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ktuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding student by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() throws LibraryException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving all students: " + e.getMessage(), e);
        }
        return students;
    }

    @Override
    public void update(Student student) throws LibraryException {
        String status = student.getStatus() != null ? student.getStatus() : "ACTIVE";
        String sqlWithStatus = "UPDATE students SET password_hash=?, name=?, branch=?, semester=?, batch=?, email=?, phone=?, status=? WHERE ktu_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlWithStatus)) {
            
            pstmt.setString(1, student.getPasswordHash());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getBranch());
            pstmt.setInt(4, student.getSemester());
            pstmt.setString(5, student.getBatch());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhone());
            pstmt.setString(8, status);
            pstmt.setString(9, student.getKtuId());
            
            pstmt.executeUpdate();
            return;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("status")) {
                String sqlWithout = "UPDATE students SET password_hash=?, name=?, branch=?, semester=?, batch=?, email=?, phone=? WHERE ktu_id=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sqlWithout)) {
                    pstmt.setString(1, student.getPasswordHash());
                    pstmt.setString(2, student.getName());
                    pstmt.setString(3, student.getBranch());
                    pstmt.setInt(4, student.getSemester());
                    pstmt.setString(5, student.getBatch());
                    pstmt.setString(6, student.getEmail());
                    pstmt.setString(7, student.getPhone());
                    pstmt.setString(8, student.getKtuId());
                    pstmt.executeUpdate();
                    return;
                } catch (SQLException ex) {
                    throw new LibraryException("Error updating student: " + ex.getMessage(), ex);
                }
            }
            throw new LibraryException("Error updating student: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateStatus(String ktuId, String status) throws LibraryException {
        String sql = "UPDATE students SET status=? WHERE ktu_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, ktuId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error updating student status: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String ktuId) throws LibraryException {
        String sql = "DELETE FROM students WHERE ktu_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ktuId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState()) || 
                (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key"))) {
                throw new LibraryException("Cannot delete student: Related records (borrow transactions, borrow requests, or reservations) exist in the system. Deletion is blocked to preserve data integrity.", e);
            }
            throw new LibraryException("Error deleting student: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasRelatedRecords(String ktuId) throws LibraryException {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM borrow_transactions WHERE student_id = ?) + " +
                     "(SELECT COUNT(*) FROM borrow_requests WHERE student_id = ?) + " +
                     "(SELECT COUNT(*) FROM reservations WHERE student_id = ?) AS total_count";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ktuId);
            pstmt.setString(2, ktuId);
            pstmt.setString(3, ktuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking related records for student: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<String> getRelatedRecordsSummary(String ktuId) throws LibraryException {
        List<String> summaries = new ArrayList<>();
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM borrow_transactions WHERE student_id = ?) AS total_tx, " +
                     "(SELECT COUNT(*) FROM borrow_transactions WHERE student_id = ? AND status = 'ACTIVE') AS active_tx, " +
                     "(SELECT COUNT(*) FROM borrow_requests WHERE student_id = ?) AS total_req, " +
                     "(SELECT COUNT(*) FROM borrow_requests WHERE student_id = ? AND status = 'PENDING') AS pending_req, " +
                     "(SELECT COUNT(*) FROM reservations WHERE student_id = ?) AS total_res, " +
                     "(SELECT COUNT(*) FROM reservations WHERE student_id = ? AND status = 'PENDING') AS pending_res";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) {
                pstmt.setString(i, ktuId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalTx = rs.getInt("total_tx");
                    int activeTx = rs.getInt("active_tx");
                    int completedTx = totalTx - activeTx;
                    if (activeTx > 0 && completedTx > 0) {
                        summaries.add(activeTx + " active borrowed book(s) and " + completedTx + " borrowing history record(s)");
                    } else if (activeTx > 0) {
                        summaries.add(activeTx + " active borrowed book(s)");
                    } else if (completedTx > 0) {
                        summaries.add(completedTx + " borrowing history record(s)");
                    }

                    int totalReq = rs.getInt("total_req");
                    int pendingReq = rs.getInt("pending_req");
                    int pastReq = totalReq - pendingReq;
                    if (pendingReq > 0 && pastReq > 0) {
                        summaries.add(pendingReq + " pending borrow request(s) and " + pastReq + " past request(s)");
                    } else if (pendingReq > 0) {
                        summaries.add(pendingReq + " pending borrow request(s)");
                    } else if (totalReq > 0) {
                        summaries.add(totalReq + " borrow request(s)");
                    }

                    int totalRes = rs.getInt("total_res");
                    int pendingRes = rs.getInt("pending_res");
                    int pastRes = totalRes - pendingRes;
                    if (pendingRes > 0 && pastRes > 0) {
                        summaries.add(pendingRes + " pending reservation(s) and " + pastRes + " past reservation(s)");
                    } else if (pendingRes > 0) {
                        summaries.add(pendingRes + " pending reservation(s)");
                    } else if (totalRes > 0) {
                        summaries.add(totalRes + " reservation(s)");
                    }
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking related records summary for student: " + e.getMessage(), e);
        }
        return summaries;
    }
    
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        // Handle password_hash column safely in case it doesn't exist yet or is null
        String passwordHash = null;
        try {
            passwordHash = rs.getString("password_hash");
        } catch (SQLException ignored) {
            // Column might not exist if migration hasn't run yet
        }
        
        String status = "ACTIVE";
        try {
            String dbStatus = rs.getString("status");
            if (dbStatus != null && !dbStatus.trim().isEmpty()) {
                status = dbStatus;
            }
        } catch (SQLException ignored) {
            // Column might not exist if migration hasn't run yet
        }
        
        return new Student(
            rs.getString("ktu_id"),
            passwordHash,
            rs.getString("name"),
            rs.getString("branch"),
            rs.getInt("semester"),
            rs.getString("batch"),
            rs.getString("email"),
            rs.getString("phone"),
            status
        );
    }
}
