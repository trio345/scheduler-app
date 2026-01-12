package com.scheduler.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scheduler.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private static final String FILE_PATH = "tasks.json";
    private final ObjectMapper mapper;

    public TaskRepository() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule()); // Handle LocalDateTime
    }

    public void saveTasks(List<Task> tasks) {
        try {
            mapper.writeValue(new File(FILE_PATH), tasks);
        } catch (IOException e) {
            System.err.println("Failed to save tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Task> loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Task>>() {});
        } catch (IOException e) {
            System.err.println("Failed to load tasks: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
