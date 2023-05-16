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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable{
   //<editor-fold desc="FXML Variables">
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
   //</editor-fold>

   private Parent workoutPane;
   private Parent calendarPane;
   private Parent profilePane;
   private Parent friendlistPane;
   private Parent exercisePane;
   private Parent scrollContent;
   ArrayList<Parent> parents = new ArrayList<>();
   public MainController(){
      parents.add(0,workoutPane);
      parents.add(1, calendarPane);
      parents.add(2, profilePane);
      parents.add(3, friendlistPane);
      parents.add(4, exercisePane);
      try {
         scrollContent = FXMLLoader.load(getClass().getResource("WelcomePane.fxml"));
      }
      catch (IOException ioe){ioe.printStackTrace();}

      for(int i = 0; i < parents.size(); i++){ //New thread handles each FXML interface.
         new LoadingThread(this, parents.get(i), i).start();
      }
}

   public void buttonPressed(ActionEvent event) throws IOException{ //Handles button presses
      if(event.getSource() == workoutsButton){
         stackPane.getChildren().removeAll();
         stackPane.getChildren().setAll(scrollPane);
         scrollPane.setContent(workoutPane);
         Singleton.getInstance().setWorkoutScroll(scrollPane);
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

   private class LoadingThread extends Thread { //Inner Thread class that loads in all FXML files.
      private MainController mainController;
      private Parent parent;
      private int index;
      public LoadingThread(MainController mainController, Parent parent, int index){
         this.mainController = mainController;
         this.parent = parent;
         this.index = index;
      }
      @Override
      public void run() {
         try {
            if(index == 0){ //Ska byta denna till switch sats jag vet //Alex
               System.out.println("Workouts!");
               workoutPane = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
            }
            if(index == 1) {
               System.out.println("Calendar");
               calendarPane = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
            }
            if(index == 2) {
               System.out.println("Profile");
               profilePane = new ProfileController(mainController).getParent();
            }
            if(index == 3) {
               System.out.println("Friends");
               friendlistPane = FXMLLoader.load(getClass().getResource("FriendListPanel.fxml"));
            }
            if(index == 4) {
               System.out.println("Exercise");
               exercisePane = FXMLLoader.load(getClass().getResource("ExercisesPanel.fxml"));
               System.out.println("Done...");
            }
         }
         catch (IOException io){
            io.printStackTrace();
         }
      }
   }
}
