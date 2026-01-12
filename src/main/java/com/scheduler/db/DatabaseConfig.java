package com.scheduler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/scheduler_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
    
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            System.err.println("DB Connection Failed: " + e.getMessage());
            return false;
        }
    }
}
