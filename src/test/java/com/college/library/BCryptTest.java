package com.college.library;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import static org.junit.jupiter.api.Assertions.*;

public class BCryptTest {
    @Test
    public void testHash() {
        String dbHash = "$2a$12$Z0E0u3/lZ.1Q4E0Rj.8bGuR8tG0t7a4H0z3D6z.0A2h0p1t0y6B6C";
        
        System.out.println("Does dbHash match admin123? " + BCrypt.checkpw("admin123", dbHash));
        System.out.println("Does dbHash match password123? " + BCrypt.checkpw("password123", dbHash));
        
        System.out.println("New hash for password123: " + BCrypt.hashpw("password123", BCrypt.gensalt(12)));
    }
}
