package com.college.library;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BCryptTest {
    @Test
    public void testHashGenerationAndVerification() {
        String password = "password123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        assertTrue(BCrypt.checkpw(password, hash), "Generated BCrypt hash must verify against the original password.");
    }
}
