package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ProfileController {
   @FXML
   private Button editButton;
   @FXML
   private TextArea goalsText;
   @FXML
   private Button changeButton;

   private Parent profilePane;
   private MainController mainController;

   public ProfileController(MainController mainController) throws IOException {
      loadFXML();

      goalsText.setEditable(false);
      editButton.setOnAction(l -> buttonHandler(l));
      changeButton.setOnAction(l -> buttonHandler(l));
   }

   public Parent getParent(){
      return profilePane;
   }

   public void buttonHandler(ActionEvent event){
      if(event.getSource() == editButton){
         if(editButton.getText() != "Save") {
            editButton.setText("Save");
            goalsText.setEditable(true);
         }
         else {
            goalsText.setEditable(false);
            editButton.setText("Edit");
         }
      }
   }
   private void loadFXML() throws IOException{ //Loads FXML file and assigns it to parent variable.
      this.mainController = mainController;
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfilePane.fxml"));
      loader.setController(this);
      profilePane = (Parent) loader.load();
   }
}
