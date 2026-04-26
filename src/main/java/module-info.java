module sportsmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens application to javafx.fxml, com.google.gson;
    opens domain to javafx.fxml, com.google.gson;
    opens football to javafx.fxml, com.google.gson;
    opens sport to javafx.fxml, com.google.gson;

    exports application;
    exports domain;
    exports football;
    exports sport;
}
