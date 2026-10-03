package com.college.library.service;

import com.college.library.dao.impl.StudentDAOImpl;
import com.college.library.exception.LibraryException;
import com.college.library.model.Student;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentAuthTest {

    @Test
    public void testStudentRegistrationAndLogin() {
        AuthService authService = new AuthService(null);
        StudentDAOImpl studentDAO = new StudentDAOImpl();
        authService.setStudentDAO(studentDAO);
        
        String ktuId = "TVE23CS001";
        String name = "Test Student";
        String password = "mypassword";
        
        try {
            // Delete if exists
            studentDAO.delete(ktuId);
            
            // 1. Register student
            authService.registerStudent(ktuId, name, password);
            
            // 2. Verify record exists and has password hash
            Student s = studentDAO.findById(ktuId).get();
            assertNotNull(s.getPasswordHash());
            assertEquals(name, s.getName());
            
            // 3. Login with correct credentials
            Student loggedIn = authService.loginStudent(ktuId, password);
            assertNotNull(loggedIn);
            
            // 4. Duplicate registration rejected
            assertThrows(LibraryException.class, () -> {
                authService.registerStudent(ktuId, name, password);
            });
            
            // 5. Invalid credentials rejected
            assertThrows(LibraryException.class, () -> {
                authService.loginStudent(ktuId, "wrongpassword");
            });
            
            // Cleanup
            studentDAO.delete(ktuId);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
