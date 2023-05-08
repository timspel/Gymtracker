package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable{
   @FXML
   private Button workoutsButton;
   @FXML
   private Button calendarButton;
   @FXML
   private StackPane stackPane;
   @FXML
   private Label gymtrackerLabel;
   @FXML
   private ScrollPane scrollPane;
   @FXML
   private Button profileButton;
   @FXML
   private Button friendsButton;
   @FXML
   private Button exercisesButton;
   @FXML
   private Button homeButton;
   @FXML
   private Button logoutButton;
   private Parent workoutPane;
   private Parent calendarPane;
   private Parent profilePane;
   private Parent friendlistPane;
   private Parent exercisePane;
   private AnchorPane scrollContent;
   public MainController(){ //Loads in the other frames
      try {
         workoutPane = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
         calendarPane = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
         scrollContent = FXMLLoader.load(getClass().getResource("WelcomePane.fxml"));
         profilePane = new ProfileController(this).getParent();
         friendlistPane = FXMLLoader.load(getClass().getResource("FriendListPanel.fxml"));
         exercisePane = FXMLLoader.load(getClass().getResource("ExercisesPanel.fxml"));
      }
      catch (IOException ioe){ioe.printStackTrace();}
}

   public void buttonPressed(ActionEvent event) throws IOException{ //Handles button presses
      if(event.getSource() == workoutsButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(scrollPane);
         scrollPane.setContent(workoutPane);
      }
      if(event.getSource() == calendarButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(calendarPane);
      }
      if(event.getSource() == profileButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(profilePane);
      }
      if(event.getSource() == friendsButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(friendlistPane);
      }
      if(event.getSource() == exercisesButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(exercisePane);
      }
      if(event.getSource() == homeButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(scrollPane);
         scrollPane.setContent(scrollContent);
      }
      if(event.getSource() == logoutButton){
         Parent root = FXMLLoader.load(getClass().getResource("LoginPanel.fxml"));
         Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
         Scene scene = new Scene(root);
         stage.setScene (scene);
         stage.setX(0);
         stage.setY(0);
         stage.show();
      }
   }
   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      scrollPane.setContent(scrollContent);
   }
}
