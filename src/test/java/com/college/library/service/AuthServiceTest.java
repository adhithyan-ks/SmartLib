package com.college.library.service;

import com.college.library.dao.LibrarianDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    
    private LibrarianDAO mockLibrarianDAO;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockLibrarianDAO = new LibrarianDAO() {
            @Override
            public void create(Librarian entity) {}
            @Override
            public Optional<Librarian> findById(Integer id) { return Optional.empty(); }
            @Override
            public List<Librarian> findAll() { return null; }
            @Override
            public void update(Librarian entity) {}
            @Override
            public void delete(Integer id) {}
            @Override
            public Optional<Librarian> findByUsername(String username) throws LibraryException {
                if ("admin".equals(username)) {
                    Librarian l = new Librarian(1, "admin", BCrypt.hashpw("password123", BCrypt.gensalt(12)), "Admin Name");
                    return Optional.of(l);
                }
                return Optional.empty();
            }
        };
        authService = new AuthService(mockLibrarianDAO);
    }

    @Test
    void testLoginSuccess() throws Exception {
        Librarian l = authService.login("admin", "password123");
        assertNotNull(l);
        assertEquals("admin", l.getUsername());
    }

    @Test
    void testLoginFailureWrongPassword() {
        assertThrows(LibraryException.class, () -> authService.login("admin", "wrongpass"));
    }

    @Test
    void testLoginFailureWrongUser() {
        assertThrows(LibraryException.class, () -> authService.login("unknown", "password123"));
    }
}
