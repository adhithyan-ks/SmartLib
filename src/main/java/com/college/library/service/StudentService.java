package com.college.library.service;

import com.college.library.dao.BorrowRequestDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Reservation;
import com.college.library.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;
    private final BorrowTransactionDAO transactionDAO;
    private final ReservationDAO reservationDAO;
    private final BorrowRequestDAO borrowRequestDAO;

    public StudentService(StudentDAO studentDAO) {
        this(studentDAO, null, null, null);
    }

    public StudentService(StudentDAO studentDAO,
                          BorrowTransactionDAO transactionDAO,
                          ReservationDAO reservationDAO,
                          BorrowRequestDAO borrowRequestDAO) {
        this.studentDAO = studentDAO;
        this.transactionDAO = transactionDAO;
        this.reservationDAO = reservationDAO;
        this.borrowRequestDAO = borrowRequestDAO;
    }

    public void addStudent(Student student) throws LibraryException {
        validateStudent(student);
        if (studentDAO.findById(student.getKtuId()).isPresent()) {
            throw new LibraryException("Student with KTU ID " + student.getKtuId() + " already exists.");
        }
        studentDAO.create(student);
    }

    public void updateStudent(Student student) throws LibraryException {
        validateStudent(student);
        if (studentDAO.findById(student.getKtuId()).isEmpty()) {
            throw new LibraryException("Student not found for update.");
        }
        studentDAO.update(student);
    }

    public void deleteStudent(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            throw new LibraryException("KTU ID cannot be empty.");
        }

        if (studentDAO.findById(ktuId).isEmpty()) {
            throw new LibraryException("Student not found for deletion.");
        }

        List<String> blockingReasons = new ArrayList<>();

        // 1. Check borrow transactions
        if (transactionDAO != null) {
            List<BorrowTransaction> transactions = transactionDAO.findByStudentId(ktuId);
            if (transactions != null && !transactions.isEmpty()) {
                long active = transactions.stream().filter(t -> "ACTIVE".equalsIgnoreCase(t.getStatus())).count();
                long completed = transactions.size() - active;
                if (active > 0 && completed > 0) {
                    blockingReasons.add(active + " active borrowed book(s) and " + completed + " borrowing history record(s)");
                } else if (active > 0) {
                    blockingReasons.add(active + " active borrowed book(s)");
                } else {
                    blockingReasons.add(completed + " borrowing history record(s)");
                }
            }
        }

        // 2. Check borrow requests
        if (borrowRequestDAO != null) {
            List<BorrowRequest> requests = borrowRequestDAO.findByStudentId(ktuId);
            if (requests != null && !requests.isEmpty()) {
                long pending = requests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();
                long past = requests.size() - pending;
                if (pending > 0 && past > 0) {
                    blockingReasons.add(pending + " pending borrow request(s) and " + past + " past request(s)");
                } else if (pending > 0) {
                    blockingReasons.add(pending + " pending borrow request(s)");
                } else {
                    blockingReasons.add(requests.size() + " borrow request(s)");
                }
            }
        }

        // 3. Check reservations
        if (reservationDAO != null) {
            List<Reservation> reservations = reservationDAO.findByStudentId(ktuId);
            if (reservations != null && !reservations.isEmpty()) {
                long pending = reservations.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();
                long past = reservations.size() - pending;
                if (pending > 0 && past > 0) {
                    blockingReasons.add(pending + " pending reservation(s) and " + past + " past reservation(s)");
                } else if (pending > 0) {
                    blockingReasons.add(pending + " pending reservation(s)");
                } else {
                    blockingReasons.add(reservations.size() + " reservation(s)");
                }
            }
        }

        // Fallback to studentDAO related records check if specific DAOs were not provided
        if (blockingReasons.isEmpty() && transactionDAO == null && reservationDAO == null && borrowRequestDAO == null) {
            List<String> daoReasons = studentDAO.getRelatedRecordsSummary(ktuId);
            if (daoReasons != null && !daoReasons.isEmpty()) {
                blockingReasons.addAll(daoReasons);
            }
        }

        if (!blockingReasons.isEmpty()) {
            throw new LibraryException(String.format(
                    "Cannot permanently delete student %s: Related records exist (%s). " +
                    "To preserve borrowing history and system records, please deactivate the student instead.",
                    ktuId, String.join(", ", blockingReasons)
            ));
        }

        studentDAO.delete(ktuId);
    }

    public void deactivateStudent(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            throw new LibraryException("KTU ID cannot be empty.");
        }
        Student student = studentDAO.findById(ktuId)
                .orElseThrow(() -> new LibraryException("Student not found."));
        if (!student.isActive()) {
            throw new LibraryException("Student " + ktuId + " is already deactivated.");
        }
        student.setStatus("INACTIVE");
        studentDAO.update(student);
    }

    public void activateStudent(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            throw new LibraryException("KTU ID cannot be empty.");
        }
        Student student = studentDAO.findById(ktuId)
                .orElseThrow(() -> new LibraryException("Student not found."));
        if (student.isActive()) {
            throw new LibraryException("Student " + ktuId + " is already active.");
        }
        student.setStatus("ACTIVE");
        studentDAO.update(student);
    }

    public boolean hasRelatedRecords(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            return false;
        }
        if (transactionDAO != null) {
            List<BorrowTransaction> tx = transactionDAO.findByStudentId(ktuId);
            if (tx != null && !tx.isEmpty()) return true;
        }
        if (borrowRequestDAO != null) {
            List<BorrowRequest> req = borrowRequestDAO.findByStudentId(ktuId);
            if (req != null && !req.isEmpty()) return true;
        }
        if (reservationDAO != null) {
            List<Reservation> res = reservationDAO.findByStudentId(ktuId);
            if (res != null && !res.isEmpty()) return true;
        }
        return studentDAO.hasRelatedRecords(ktuId);
    }

    public Student getStudent(String ktuId) throws LibraryException {
        return studentDAO.findById(ktuId)
                .orElseThrow(() -> new LibraryException("Student not found."));
    }

    public List<Student> getAllStudents() throws LibraryException {
        return studentDAO.findAll();
    }

    private void validateStudent(Student student) throws LibraryException {
        if (student == null) throw new LibraryException("Student cannot be null.");
        if (student.getKtuId() == null || student.getKtuId().trim().isEmpty()) {
            throw new LibraryException("Student KTU ID is required.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new LibraryException("Student name is required.");
        }
    }
}
