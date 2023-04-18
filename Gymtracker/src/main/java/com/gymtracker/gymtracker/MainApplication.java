package com.gymtracker.gymtracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
<<<<<<< HEAD:Gymtracker/src/main/java/com/gymtracker/gymtracker/MainApplication.java
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("LoginPanel.fxml"));
=======
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("WorkoutScrollPane.fxml"));
>>>>>>> Samuel:Gymtracker/src/main/java/com/gymtracker/gymtracker/HelloApplication.java
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gymtracker");
        stage.getIcons().add(new Image("icon.png"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}