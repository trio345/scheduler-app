package com.scheduler.service;

import com.scheduler.db.DatabaseConfig;
import com.scheduler.model.ExecutionLog;
import java.sql.*;

public class LogService {

    public void saveLog(ExecutionLog log) {
        String sql = "INSERT INTO execution_logs (task_name, command, scheduled_time, start_time, end_time, status, exit_code, output_log, error_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, log.getTaskName());
            pstmt.setString(2, log.getCommand());
            pstmt.setObject(3, log.getScheduledTime());
            pstmt.setObject(4, log.getStartTime());
            pstmt.setObject(5, log.getEndTime());
            pstmt.setString(6, log.getStatus());
            pstmt.setInt(7, log.getExitCode());
            pstmt.setString(8, log.getOutputLog());
            pstmt.setString(9, log.getErrorMessage());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to save log to DB: " + e.getMessage());
        }
    }
}
