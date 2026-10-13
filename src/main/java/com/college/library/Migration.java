package com.college.library;

import com.college.library.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;

public class Migration {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS borrow_requests (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "student_id VARCHAR(20) NOT NULL," +
                "book_id VARCHAR(50) NOT NULL," +
                "request_date DATETIME NOT NULL," +
                "status VARCHAR(20) NOT NULL," +
                "FOREIGN KEY (student_id) REFERENCES students(ktu_id)," +
                "FOREIGN KEY (book_id) REFERENCES books(accession_id)" +
                ")");

            try {
                stmt.executeUpdate("ALTER TABLE students ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE'");
            } catch (Exception ignored) {
                // Column might already exist
            }

            try {
                stmt.executeUpdate("ALTER TABLE books DROP COLUMN publisher");
            } catch (Exception ignored) {
                // Column might already be dropped or not exist
            }

            try {
                stmt.executeUpdate("ALTER TABLE books DROP COLUMN edition");
            } catch (Exception ignored) {
                // Column might already be dropped or not exist
            }

            System.out.println("Migration successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
