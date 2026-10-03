package com.college.library.dao.impl;

import com.college.library.dao.BookDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOImpl implements BookDAO {

    @Override
    public void create(Book book) throws LibraryException {
        String sql = "INSERT INTO books (accession_id, isbn, title, author, publisher, edition, category_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getAccessionId());
            pstmt.setString(2, book.getIsbn());
            pstmt.setString(3, book.getTitle());
            pstmt.setString(4, book.getAuthor());
            pstmt.setString(5, book.getPublisher());
            pstmt.setString(6, book.getEdition());
            pstmt.setInt(7, book.getCategoryId());
            pstmt.setString(8, book.getStatus());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error creating book: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Book> findById(String id) throws LibraryException {
        String sql = "SELECT * FROM books WHERE accession_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding book by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() throws LibraryException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving all books: " + e.getMessage(), e);
        }
        return books;
    }

    @Override
    public void update(Book book) throws LibraryException {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, publisher=?, edition=?, category_id=?, status=? WHERE accession_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getPublisher());
            pstmt.setString(5, book.getEdition());
            pstmt.setInt(6, book.getCategoryId());
            pstmt.setString(7, book.getStatus());
            pstmt.setString(8, book.getAccessionId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error updating book: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) throws LibraryException {
        String sql = "DELETE FROM books WHERE accession_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting book: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void updateStatus(String accessionId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE books SET status=? WHERE accession_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, accessionId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public int updateStatusIfCondition(String accessionId, String newStatus, String expectedOldStatus, Connection conn) throws SQLException {
        String sql = "UPDATE books SET status=? WHERE accession_id=? AND status=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, accessionId);
            pstmt.setString(3, expectedOldStatus);
            return pstmt.executeUpdate();
        }
    }
    
    @Override
    public List<com.college.library.model.Category> getAllCategories() throws LibraryException {
        List<com.college.library.model.Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(new com.college.library.model.Category(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving categories: " + e.getMessage(), e);
        }
        return categories;
    }
    
    @Override
    public List<Book> findByIsbn(String isbn) throws LibraryException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving books by ISBN: " + e.getMessage(), e);
        }
        return books;
    }
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        return new Book(
            rs.getString("accession_id"),
            rs.getString("isbn"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("publisher"),
            rs.getString("edition"),
            rs.getInt("category_id"),
            rs.getString("status")
        );
    }
}
