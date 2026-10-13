package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.Category;

import java.util.*;

public class BookService {
    private static final Set<String> VALID_STATUSES = Set.of("AVAILABLE", "ISSUED", "LOST", "DAMAGED");
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

    public List<Category> getAllCategories() throws LibraryException {
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
        if (book.getStatus() == null || !VALID_STATUSES.contains(book.getStatus())) {
            throw new LibraryException("Invalid book status.");
        }
    }

    public List<Object[]> searchAvailableBooks(String query) throws LibraryException {
        List<Book> allBooks = bookDAO.findAll();
        List<Category> categories = bookDAO.getAllCategories();
        
        Map<Integer, String> catMap = new HashMap<>();
        for (Category c : categories) {
            catMap.put(c.getId(), c.getName());
        }
        
        Map<String, Object[]> grouped = new LinkedHashMap<>();
        String lowerQuery = query == null ? "" : query.toLowerCase().trim();
        
        for (Book b : allBooks) {
            boolean matches = lowerQuery.isEmpty() || 
                              b.getTitle().toLowerCase().contains(lowerQuery) ||
                              b.getAuthor().toLowerCase().contains(lowerQuery) ||
                              b.getIsbn().toLowerCase().contains(lowerQuery);
            if (!matches) continue;
            
            Object[] row = grouped.computeIfAbsent(b.getIsbn(), isbn -> new Object[]{
                isbn,
                b.getTitle(),
                b.getAuthor(),
                catMap.getOrDefault(b.getCategoryId(), "Unknown"),
                0
            });
            if ("AVAILABLE".equals(b.getStatus())) {
                row[4] = (Integer) row[4] + 1;
            }
        }
        return new ArrayList<>(grouped.values());
    }
    
    public String getFirstAvailableBookId(String isbn) throws LibraryException {
        return bookDAO.findByIsbn(isbn).stream()
                .filter(b -> "AVAILABLE".equals(b.getStatus()))
                .map(Book::getAccessionId)
                .findFirst()
                .orElse(null);
    }
}
