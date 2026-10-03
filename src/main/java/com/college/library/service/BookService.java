package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;

import java.util.List;

public class BookService {
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void addBook(Book book) throws LibraryException {
        validateBook(book);
        if (bookDAO.findById(book.getAccessionId()).isPresent()) {
            throw new LibraryException("Book with accession ID " + book.getAccessionId() + " already exists.");
        }
        bookDAO.create(book);
    }

    public void updateBook(Book book) throws LibraryException {
        validateBook(book);
        if (bookDAO.findById(book.getAccessionId()).isEmpty()) {
            throw new LibraryException("Book not found for update.");
        }
        bookDAO.update(book);
    }

    public void deleteBook(String accessionId) throws LibraryException {
        if (accessionId == null || accessionId.trim().isEmpty()) {
            throw new LibraryException("Accession ID cannot be empty.");
        }
        bookDAO.delete(accessionId);
    }

    public Book getBook(String accessionId) throws LibraryException {
        return bookDAO.findById(accessionId)
                .orElseThrow(() -> new LibraryException("Book not found."));
    }

    public List<Book> getAllBooks() throws LibraryException {
        return bookDAO.findAll();
    }

    public List<com.college.library.model.Category> getAllCategories() throws LibraryException {
        return bookDAO.getAllCategories();
    }

    private void validateBook(Book book) throws LibraryException {
        if (book == null) throw new LibraryException("Book cannot be null.");
        if (book.getAccessionId() == null || book.getAccessionId().trim().isEmpty()) {
            throw new LibraryException("Book accession ID is required.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new LibraryException("Book title is required.");
        }
        if (book.getStatus() == null || (!book.getStatus().equals("AVAILABLE") && !book.getStatus().equals("ISSUED") 
            && !book.getStatus().equals("LOST") && !book.getStatus().equals("DAMAGED"))) {
            throw new LibraryException("Invalid book status.");
        }
    }

    public List<Object[]> searchAvailableBooks(String query) throws LibraryException {
        List<Book> allBooks = bookDAO.findAll();
        List<com.college.library.model.Category> categories = bookDAO.getAllCategories();
        
        java.util.Map<Integer, String> catMap = new java.util.HashMap<>();
        for (com.college.library.model.Category c : categories) {
            catMap.put(c.getId(), c.getName());
        }
        
        java.util.Map<String, Object[]> grouped = new java.util.LinkedHashMap<>();
        String lowerQuery = query == null ? "" : query.toLowerCase().trim();
        
        for (Book b : allBooks) {
            boolean matches = lowerQuery.isEmpty() || 
                              b.getTitle().toLowerCase().contains(lowerQuery) ||
                              b.getAuthor().toLowerCase().contains(lowerQuery) ||
                              b.getIsbn().toLowerCase().contains(lowerQuery);
            if (!matches) continue;
            
            Object[] row = grouped.get(b.getIsbn());
            if (row == null) {
                String catName = catMap.getOrDefault(b.getCategoryId(), "Unknown");
                row = new Object[] { b.getIsbn(), b.getTitle(), b.getAuthor(), catName, 0 };
                grouped.put(b.getIsbn(), row);
            }
            if ("AVAILABLE".equals(b.getStatus())) {
                row[4] = (Integer) row[4] + 1;
            }
        }
        return new java.util.ArrayList<>(grouped.values());
    }
    
    public String getFirstAvailableBookId(String isbn) throws LibraryException {
        List<Book> books = bookDAO.findByIsbn(isbn);
        for (Book b : books) {
            if ("AVAILABLE".equals(b.getStatus())) {
                return b.getAccessionId();
            }
        }
        return null;
    }
}
