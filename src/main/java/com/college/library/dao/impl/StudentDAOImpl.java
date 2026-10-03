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
        String sql = "INSERT INTO students (ktu_id, password_hash, name, branch, semester, batch, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "UPDATE students SET password_hash=?, name=?, branch=?, semester=?, batch=?, email=?, phone=? WHERE ktu_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getPasswordHash());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getBranch());
            pstmt.setInt(4, student.getSemester());
            pstmt.setString(5, student.getBatch());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhone());
            pstmt.setString(8, student.getKtuId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error updating student: " + e.getMessage(), e);
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
            throw new LibraryException("Error deleting student: " + e.getMessage(), e);
        }
    }
    
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        // Handle password_hash column safely in case it doesn't exist yet or is null
        String passwordHash = null;
        try {
            passwordHash = rs.getString("password_hash");
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
            rs.getString("phone")
        );
    }
}
