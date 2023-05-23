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

/**
 * @author Alexander Fleming
 *
 * Class that handles the Main Page which includes the Navigation bar on the left.
 */
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

   /**
    * Constructor for the class.
    * Initializes the load-in sequence for all the FXML files.
    */
   public MainController(){
      parents.add(0,workoutPane);
      parents.add(1, calendarPane);
      parents.add(2, profilePane);
      parents.add(3, friendlistPane);
      parents.add(4, exercisePane);
      try {
         scrollContent = FXMLLoader.load(getClass().getResource("WelcomePane.fxml")); //Loads in the welcome page first.
      }
      catch (IOException ioe){ioe.printStackTrace();}

      for(int i = 0; i < parents.size(); i++){ //New thread handles each FXML interface.
         new LoadingThread(this, parents.get(i), i).start();
      }
   }

   /**
    * Handles all button pressed events from the GUI.
    * Checks the source from the event to determine which button was pressed.
    *
    * @param event - The Button Pressed event.
    * @throws IOException
    */
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

   /**
    * The FXML Initialize method. Acts like a constructor but for FXML injection components.
    * @param url
    * @param resourceBundle
    */
   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      scrollPane.setContent(scrollContent);
   }

   /**
    * Inner Thread class
    * Handles all FXML Files in separate threads.
    */
   private class LoadingThread extends Thread { //Inner Thread class that loads in all FXML files.
      private MainController mainController;
      private Parent parent;
      private int index;

      /**
       * Constructor for each thread. Assigns correct values and keeps track of parent class.
       * @param mainController - Parent Class.
       * @param parent - FXML File
       * @param index - Index to load parent from.
       */
      public LoadingThread(MainController mainController, Parent parent, int index){
         this.mainController = mainController;
         this.parent = parent;
         this.index = index;
      }
      @Override
      public void run() {
         try {
            if(index == 0){ //Ska byta denna till switch sats jag vet //Alex
               workoutPane = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
            }
            if(index == 1) {
               calendarPane = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
            }
            if(index == 2) {
               profilePane = new ProfileController(mainController).getParent();
            }
            if(index == 3) {
               friendlistPane = FXMLLoader.load(getClass().getResource("FriendListPanel.fxml"));
            }
            if(index == 4) {
               exercisePane = FXMLLoader.load(getClass().getResource("ExercisesPanel.fxml"));
               System.out.println("Done...");
            } //Hela denna ska göras om.
         }
         catch (IOException io){
            io.printStackTrace();
         }
      }
   }
}
