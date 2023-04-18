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

   private Parent workoutScrollPane;

   private Parent calendarPane;

   private AnchorPane scrollContent;
   public MainController(){ //Loads in the other frames
      try {
         workoutScrollPane = FXMLLoader.load(getClass().getResource("WorkoutScrollPane.fxml"));
         calendarPane = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
         scrollContent = FXMLLoader.load(getClass().getResource("WelcomePane.fxml"));
      }
      catch (IOException ioe){ioe.printStackTrace();}
}

   public void buttonPressed(ActionEvent event){ //Handles button presses
      if(event.getSource() == workoutsButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(workoutScrollPane);
      }
      if(event.getSource() == calendarButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(calendarPane);
      }
      if(event.getSource() == profileButton){

      }
   }

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      scrollPane.setContent(scrollContent);
   }
}