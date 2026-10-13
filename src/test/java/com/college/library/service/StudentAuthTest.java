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
            
            // 6. Inactive student cannot login
            s.setStatus("INACTIVE");
            studentDAO.update(s);
            assertThrows(LibraryException.class, () -> {
                authService.loginStudent(ktuId, password);
            });
            
            // Cleanup
            studentDAO.delete(ktuId);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testKtuIdFormatValidation() {
        String regex = "^[A-Z]{3}[0-9]{2}[A-Z]{2}[0-9]{3}$";

        // Valid KTU IDs: any 3-letter uppercase prefix, any 2-digit year, 2-letter branch, 3 digits
        assertTrue("TVE25CS002".matches(regex));
        assertTrue("TCR20CS001".matches(regex));
        assertTrue("TCR20CS002".matches(regex));
        assertTrue("ABC19EC123".matches(regex));
        assertTrue("XYZ30DM456".matches(regex));
        assertTrue("ABC25CS002".matches(regex));
        assertTrue("XYZ22EC010".matches(regex));

        // Invalid KTU IDs
        assertFalse("tve25cs002".matches(regex));  // lowercase prefix and branch
        assertFalse("TV25CS002".matches(regex));   // 2 prefix letters
        assertFalse("TVER25CS002".matches(regex)); // 4 prefix letters
        assertFalse("TVE2CS002".matches(regex));   // 1 digit year
        assertFalse("TVE202CS002".matches(regex)); // 3 digit year
        assertFalse("TVEABCS002".matches(regex));  // letters instead of digits for year
        assertFalse("TVE25C002".matches(regex));   // 1 branch letter
        assertFalse("TVE25CSE002".matches(regex)); // 3 branch letters
        assertFalse("TVE25CS02".matches(regex));   // 2 trailing digits
        assertFalse("TVE25CS0002".matches(regex)); // 4 trailing digits
        assertFalse("12325CS002".matches(regex));  // numeric prefix
    }

    @Test
    public void testNonTveStudentRegistrationAndLogin() {
        AuthService authService = new AuthService(null);
        StudentDAOImpl studentDAO = new StudentDAOImpl();
        authService.setStudentDAO(studentDAO);

        String ktuId = "ABC25CS999";
        String name = "ABC Student";
        String password = "mypassword";

        try {
            studentDAO.delete(ktuId);

            authService.registerStudent(ktuId, name, password);

            Student loggedIn = authService.loginStudent(ktuId, password);
            assertNotNull(loggedIn);
            assertEquals("ABC25CS999", loggedIn.getKtuId());
            assertEquals("ABC Student", loggedIn.getName());

            // Cleanup
            studentDAO.delete(ktuId);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
