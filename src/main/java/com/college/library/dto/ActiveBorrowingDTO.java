package com.college.library.dto;

import java.time.LocalDate;
import java.util.Objects;

public class ActiveBorrowingDTO {
    private int transactionId;
    private String studentId;
    private String studentName;
    private String bookAccessionId;
    private String bookTitle;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private long daysOverdue;
    private double currentFine;
    private String status;

    public ActiveBorrowingDTO() {}

    public ActiveBorrowingDTO(int transactionId, String studentId, String studentName,
                              String bookAccessionId, String bookTitle,
                              LocalDate issueDate, LocalDate dueDate, String status) {
        this(transactionId, studentId, studentName, bookAccessionId, bookTitle, issueDate, dueDate, 0, 0.0, status);
    }

    public ActiveBorrowingDTO(int transactionId, String studentId, String studentName,
                              String bookAccessionId, String bookTitle,
                              LocalDate issueDate, LocalDate dueDate,
                              long daysOverdue, double currentFine, String status) {
        this.transactionId = transactionId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.bookAccessionId = bookAccessionId;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.daysOverdue = daysOverdue;
        this.currentFine = currentFine;
        this.status = status;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBookAccessionId() {
        return bookAccessionId;
    }

    public void setBookAccessionId(String bookAccessionId) {
        this.bookAccessionId = bookAccessionId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public long getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(long daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public double getCurrentFine() {
        return currentFine;
    }

    public void setCurrentFine(double currentFine) {
        this.currentFine = currentFine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveBorrowingDTO that = (ActiveBorrowingDTO) o;
        return transactionId == that.transactionId &&
                daysOverdue == that.daysOverdue &&
                Double.compare(that.currentFine, currentFine) == 0 &&
                Objects.equals(studentId, that.studentId) &&
                Objects.equals(studentName, that.studentName) &&
                Objects.equals(bookAccessionId, that.bookAccessionId) &&
                Objects.equals(bookTitle, that.bookTitle) &&
                Objects.equals(issueDate, that.issueDate) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, studentId, studentName, bookAccessionId, bookTitle, issueDate, dueDate, daysOverdue, currentFine, status);
    }

    @Override
    public String toString() {
        return "ActiveBorrowingDTO{" +
                "transactionId=" + transactionId +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", bookAccessionId='" + bookAccessionId + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", daysOverdue=" + daysOverdue +
                ", currentFine=" + currentFine +
                ", status='" + status + '\'' +
                '}';
    }
}
