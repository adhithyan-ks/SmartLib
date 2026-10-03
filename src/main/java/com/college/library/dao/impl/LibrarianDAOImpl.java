package com.college.library.dao.impl;

import com.college.library.dao.LibrarianDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibrarianDAOImpl implements LibrarianDAO {

    @Override
    public void create(Librarian librarian) throws LibraryException {
        String sql = "INSERT INTO librarians (username, password_hash, name) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, librarian.getUsername());
            pstmt.setString(2, librarian.getPasswordHash());
            pstmt.setString(3, librarian.getName());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    librarian.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error creating librarian: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Librarian> findById(Integer id) throws LibraryException {
        String sql = "SELECT * FROM librarians WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLibrarian(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding librarian by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Librarian> findByUsername(String username) throws LibraryException {
        String sql = "SELECT * FROM librarians WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLibrarian(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding librarian by username: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Librarian> findAll() throws LibraryException {
        List<Librarian> librarians = new ArrayList<>();
        String sql = "SELECT * FROM librarians";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                librarians.add(mapResultSetToLibrarian(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving all librarians: " + e.getMessage(), e);
        }
        return librarians;
    }

    @Override
    public void update(Librarian librarian) throws LibraryException {
        String sql = "UPDATE librarians SET username=?, password_hash=?, name=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, librarian.getUsername());
            pstmt.setString(2, librarian.getPasswordHash());
            pstmt.setString(3, librarian.getName());
            pstmt.setInt(4, librarian.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error updating librarian: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws LibraryException {
        String sql = "DELETE FROM librarians WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting librarian: " + e.getMessage(), e);
        }
    }
    
    private Librarian mapResultSetToLibrarian(ResultSet rs) throws SQLException {
        return new Librarian(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("name")
        );
    }
}
