module com.gymtracker.gymtracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.gymtracker.gymtracker to javafx.fxml;
    exports com.gymtracker.gymtracker;
    opens model to javafx.base;
}