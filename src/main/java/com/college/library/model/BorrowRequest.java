package com.college.library.model;

import java.time.LocalDateTime;

public class BorrowRequest {
    private int id;
    private String studentId;
    private String bookId;
    private LocalDateTime requestDate;
    private String status;

    public BorrowRequest() {}

    public BorrowRequest(int id, String studentId, String bookId, LocalDateTime requestDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.bookId = bookId;
        this.requestDate = requestDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
