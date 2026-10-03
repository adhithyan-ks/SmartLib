package com.college.library.service;

import com.college.library.dao.LibrarianDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;

public class AuthService {
    private final LibrarianDAO librarianDAO;

    public AuthService(LibrarianDAO librarianDAO) {
        this.librarianDAO = librarianDAO;
    }

    public Librarian login(String username, String password) throws LibraryException {
        if (username == null || username.trim().isEmpty()) {
            throw new LibraryException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new LibraryException("Password cannot be empty");
        }

        Optional<Librarian> librarianOpt = librarianDAO.findByUsername(username);
        
        if (librarianOpt.isEmpty()) {
            throw new LibraryException("Invalid username or password");
        }
        
        Librarian librarian = librarianOpt.get();
        if (BCrypt.checkpw(password, librarian.getPasswordHash())) {
            return librarian;
        } else {
            throw new LibraryException("Invalid username or password");
        }
    }

    public void registerLibrarian(String username, String password, String name) throws LibraryException {
        if (librarianDAO.findByUsername(username).isPresent()) {
            throw new LibraryException("Username already exists");
        }
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        Librarian newLibrarian = new Librarian(0, username, hashedPassword, name);
        librarianDAO.create(newLibrarian);
    }
    
    // Student Authentication
    private com.college.library.dao.StudentDAO studentDAO;
    
    public void setStudentDAO(com.college.library.dao.StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }
    
    public com.college.library.model.Student loginStudent(String ktuId, String password) throws LibraryException {
        if (studentDAO == null) throw new LibraryException("StudentDAO not initialized in AuthService");
        if (ktuId == null || ktuId.trim().isEmpty()) throw new LibraryException("KTU ID cannot be empty");
        if (password == null || password.trim().isEmpty()) throw new LibraryException("Password cannot be empty");
        
        Optional<com.college.library.model.Student> studentOpt = studentDAO.findById(ktuId.trim());
        if (studentOpt.isEmpty()) {
            throw new LibraryException("Invalid KTU ID or password");
        }
        
        com.college.library.model.Student student = studentOpt.get();
        if (student.getPasswordHash() == null) {
            throw new LibraryException("Account exists but not registered for login. Please register first.");
        }
        
        if (BCrypt.checkpw(password, student.getPasswordHash())) {
            return student;
        } else {
            throw new LibraryException("Invalid KTU ID or password");
        }
    }
    
    public void registerStudent(String ktuId, String name, String password) throws LibraryException {
        if (studentDAO == null) throw new LibraryException("StudentDAO not initialized in AuthService");
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        
        Optional<com.college.library.model.Student> existingOpt = studentDAO.findById(ktuId);
        if (existingOpt.isPresent()) {
            com.college.library.model.Student existing = existingOpt.get();
            if (existing.getPasswordHash() != null) {
                throw new LibraryException("Student already registered. Please login.");
            }
            existing.setPasswordHash(hashedPassword);
            if (name != null && !name.trim().isEmpty()) {
                existing.setName(name.trim());
            }
            studentDAO.update(existing);
        } else {
            com.college.library.model.Student newStudent = new com.college.library.model.Student(ktuId, hashedPassword, name, "", 1, "", "", "");
            studentDAO.create(newStudent);
        }
    }

    public boolean isStudentRegistered(String ktuId) {
        if (studentDAO == null) return false;
        try {
            Optional<com.college.library.model.Student> studentOpt = studentDAO.findById(ktuId);
            return studentOpt.isPresent() && studentOpt.get().getPasswordHash() != null;
        } catch (LibraryException e) {
            return false;
        }
    }
}
