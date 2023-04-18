package com.gymtracker.gymtracker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerWorkoutScrollPane implements Initializable {
    @FXML
    private ScrollPane workoutScrollPane;
    private Parent workoutPane;

    public ControllerWorkoutScrollPane() throws IOException {
        workoutPane = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        workoutScrollPane.setContent(workoutPane);
    }
}
