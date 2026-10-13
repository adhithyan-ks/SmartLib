package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.Reservation;
import com.college.library.model.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final StudentDAO studentDAO;
    private final BookDAO bookDAO;

    public ReservationService(ReservationDAO reservationDAO, StudentDAO studentDAO, BookDAO bookDAO) {
        this.reservationDAO = reservationDAO;
        this.studentDAO = studentDAO;
        this.bookDAO = bookDAO;
    }

    public String addReservation(String studentId, String bookIsbn) throws LibraryException {
        Student student = studentDAO.findById(studentId)
            .orElseThrow(() -> new LibraryException("Student not found."));
        if (!student.isActive()) {
            throw new LibraryException("Cannot place reservation: Student account is inactive.");
        }
            
        List<Book> books = bookDAO.findByIsbn(bookIsbn);
        if (books.isEmpty()) {
            throw new LibraryException("No books found with ISBN: " + bookIsbn);
        }
        
        boolean hasAvailable = books.stream().anyMatch(b -> "AVAILABLE".equals(b.getStatus()));
        if (hasAvailable) {
            throw new LibraryException("This book is available. Please contact the librarian to borrow it directly.");
        }
        
        List<Reservation> pending = reservationDAO.findPendingByIsbn(bookIsbn);
        boolean alreadyReserved = pending.stream().anyMatch(r -> r.getStudentId().equals(studentId));
        if (alreadyReserved) {
            throw new LibraryException("Student already has an active reservation for this book.");
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
    
    public List<Object[]> getAllReservationsWithDetails() throws LibraryException {
        List<Object[]> result = new ArrayList<>();
        List<Reservation> allReservations = reservationDAO.findAll();
        
        for (Reservation r : allReservations) {
            String studentName = studentDAO.findById(r.getStudentId())
                    .map(Student::getName)
                    .orElse("Unknown");
            
            List<Book> books = bookDAO.findByIsbn(r.getBookIsbn());
            String bookTitle = !books.isEmpty() ? books.get(0).getTitle() : "Unknown";
            
            String queuePosition = "-";
            if ("PENDING".equals(r.getStatus())) {
                List<Reservation> pending = reservationDAO.findPendingByIsbn(r.getBookIsbn());
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
    
    public List<Object[]> getStudentReservationsWithDetails(String studentId) throws LibraryException {
        List<Object[]> result = new ArrayList<>();
        for (Object[] row : getAllReservationsWithDetails()) {
            if (studentId.equals(row[1])) {
                result.add(row);
            }
        }
        return result;
    }
}
