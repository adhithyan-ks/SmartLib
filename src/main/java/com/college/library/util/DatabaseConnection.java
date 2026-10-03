package com.college.library.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("database.properties not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("db.url", "jdbc:mysql://localhost:3306/smartlib"),
            props.getProperty("db.user", "root"),
            props.getProperty("db.password", "root")
        );
    }
}
