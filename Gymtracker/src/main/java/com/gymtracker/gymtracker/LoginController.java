package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
   private Stage stage;
   private Scene scene;
   @FXML
   private Button loginButton;
   @FXML
   private Button registerButton;
   @FXML
   private Button regUserButton;
   @FXML
   private Button cancelButton;
   @FXML
   private AnchorPane registerPane;
   public void login(ActionEvent e) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource("MainFrame.fxml"));
      stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene (scene);
      stage.setX(0);
      stage.setY(0);
      stage.show();
   }

   public void buttonClicked(ActionEvent event){
      if(event.getSource() == loginButton){
         System.out.println("Funkar!!!!");
      }
      if(event.getSource() == registerButton){
         registerPane.setVisible(true);
      }
      if(event.getSource() == regUserButton){
         registerPane.setVisible(false);
      }
      if(event.getSource() == cancelButton){
         registerPane.setVisible(false);
      }
   }

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      registerPane.setVisible(false);
   }
}
