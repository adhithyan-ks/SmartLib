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
}
