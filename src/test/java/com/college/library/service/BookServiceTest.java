package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    
    private BookService bookService;
    private boolean createCalled = false;

    @BeforeEach
    void setUp() {
        createCalled = false;
        BookDAO mockDAO = new BookDAO() {
            @Override public void create(Book entity) { createCalled = true; }
            @Override public Optional<Book> findById(String id) {
                if ("EXISTS".equals(id)) return Optional.of(new Book());
                return Optional.empty();
            }
            @Override public java.util.List<Book> findAll() { return null; }
            @Override public void update(Book entity) {}
            @Override public void delete(String id) {}
            @Override public void updateStatus(String id, String s, Connection c) {}
            @Override public int updateStatusIfCondition(String i, String n, String o, Connection c) { return 1; }
            @Override public java.util.List<com.college.library.model.Category> getAllCategories() { return new java.util.ArrayList<>(); }
            @Override public java.util.List<Book> findByIsbn(String isbn) { return new java.util.ArrayList<>(); }
        };
        bookService = new BookService(mockDAO);
    }

    @Test
    void testAddBookSuccess() throws Exception {
        Book b = new Book("NEW", "123", "Title", "Author", "Pub", "1", 1, "AVAILABLE");
        bookService.addBook(b);
        assertTrue(createCalled);
    }

    @Test
    void testAddBookDuplicate() {
        Book b = new Book("EXISTS", "123", "Title", "Author", "Pub", "1", 1, "AVAILABLE");
        assertThrows(LibraryException.class, () -> bookService.addBook(b));
    }
    
    @Test
    void testValidateBookMissingTitle() {
        Book b = new Book("NEW2", "123", "", "Author", "Pub", "1", 1, "AVAILABLE");
        assertThrows(LibraryException.class, () -> bookService.addBook(b));
    }
}
