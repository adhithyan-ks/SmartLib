package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;

public class AnalyticsService {
    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final BorrowTransactionDAO transactionDAO;

    public AnalyticsService(BookDAO bookDAO, StudentDAO studentDAO, BorrowTransactionDAO transactionDAO) {
        this.bookDAO = bookDAO;
        this.studentDAO = studentDAO;
        this.transactionDAO = transactionDAO;
    }

    public int getTotalBooks() throws LibraryException {
        return bookDAO.findAll().size();
    }

    public int getAvailableBooks() throws LibraryException {
        return (int) bookDAO.findAll().stream()
                .filter(b -> "AVAILABLE".equals(b.getStatus()))
                .count();
    }

    public int getIssuedBooks() throws LibraryException {
        return (int) bookDAO.findAll().stream()
                .filter(b -> "ISSUED".equals(b.getStatus()))
                .count();
    }

    public int getTotalStudents() throws LibraryException {
        return studentDAO.findAll().size();
    }

    public int getActiveTransactions() throws LibraryException {
        return transactionDAO.countActiveTransactions();
    }
    
    public int getTotalTransactions() throws LibraryException {
        return transactionDAO.findAll().size();
    }
}
