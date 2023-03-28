package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TempController {
   private Stage stage;
   private Scene scene;

   public void login(ActionEvent e) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource("MainFrame.fxml"));
      stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene (scene);
      stage.show();
   }
}
