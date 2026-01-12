package com.scheduler.model;

import java.time.LocalDateTime;

public class ExecutionLog {
    private long id;
    private String taskName;
    private String command;
    private LocalDateTime scheduledTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private int exitCode;
    private String outputLog;
    private String errorMessage;

    public ExecutionLog() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }

    public String getOutputLog() { return outputLog; }
    public void setOutputLog(String outputLog) { this.outputLog = outputLog; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
