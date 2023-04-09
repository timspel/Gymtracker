module com.gymtracker.gymtracker {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.gymtracker.gymtracker to javafx.fxml;
    exports com.gymtracker.gymtracker;
    exports com.gymtracker.gymtracker.controller;
    opens com.gymtracker.gymtracker.controller to javafx.fxml;
}