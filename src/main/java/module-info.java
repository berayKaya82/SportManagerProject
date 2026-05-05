module sportsmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.google.gson;

    opens application to javafx.fxml, com.google.gson;
    opens domain to javafx.fxml, com.google.gson;
    opens football to javafx.fxml, com.google.gson;
    opens handball to javafx.fxml, com.google.gson;
    opens sport to javafx.fxml, com.google.gson;
    opens ui to javafx.fxml;
    opens ui.controller to javafx.fxml;

    exports application;
    exports domain;
    exports football;
    exports handball;
    exports sport;
    exports ui;
    exports ui.controller;
}