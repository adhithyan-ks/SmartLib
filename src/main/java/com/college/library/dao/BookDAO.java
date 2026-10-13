package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.Category;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BookDAO extends CrudDAO<Book, String> {
    void updateStatus(String accessionId, String status, Connection conn) throws SQLException;
    int updateStatusIfCondition(String accessionId, String newStatus, String expectedOldStatus, Connection conn) throws SQLException;
    List<Category> getAllCategories() throws LibraryException;
    List<Book> findByIsbn(String isbn) throws LibraryException;
}
