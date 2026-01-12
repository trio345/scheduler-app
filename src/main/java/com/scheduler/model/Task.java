package com.scheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Task {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty command = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty scheduleValue = new SimpleStringProperty();
    private final BooleanProperty active = new SimpleBooleanProperty(true);
    private final ObjectProperty<LocalDateTime> lastRun = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty("IDLE");

    public Task() {}

    public Task(String name, String command, String type, String scheduleValue) {
        setName(name);
        setCommand(command);
        setType(type);
        setScheduleValue(scheduleValue);
    }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getCommand() { return command.get(); }
    public StringProperty commandProperty() { return command; }
    public void setCommand(String command) { this.command.set(command); }

    public String getType() { return type.get(); }
    public StringProperty typeProperty() { return type; }
    public void setType(String type) { this.type.set(type); }

    public String getScheduleValue() { return scheduleValue.get(); }
    public StringProperty scheduleValueProperty() { return scheduleValue; }
    public void setScheduleValue(String scheduleValue) { this.scheduleValue.set(scheduleValue); }

    public boolean isActive() { return active.get(); }
    public BooleanProperty activeProperty() { return active; }
    public void setActive(boolean active) { this.active.set(active); }

    public LocalDateTime getLastRun() { return lastRun.get(); }
    public ObjectProperty<LocalDateTime> lastRunProperty() { return lastRun; }
    public void setLastRun(LocalDateTime lastRun) { this.lastRun.set(lastRun); }
    
    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String status) { this.status.set(status); }

    private final StringProperty recentLog = new SimpleStringProperty("");
    public String getRecentLog() { return recentLog.get(); }
    public StringProperty recentLogProperty() { return recentLog; }
    public void setRecentLog(String recentLog) { this.recentLog.set(recentLog); }

    @Override
    public String toString() {
        return getName();
    }
}
