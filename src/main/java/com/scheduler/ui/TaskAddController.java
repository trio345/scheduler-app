package com.scheduler.ui;

import com.scheduler.model.Task;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class TaskAddController {

    @FXML private TextField nameField;
    @FXML private TextArea commandArea;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField scheduleValueField;
    @FXML private Label headerLabel;
    @FXML private javafx.scene.layout.VBox specificDateBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private ListView<String> dateList;

    private Stage dialogStage;
    private Task task;

    private static final java.time.format.DateTimeFormatter DATA_FORMAT = 
            java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
    public void initialize() {
        specificDateBox.managedProperty().bind(specificDateBox.visibleProperty());
        scheduleValueField.managedProperty().bind(scheduleValueField.visibleProperty());

        datePicker.setConverter(new javafx.util.StringConverter<java.time.LocalDate>() {
             @Override
             public String toString(java.time.LocalDate date) {
                 if (date != null) {
                     return DATA_FORMAT.format(date);
                 } else {
                     return "";
                 }
             }
             @Override
             public java.time.LocalDate fromString(String string) {
                 if (string != null && !string.isEmpty()) {
                     return java.time.LocalDate.parse(string, DATA_FORMAT);
                 } else {
                     return null;
                 }
             }
        });
        typeCombo.setItems(FXCollections.observableArrayList("INTERVAL", "FIXED_TIME", "SPECIFIC_DATE_TIME"));
        typeCombo.setValue("INTERVAL");

        updateInputVisibility("INTERVAL");

        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            updateInputVisibility(newVal);
        });
    }

    private void updateInputVisibility(String type) {
        if ("SPECIFIC_DATE_TIME".equals(type)) {
            scheduleValueField.setVisible(false);
            specificDateBox.setVisible(true);
        } else {
            scheduleValueField.setVisible(true);
            specificDateBox.setVisible(false);
            
            switch (type) {
                case "INTERVAL":
                    scheduleValueField.setPromptText("Minutes (e.g. 60)");
                    break;
                case "FIXED_TIME":
                    scheduleValueField.setPromptText("HH:mm (e.g. 14:30)");
                    break;
            }
        }
        
        if (dialogStage != null) {
            dialogStage.sizeToScene();
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTaskData(Task task) {
        this.task = task;
        if (task != null) {
            nameField.setText(task.getName());
            commandArea.setText(task.getCommand());
            typeCombo.setValue(task.getType());
            
            if ("SPECIFIC_DATE_TIME".equals(task.getType())) {
                try {
                    String[] dates = task.getScheduleValue().split(",");
                    dateList.getItems().clear();
                    for (String d : dates) {
                        dateList.getItems().add(d.trim());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                scheduleValueField.setText(task.getScheduleValue());
            }
            
            headerLabel.setText("Edit Task");
        } else {
            headerLabel.setText("Add New Task");
        }
    }

    public Task getTask() {
        return task;
    }

    @FXML
    private void handleSave() {
        if (isValid()) {
            String finalValue;
            if ("SPECIFIC_DATE_TIME".equals(typeCombo.getValue())) {
                finalValue = String.join(",", dateList.getItems());
            } else {
                finalValue = scheduleValueField.getText();
            }

            task = new Task(
                    nameField.getText(),
                    commandArea.getText(),
                    typeCombo.getValue(),
                    finalValue
            );
            dialogStage.close();
        }
    }

    @FXML
    private void handleAddDate() {
        if (datePicker.getValue() != null && timeField.getText() != null && !timeField.getText().isEmpty()) {
            try {
                String val = DATA_FORMAT.format(datePicker.getValue()) + " " + timeField.getText();
                if (!dateList.getItems().contains(val)) {
                    dateList.getItems().add(val);
                }
                datePicker.setValue(null);
                timeField.clear();
            } catch (Exception e) {
                 Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Date/Time Format");
                 alert.show();
            }
        }
    }

    @FXML
    private void handleRemoveDate() {
        String selected = dateList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dateList.getItems().remove(selected);
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isValid() {
        String errorMessage = "";
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "No valid task name!\n";
        }
        if (commandArea.getText() == null || commandArea.getText().isEmpty()) {
            errorMessage += "No valid command!\n";
        }
        if ("SPECIFIC_DATE_TIME".equals(typeCombo.getValue())) {
             if (dateList.getItems().isEmpty()) {
                 errorMessage += "No dates added to the list!\n";
             }
        } else {
            if (scheduleValueField.getText() == null || scheduleValueField.getText().isEmpty()) {
                errorMessage += "No valid schedule value!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}
