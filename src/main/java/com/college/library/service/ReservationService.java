package com.college.library.service;

import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Reservation;

import java.time.LocalDateTime;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final StudentDAO studentDAO;
    private final com.college.library.dao.BookDAO bookDAO;

    public ReservationService(ReservationDAO reservationDAO, StudentDAO studentDAO, com.college.library.dao.BookDAO bookDAO) {
        this.reservationDAO = reservationDAO;
        this.studentDAO = studentDAO;
        this.bookDAO = bookDAO;
    }

    public String addReservation(String studentId, String bookIsbn) throws LibraryException {
        com.college.library.model.Student student = studentDAO.findById(studentId)
            .orElseThrow(() -> new LibraryException("Student not found."));
            
        java.util.List<com.college.library.model.Book> books = bookDAO.findByIsbn(bookIsbn);
        if (books.isEmpty()) {
            throw new LibraryException("No books found with ISBN: " + bookIsbn);
        }
        
        for (com.college.library.model.Book b : books) {
            if ("AVAILABLE".equals(b.getStatus())) {
                throw new LibraryException("This book is available. Please contact the librarian to borrow it directly.");
            }
        }
        
        java.util.List<Reservation> pending = reservationDAO.findPendingByIsbn(bookIsbn);
        for (Reservation r : pending) {
            if (r.getStudentId().equals(studentId)) {
                throw new LibraryException("Student already has an active reservation for this book.");
            }
        }
        
        Reservation res = new Reservation();
        res.setStudentId(studentId);
        res.setBookIsbn(bookIsbn);
        res.setRequestDate(LocalDateTime.now());
        res.setStatus("PENDING");
        
        reservationDAO.create(res);
        
        int queuePosition = pending.size() + 1;
        return String.format("Reservation successful!\nStudent: %s\nBook: %s\nQueue Position: %d\nStatus: PENDING",
                student.getName(), books.get(0).getTitle(), queuePosition);
    }
    
    public void cancelReservation(int reservationId) throws LibraryException {
        Reservation res = reservationDAO.findById(reservationId)
            .orElseThrow(() -> new LibraryException("Reservation not found."));
        if (!"PENDING".equals(res.getStatus())) {
            throw new LibraryException("Only pending reservations can be cancelled.");
        }
        res.setStatus("CANCELLED");
        reservationDAO.update(res);
    }
    
    public java.util.List<Object[]> getAllReservationsWithDetails() throws LibraryException {
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        java.util.List<Reservation> allReservations = reservationDAO.findAll();
        
        for (Reservation r : allReservations) {
            String studentName = "Unknown";
            com.college.library.model.Student s = studentDAO.findById(r.getStudentId()).orElse(null);
            if (s != null) {
                studentName = s.getName();
            }
            
            String bookTitle = "Unknown";
            java.util.List<com.college.library.model.Book> books = bookDAO.findByIsbn(r.getBookIsbn());
            if (!books.isEmpty()) {
                bookTitle = books.get(0).getTitle();
            }
            
            String queuePosition = "-";
            if ("PENDING".equals(r.getStatus())) {
                java.util.List<Reservation> pending = reservationDAO.findPendingByIsbn(r.getBookIsbn());
                for (int i = 0; i < pending.size(); i++) {
                    if (pending.get(i).getId() == r.getId()) {
                        queuePosition = String.valueOf(i + 1);
                        break;
                    }
                }
            }
            
            String reqDateStr = r.getRequestDate() != null ? r.getRequestDate().toString().replace("T", " ") : "";
            
            result.add(new Object[]{
                r.getId(),
                r.getStudentId(),
                studentName,
                r.getBookIsbn(),
                bookTitle,
                reqDateStr,
                queuePosition,
                r.getStatus()
            });
        }
        
        return result;
    }
    
    public java.util.List<Object[]> getStudentReservationsWithDetails(String studentId) throws LibraryException {
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        java.util.List<Object[]> all = getAllReservationsWithDetails();
        for (Object[] row : all) {
            if (studentId.equals(row[1])) {
                result.add(row);
            }
        }
        return result;
    }
}
