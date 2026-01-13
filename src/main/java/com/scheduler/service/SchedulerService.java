package com.scheduler.service;

import com.scheduler.model.Task;
import javafx.application.Platform;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {
    private final List<Task> tasks = new ArrayList<>();
    private final CommandExecutor executor;
    private final TaskRepository taskRepository;
    private ScheduledExecutorService scheduler;
    private boolean running = false;
    private java.util.function.Consumer<com.scheduler.model.ExecutionLog> onTaskFinished;

    public void setOnTaskFinished(java.util.function.Consumer<com.scheduler.model.ExecutionLog> callback) {
        this.onTaskFinished = callback;
    }

    public SchedulerService() {
        this.executor = new CommandExecutor();
        this.taskRepository = new TaskRepository();
        tasks.addAll(taskRepository.loadTasks());
    }

    public void addTask(Task task) {
        tasks.add(task);
        taskRepository.saveTasks(tasks);
    }
    
    public void removeTask(Task task) {
        tasks.remove(task);
        taskRepository.saveTasks(tasks);
    }

    public void updateTask(Task task) {
        taskRepository.saveTasks(tasks);
    }
    
    public List<Task> getTasks() {
        return tasks;
    }

    public void start() {
        if (running) return;
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Check activity every 10 seconds
        scheduler.scheduleAtFixedRate(this::checkTasks, 0, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        if (!running) return;
        running = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    private void checkTasks() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Scheduler tick at : " + now);

        for (Task task : tasks) {
            if (!task.isActive()) {
                updateTaskStatus(task, "INACTIVE");
                continue;
            }

            boolean shouldRun = false;

            try {
                if ("INTERVAL".equals(task.getType())) {
                    int intervalMin = Integer.parseInt(task.getScheduleValue());
                    if (task.getLastRun() == null) {
                        shouldRun = true;
                    } else {
                        Duration duration = Duration.between(task.getLastRun(), now);
                        if (duration.toMinutes() >= intervalMin) {
                            shouldRun = true;
                        }
                    }
                } else if ("FIXED_TIME".equals(task.getType())) {
                    // Format time HH:mm
                    LocalTime targetTime = LocalTime.parse(task.getScheduleValue());
                    LocalDateTime scheduledDateTime = LocalDateTime.of(now.toLocalDate(), targetTime);

                    // if (now.getHour() == targetTime.getHour() && now.getMinute() == targetTime.getMinute()) {
                    //     if (task.getLastRun() == null || task.getLastRun().toLocalDate().isBefore(now.toLocalDate())) {
                    //         shouldRun = true;
                    //     }
                    // }

                    if (now.isAfter(scheduledDateTime)) {
                        if (task.getLastRun() == null || task.getLastRun().isBefore(scheduledDateTime)) {
                            shouldRun = true;
                        }
                    }
                } else if ("SPECIFIC_DATE_TIME".equals(task.getType())) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    List<String> triggers = task.getScheduleList();
                    
                    for (String trigger : triggers) {
                        try {
                            LocalDateTime targetDateTime = LocalDateTime.parse(trigger, formatter);
                            
                            if (now.isAfter(targetDateTime) || now.isEqual(targetDateTime)) {
                                if (task.getLastRun() == null || task.getLastRun().isBefore(targetDateTime)) {
                                    shouldRun = true;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing " + trigger + ": " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error parsing " + task.getName() + ": " + e.getMessage());
            }

            if (shouldRun) {
                new Thread(() -> {
                    updateTaskStatus(task, "RUNNING");
                    com.scheduler.model.ExecutionLog log = executor.execute(task);
                    Platform.runLater(() -> {
                        task.setLastRun(LocalDateTime.now());
                        task.setRecentLog(log.getOutputLog());
                        taskRepository.saveTasks(tasks);
                        if (onTaskFinished != null) {
                            onTaskFinished.accept(log);
                        }
                    });
                    updateTaskStatus(task, "IDLE");
                }).start();
            }
        }
    }
    
    private void updateTaskStatus(Task task, String status) {
        Platform.runLater(() -> task.setStatus(status));
    }
}
