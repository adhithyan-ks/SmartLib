package com.college.library.model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private String studentId;
    private String bookIsbn;
    private LocalDateTime requestDate;
    private String status; // 'PENDING', 'FULFILLED', 'CANCELLED'

    public Reservation() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
