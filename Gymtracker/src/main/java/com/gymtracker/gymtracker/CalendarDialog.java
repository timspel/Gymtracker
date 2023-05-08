package com.gymtracker.gymtracker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CalendarDialog {


    @FXML
    private Text selectedWorkoutName;

    static Dialog<ButtonType> loadDialog(String workoutName) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(CalendarDialog.class.getResource("CalendarDialog.fxml"));
            Parent root = loader.load();

            // Get the controller for the FXML file
            CalendarDialog controller = loader.getController();

            // Set the selected workout name
            controller.setSelectedWorkoutName(workoutName);

            // Create a new scene with the root node and show the dialog
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setSelectedWorkoutName(String workoutName) {
        selectedWorkoutName.setText(workoutName);
    }



}
