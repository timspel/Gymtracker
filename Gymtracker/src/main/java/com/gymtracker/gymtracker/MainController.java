package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

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

   private Parent workoutPane;
   private Parent calendarPane;
   private Parent profilePane;
   private AnchorPane scrollContent;
   public MainController(){ //Loads in the other frames
      try {
         workoutPane = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
         calendarPane = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
         scrollContent = FXMLLoader.load(getClass().getResource("WelcomePane.fxml"));
         profilePane = FXMLLoader.load(getClass().getResource("ProfilePane.fxml"));
      }
      catch (IOException ioe){ioe.printStackTrace();}
}

   public void buttonPressed(ActionEvent event){ //Handles button presses
      if(event.getSource() == workoutsButton){
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
   }

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      scrollPane.setContent(scrollContent);
   }
}
