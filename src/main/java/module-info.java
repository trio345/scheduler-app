module com.scheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;

    requires java.sql;

    opens com.scheduler.ui to javafx.fxml;
    opens com.scheduler.model to com.fasterxml.jackson.databind;

    exports com.scheduler.ui;
}
