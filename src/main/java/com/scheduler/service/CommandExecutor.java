package com.scheduler.service;

import com.scheduler.model.ExecutionLog;
import com.scheduler.model.Task;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class CommandExecutor {

    private final LogService logService;

    public CommandExecutor() {
        this.logService = new LogService();
    }

    public ExecutionLog execute(Task task) {
        ExecutionLog log = new ExecutionLog();
        log.setTaskName(task.getName());
        log.setCommand(task.getCommand());
        log.setStartTime(LocalDateTime.now());
        log.setScheduledTime(LocalDateTime.now());

        System.out.println("Executing task: " + task.getName());

        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            ProcessBuilder builder = new ProcessBuilder();
            if (isWindows) {
                builder.command("cmd.exe", "/c", task.getCommand());
            } else {
                builder.command("sh", "-c", task.getCommand());
            }
            
            builder.redirectErrorStream(true);
            Process process = builder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            boolean finished = process.waitFor(5, TimeUnit.MINUTES);
            log.setEndTime(LocalDateTime.now());
            
            if (finished) {
                log.setExitCode(process.exitValue());
                log.setOutputLog(output.toString());
                log.setStatus(process.exitValue() == 0 ? "SUCCESS" : "FAILED");
            } else {
                process.destroy();
                log.setExitCode(-1);
                log.setOutputLog(output.toString() + "\n[TIMEOUT]");
                log.setStatus("TIMEOUT");
                log.setErrorMessage("Task timed out after 5 minutes");
            }

        } catch (Exception e) {
            log.setEndTime(LocalDateTime.now());
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
        
        logService.saveLog(log);
        return log;
    }
}
