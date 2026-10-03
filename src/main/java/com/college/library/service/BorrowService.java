package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Reservation;
import com.college.library.model.Student;
import com.college.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class BorrowService {
    private final BorrowTransactionDAO transactionDAO;
    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final ReservationDAO reservationDAO;
    private final com.college.library.dao.BorrowRequestDAO borrowRequestDAO;
    
    private static final double FINE_PER_DAY = 10.0;
    private static final int MAX_BORROW_DAYS = 14;

    public BorrowService(BorrowTransactionDAO transactionDAO, BookDAO bookDAO, StudentDAO studentDAO, ReservationDAO reservationDAO, com.college.library.dao.BorrowRequestDAO borrowRequestDAO) {
        this.transactionDAO = transactionDAO;
        this.bookDAO = bookDAO;
        this.studentDAO = studentDAO;
        this.reservationDAO = reservationDAO;
        this.borrowRequestDAO = borrowRequestDAO;
    }

    public void issueBook(String studentId, String bookId, int librarianId) throws LibraryException {
        Student student = studentDAO.findById(studentId)
                .orElseThrow(() -> new LibraryException("Student not found."));
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Optional<Book> bookOpt = bookDAO.findById(bookId);
                if (bookOpt.isEmpty()) {
                    throw new LibraryException("Book not found.");
                }

                int updatedRows = bookDAO.updateStatusIfCondition(bookId, "ISSUED", "AVAILABLE", conn);
                if (updatedRows == 0) {
                    throw new LibraryException("Book is not currently available for issue (it may have been issued concurrently).");
                }
                
                BorrowTransaction bt = new BorrowTransaction();
                bt.setStudentId(studentId);
                bt.setBookId(bookId);
                bt.setLibrarianId(librarianId);
                bt.setIssueDate(LocalDate.now());
                bt.setDueDate(LocalDate.now().plusDays(MAX_BORROW_DAYS));
                bt.setFine(0.0);
                bt.setStatus("ACTIVE");
                
                transactionDAO.createWithConnection(bt, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new LibraryException("Failed to issue book: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new LibraryException("Database error during issue: " + e.getMessage(), e);
        }
    }

    public String returnBook(String bookId, int librarianId) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                BorrowTransaction bt = transactionDAO.findActiveByBookId(bookId)
                        .orElseThrow(() -> new LibraryException("No active borrow transaction found for this book."));
                
                LocalDate now = LocalDate.now();
                double fine = 0.0;
                if (now.isAfter(bt.getDueDate())) {
                    long daysOverdue = ChronoUnit.DAYS.between(bt.getDueDate(), now);
                    fine = daysOverdue * FINE_PER_DAY;
                }
                
                bt.setReturnDate(now);
                bt.setFine(fine);
                bt.setStatus("COMPLETED");
                transactionDAO.updateWithConnection(bt, conn);
                
                Book book = bookDAO.findById(bookId).get();
                Optional<Reservation> resOpt = reservationDAO.findOldestPendingByIsbn(book.getIsbn(), conn);
                
                String msg = "Book successfully returned!";
                if (fine > 0) {
                    msg += "\nLate Fine Collected: Rs. " + fine;
                }
                
                if (resOpt.isPresent()) {
                    Reservation res = resOpt.get();
                    
                    bookDAO.updateStatus(bookId, "AVAILABLE", conn);
                    
                    Student reservedStudent = studentDAO.findById(res.getStudentId()).orElse(null);
                    String studentName = (reservedStudent != null) ? reservedStudent.getName() : res.getStudentId();
                    
                    msg += "\n\nATTENTION: This book is reserved!\nNext Student in Queue: " + studentName + " (" + res.getStudentId() + ")\nPlease set this book aside.";
                } else {
                    bookDAO.updateStatus(bookId, "AVAILABLE", conn);
                }
                
                conn.commit();
                return msg;
            } catch (Exception e) {
                conn.rollback();
                throw new LibraryException("Failed to return book: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new LibraryException("Database error during return: " + e.getMessage(), e);
        }
    }

    public String fulfilReservation(int reservationId, int librarianId) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Reservation res = reservationDAO.findById(reservationId)
                        .orElseThrow(() -> new LibraryException("Reservation not found."));
                
                if (!"PENDING".equals(res.getStatus())) {
                    throw new LibraryException("Reservation is not in PENDING state.");
                }
                
                // Find an available book matching the ISBN
                java.util.List<Book> books = bookDAO.findByIsbn(res.getBookIsbn());
                Book availableBook = null;
                for (Book b : books) {
                    if ("AVAILABLE".equals(b.getStatus())) {
                        availableBook = b;
                        break;
                    }
                }
                
                if (availableBook == null) {
                    throw new LibraryException("No copy of this book is currently available for collection.");
                }
                
                // Issue the book
                int updatedRows = bookDAO.updateStatusIfCondition(availableBook.getAccessionId(), "ISSUED", "AVAILABLE", conn);
                if (updatedRows == 0) {
                    throw new LibraryException("Book is not currently available for issue (concurrently modified).");
                }
                
                BorrowTransaction bt = new BorrowTransaction();
                bt.setStudentId(res.getStudentId());
                bt.setBookId(availableBook.getAccessionId());
                bt.setLibrarianId(librarianId);
                bt.setIssueDate(LocalDate.now());
                bt.setDueDate(LocalDate.now().plusDays(MAX_BORROW_DAYS));
                bt.setFine(0.0);
                bt.setStatus("ACTIVE");
                
                transactionDAO.createWithConnection(bt, conn);
                
                // Update reservation to FULFILLED
                res.setStatus("FULFILLED");
                reservationDAO.updateWithConnection(res, conn);
                
                conn.commit();
                return String.format("Reservation fulfilled successfully! Book '%s' issued to Student '%s'.", 
                        availableBook.getTitle(), res.getStudentId());
            } catch (Exception e) {
                conn.rollback();
                throw new LibraryException("Failed to fulfil reservation: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new LibraryException("Database error during reservation fulfilment: " + e.getMessage(), e);
        }
    }

    public java.util.List<BorrowTransaction> getStudentHistory(String studentId) throws LibraryException {
        return transactionDAO.findByStudentId(studentId);
    }
    
    // Borrow Request Methods
    public void requestToBorrow(String studentId, String bookId) throws LibraryException {
        Optional<Book> bookOpt = bookDAO.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new LibraryException("Book not found.");
        }
        Book book = bookOpt.get();
        if (!"AVAILABLE".equals(book.getStatus())) {
            throw new LibraryException("This book is not available. Please use the reservation feature instead.");
        }
        
        Optional<com.college.library.model.BorrowRequest> pending = borrowRequestDAO.findPendingByStudentAndBook(studentId, bookId);
        if (pending.isPresent()) {
            throw new LibraryException("You already have a pending request for this book.");
        }
        
        com.college.library.model.BorrowRequest req = new com.college.library.model.BorrowRequest();
        req.setStudentId(studentId);
        req.setBookId(bookId);
        req.setRequestDate(java.time.LocalDateTime.now());
        req.setStatus("PENDING");
        borrowRequestDAO.create(req);
    }
    
    public java.util.List<com.college.library.model.BorrowRequest> getStudentBorrowRequests(String studentId) throws LibraryException {
        return borrowRequestDAO.findByStudentId(studentId);
    }
    
    public java.util.List<com.college.library.model.BorrowRequest> getAllPendingBorrowRequests() throws LibraryException {
        return borrowRequestDAO.findByStatus("PENDING");
    }
    
    public String approveBorrowRequest(int requestId, int librarianId) throws LibraryException {
        com.college.library.model.BorrowRequest req = borrowRequestDAO.findById(requestId)
            .orElseThrow(() -> new LibraryException("Request not found."));
            
        if (!"PENDING".equals(req.getStatus())) {
            throw new LibraryException("Request is not pending.");
        }
        
        issueBook(req.getStudentId(), req.getBookId(), librarianId);
        
        req.setStatus("APPROVED"); // Or "ISSUED" depending on preference, we will use "ISSUED"
        borrowRequestDAO.update(req);
        return "Request approved and book issued.";
    }
    
    public void rejectBorrowRequest(int requestId) throws LibraryException {
        com.college.library.model.BorrowRequest req = borrowRequestDAO.findById(requestId)
            .orElseThrow(() -> new LibraryException("Request not found."));
            
        if (!"PENDING".equals(req.getStatus())) {
            throw new LibraryException("Request is not pending.");
        }
        
        req.setStatus("REJECTED");
        borrowRequestDAO.update(req);
    }
}
