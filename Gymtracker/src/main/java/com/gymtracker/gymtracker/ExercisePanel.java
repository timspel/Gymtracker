package com.gymtracker.gymtracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ExercisePanel extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ExercisePanel.class.getResource("ExercisesPanel.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gymtracker");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}