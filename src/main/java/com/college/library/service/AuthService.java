package com.college.library.service;

import com.college.library.dao.LibrarianDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import com.college.library.model.Student;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final LibrarianDAO librarianDAO;
    private StudentDAO studentDAO;

    public AuthService(LibrarianDAO librarianDAO) {
        this(librarianDAO, null);
    }

    public AuthService(LibrarianDAO librarianDAO, StudentDAO studentDAO) {
        this.librarianDAO = librarianDAO;
        this.studentDAO = studentDAO;
    }

    public void setStudentDAO(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public Librarian login(String username, String password) throws LibraryException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new LibraryException("Username and password cannot be empty");
        }

        Librarian librarian = librarianDAO.findByUsername(username.trim())
                .orElseThrow(() -> new LibraryException("Invalid username or password"));

        if (!BCrypt.checkpw(password, librarian.getPasswordHash())) {
            throw new LibraryException("Invalid username or password");
        }

        return librarian;
    }

    public void registerLibrarian(String username, String password, String name) throws LibraryException {
        if (librarianDAO.findByUsername(username).isPresent()) {
            throw new LibraryException("Username already exists");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        librarianDAO.create(new Librarian(0, username, hashedPassword, name));
    }

    public Student loginStudent(String ktuId, String password) throws LibraryException {
        if (studentDAO == null) throw new LibraryException("StudentDAO not initialized in AuthService");
        if (ktuId == null || ktuId.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new LibraryException("KTU ID and password cannot be empty");
        }

        Student student = studentDAO.findById(ktuId.trim())
                .orElseThrow(() -> new LibraryException("Invalid KTU ID or password"));

        if (student.getPasswordHash() == null) {
            throw new LibraryException("Account exists but not registered for login. Please register first.");
        }

        if (!student.isActive()) {
            throw new LibraryException("Student account is inactive. Please contact the librarian.");
        }

        if (!BCrypt.checkpw(password, student.getPasswordHash())) {
            throw new LibraryException("Invalid KTU ID or password");
        }

        return student;
    }

    public void registerStudent(String ktuId, String name, String password) throws LibraryException {
        registerStudent(ktuId, name, "", "", "", "", "", password);
    }

    public void registerStudent(String ktuId, String name, String branch, String semester, String batch, String email, String phone, String password) throws LibraryException {
        if (studentDAO == null) throw new LibraryException("StudentDAO not initialized in AuthService");

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        Optional<Student> existingOpt = studentDAO.findById(ktuId);

        if (existingOpt.isPresent()) {
            Student existing = existingOpt.get();
            if (!existing.isActive()) {
                throw new LibraryException("Student account is inactive. Please contact the librarian.");
            }
            if (existing.getPasswordHash() != null) {
                throw new LibraryException("Student already registered. Please login.");
            }
            existing.setPasswordHash(hashedPassword);
            if (name != null && !name.trim().isEmpty()) {
                existing.setName(name.trim());
            }
            studentDAO.update(existing);
        } else {
            int sem = 1;
            if (semester != null && !semester.trim().isEmpty()) {
                try {
                    sem = Integer.parseInt(semester.trim());
                } catch (NumberFormatException ignored) {
                }
            }
            studentDAO.create(new Student(ktuId, hashedPassword, name, branch, sem, batch, email, phone));
        }
    }

    public boolean isStudentRegistered(String ktuId) {
        if (studentDAO == null) return false;
        try {
            return studentDAO.findById(ktuId)
                    .map(s -> s.getPasswordHash() != null)
                    .orElse(false);
        } catch (LibraryException e) {
            return false;
        }
    }
}
