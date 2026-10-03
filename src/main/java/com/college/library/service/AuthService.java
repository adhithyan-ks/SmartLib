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
}
