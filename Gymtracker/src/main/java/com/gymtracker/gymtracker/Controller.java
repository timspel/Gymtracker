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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
   @FXML
   private AnchorPane welcomePane;
   @FXML
   private Button workoutsButton;
   @FXML
   private Button calendarButton;
   @FXML
   private StackPane stackPane;
   @FXML
   private Label gymtrackerLabel;

   private Parent workoutPane;

   private Parent calendarPane;
   public Controller(){ //Loads in the other frames
      try {
         workoutPane = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
         calendarPane = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
      }
      catch (IOException ioe){ioe.printStackTrace();}
}
   public void buttonPressed(ActionEvent event){ //Handles button presses
      if(event.getSource() == workoutsButton){
         //Parent root = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(workoutPane);
      }
      if(event.getSource() == calendarButton){
         //Parent root = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(calendarPane);
      }
   }
}
