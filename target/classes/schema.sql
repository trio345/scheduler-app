CREATE TABLE IF NOT EXISTS execution_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(255) NOT NULL,
    command TEXT,
    scheduled_time DATETIME,
    start_time DATETIME,
    end_time DATETIME,
    status VARCHAR(50),
    exit_code INT,
    output_log LONGTEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
