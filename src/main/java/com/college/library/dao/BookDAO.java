package com.college.library.dao;

import com.college.library.model.Book;

import java.sql.Connection;
import java.sql.SQLException;

public interface BookDAO extends CrudDAO<Book, String> {
    void updateStatus(String accessionId, String status, Connection conn) throws SQLException;
    int updateStatusIfCondition(String accessionId, String newStatus, String expectedOldStatus, Connection conn) throws SQLException;
    java.util.List<com.college.library.model.Category> getAllCategories() throws com.college.library.exception.LibraryException;
    java.util.List<Book> findByIsbn(String isbn) throws com.college.library.exception.LibraryException;
}
