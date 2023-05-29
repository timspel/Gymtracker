package com.gymtracker.gymtracker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is responsible for managing the workout scroll pane and initializing its content.
 * @author Samuel Carlsson
 */
public class ControllerWorkoutScrollPane implements Initializable {
    @FXML
    private ScrollPane workoutScrollPane;
    private Parent workoutPane;

    /**
     * Constructs a new instance of ControllerWorkoutScrollPane.
     * @throws IOException if an error occurs while loading the workout pane from the FXML file
     */
    public ControllerWorkoutScrollPane() throws IOException {
        workoutPane = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
    }

    /**
     * Initializes the controller and sets the content of the workout scroll pane to the loaded workout pane.
     * @param url the location used to resolve relative paths
     * @param resourceBundle the resources specific to the controller's bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        workoutScrollPane.setContent(workoutPane);
    }
}
