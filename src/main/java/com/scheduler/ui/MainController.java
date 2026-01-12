package com.scheduler.ui;

import com.scheduler.model.Task;
import com.scheduler.service.SchedulerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameCol;
    @FXML private TableColumn<Task, String> scheduleCol;
    @FXML private TableColumn<Task, String> statusCol;
    @FXML private TableColumn<Task, String> lastRunCol;

    @FXML private Button startBtn;
    @FXML private Button stopBtn;
    @FXML private Label statusLabel;
    @FXML private TextArea recentStatusArea;

    private SchedulerService schedulerService;
    private ObservableList<Task> taskList;

    private static final java.time.format.DateTimeFormatter DATE_FORMATTER = 
        java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        schedulerService = new SchedulerService();
        taskList = FXCollections.observableArrayList(schedulerService.getTasks());

        // Global Log Callback
        schedulerService.setOnTaskFinished(log -> {
            String timestamp = java.time.LocalDateTime.now().format(DATE_FORMATTER);
            String message = String.format("[%s] Task '%s' (%s): %s\n", 
                timestamp, log.getTaskName(), log.getStatus(), log.getOutputLog().trim());
            recentStatusArea.appendText(message);
        });

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        scheduleCol.setCellValueFactory(cellData -> {
            Task t = cellData.getValue();
            String displayValue = t.getScheduleValue();
            if ("INTERVAL".equals(t.getType())) {
                displayValue += " minute(s)";
            }
            return new javafx.beans.property.SimpleStringProperty(
                    t.getType() + ": " + displayValue);
        });
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        lastRunCol.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                 if (cellData.getValue().lastRunProperty().get() != null) {
                     return cellData.getValue().lastRunProperty().get().format(DATE_FORMATTER);
                 }
                 return "-";
            }, cellData.getValue().lastRunProperty())
        );

        taskTable.setItems(taskList);
        updateSchedulerStatus(false);
        
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit Task");
        MenuItem toggleItem = new MenuItem("Toggle Enable/Disable");
        MenuItem deleteItem = new MenuItem("Delete Task");

        editItem.setOnAction(event -> {
            Task selected = taskTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_task.fxml"));
                    Parent page = loader.load();

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Edit Task");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(MainApp.getPrimaryStage());
                    Scene scene = new Scene(page);
                    dialogStage.setScene(scene);

                    TaskAddController controller = loader.getController();
                    controller.setDialogStage(dialogStage);
                    controller.setTaskData(selected);
                    
                    dialogStage.showAndWait();

                    Task updatedValues = controller.getTask();
                    if (updatedValues != null) {
                        selected.setName(updatedValues.getName());
                        selected.setCommand(updatedValues.getCommand());
                        selected.setType(updatedValues.getType());
                        selected.setScheduleValue(updatedValues.getScheduleValue());
                        schedulerService.updateTask(selected);
                        taskTable.refresh();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not load Update Task dialog");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
        
        toggleItem.setOnAction(event -> {
             Task selected = taskTable.getSelectionModel().getSelectedItem();
             if (selected != null) {
                 selected.setActive(!selected.isActive());
                 if (!selected.isActive()) {
                     selected.setStatus("DISABLED");
                 } else {
                     selected.setStatus("IDLE");
                 }
                 schedulerService.updateTask(selected);
                 taskTable.refresh();
             }
        });
        
        deleteItem.setOnAction(event -> {
            Task selected = taskTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Task");
                alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");
                if (alert.showAndWait().get() == ButtonType.OK) {
                   schedulerService.removeTask(selected);
                   taskList.remove(selected);
                }
            }
        });
        
        contextMenu.getItems().addAll(editItem, toggleItem, deleteItem);
        taskTable.setContextMenu(contextMenu);
    }

    @FXML
    private void handleAddTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_task.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Task");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(MainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TaskAddController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();

            Task newTask = controller.getTask();
            if (newTask != null) {
                schedulerService.addTask(newTask);
                taskList.add(newTask); // Update UI list
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStart() {
        schedulerService.start();
        updateSchedulerStatus(true);
    }

    @FXML
    private void handleStop() {
        schedulerService.stop();
        updateSchedulerStatus(false);
    }

    private void updateSchedulerStatus(boolean running) {
        if (running) {
            statusLabel.setText("Status: RUNNING");
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
        } else {
            statusLabel.setText("Status: STOPPED");
            startBtn.setDisable(false);
            stopBtn.setDisable(true);
        }
    }
}
