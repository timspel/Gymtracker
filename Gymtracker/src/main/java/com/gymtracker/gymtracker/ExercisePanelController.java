package com.gymtracker.gymtracker;

import com.gymtracker.gymtracker.ExercisePanel;
import model.Exercise;
import model.MuscleGroup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class ExercisePanelController {
   @FXML
   private ListView<Exercise> exercisesList = new ListView<>();
   @FXML
   private Label infoPanelName;
   @FXML
   private Label infoPanelDescription;
   @FXML
   private ImageView infoPanelImage;
   private ObservableList<Exercise> exercises = FXCollections.observableArrayList();

   public void initialize() {
      populateExercisesList();
      exercisesList.setItems(exercises);
   }

   public void populateExercisesList(){
      for(int i = 0; i < 5; i++){
         exercises.add(new Exercise((i + 10), "Test " + (i + 1),"Description goes here", new Image("icon.png"), MuscleGroup.Arms));
      }
   }

   public Exercise getExercise(){
      int exerciseID;
      if(exercisesList.getSelectionModel().getSelectedItems().get(0) != null){
         exerciseID = exercisesList.getSelectionModel().getSelectedItems().get(0).getId();
      }else{
         return null;
      }
      Exercise selectedExercise;
      for(int i = 0; i < exercises.size(); i++){
         if(exercises.get(i).getId() == exerciseID){
            selectedExercise = exercises.get(i);
            populateInfoPanel(selectedExercise);
            return selectedExercise;
         }
      }
      System.out.println("Can find matching ID for exercise. Returning null");
      return null;
   }

   private void populateInfoPanel(Exercise selectedExercise) {
      infoPanelName.setText(selectedExercise.getName());
      infoPanelDescription.setText(selectedExercise.getDescription());
      infoPanelImage.setImage(selectedExercise.getImage());
   }

   public void openAddWindow(){
      try{
         FXMLLoader fxmlLoader = new FXMLLoader(ExercisePanel.class.getResource("AddWindow.fxml"));
         Scene scene = new Scene(fxmlLoader.load());
         Stage stage = new Stage();
         stage.setTitle("Add Exercise");
         stage.setScene(scene);
         stage.setResizable(false);
         stage.show();
      }catch (IOException e){
         System.out.println("Problem occurred opening Add window: " + e);
      }
   }

   public void removeExercise(){
      int exerciseToRemove = getExercise().getId();

      for(int i = 0; i < exercises.size(); i++){
         if(exercises.get(i).getId() == exerciseToRemove){
            exercises.remove(i);
            break;
         }
      }
   }
}
