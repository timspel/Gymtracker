package com.gymtracker.gymtracker;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ExercisePanelController {
   @FXML
   private ListView<String> exercisesList = new ListView<>();
   @FXML
   private Label infoPanelName;
   @FXML
   private Label infoPanelDescription;
   @FXML
   private ImageView infoPanelImage;
   private ObservableList<String> exercises = FXCollections.observableArrayList();

   public void initialize() {
      populateExercises();
      exercisesList.setItems(exercises);
   }

   /*public void populateExercisesList(){
      for(int i = 0; i < 5; i++){
         exercises.add(new Exercise((i + 10), "Test " + (i + 1),"Description goes here", new Image("icon.png"), MuscleGroup.Arms));
      }
   }*/

   public void populateExercises(){
      Connection con = null;

      PreparedStatement stmt1 = null;
      PreparedStatement stmt2 = null;
      ResultSet res1 = null;
      ResultSet res2 = null;
      int totalExercises = -1;

      try {
         con = Database.getDatabase();
         con.setAutoCommit(false);

         String sql2 = ("SELECT COUNT(*) AS totalExercises FROM exercise");
         stmt2 = con.prepareStatement(sql2);
         res2 = stmt2.executeQuery();

         if(res2.next()){
            totalExercises = res2.getInt("totalExercises");
            System.out.println(totalExercises);
         }

         if(totalExercises != -1){
            for(int i = 1; i < totalExercises + 1; i++){
               String sql1 = ("SELECT exercise_name AS exercise FROM exercise WHERE exercise_id = " + i);
               stmt1 = con.prepareStatement(sql1);
               res1 = stmt1.executeQuery();
               if(res1.next()){
                  String exerciseName = res1.getString("exercise");
                  System.out.println(exerciseName);
                  exercises.add(exerciseName);
               }
            }
         }

         /*String sql1 = ("SELECT COUNT(*) AS totalExercises FROM exercise");
         stmt = con.prepareStatement(sql1);
         ResultSet test = stmt.executeQuery();
         int testInt = -1;

         if(test.next()){
            testInt = test.getInt("totalExercises");
         };

         String sql2 = ("SELECT ");
         for(int i = 0; i < testInt; i++){
            //stmt = con
         }*/
         /*stmt.close();
         con.commit();
         con.close();*/



      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /*public Exercise getExercise(){
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
   }*/

   private void populateInfoPanel(Exercise selectedExercise) {
      infoPanelName.setText(selectedExercise.getName());
      infoPanelDescription.setText(selectedExercise.getDescription());
      infoPanelImage.setImage(selectedExercise.getImage());
   }

   public void openAddExerciseWindow(){
      try{
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddExerciseWindow.fxml"));
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

   /*public void removeExercise(){
      int exerciseToRemove = getExercise().getId();

      for(int i = 0; i < exercises.size(); i++){
         if(exercises.get(i).getId() == exerciseToRemove){
            exercises.remove(i);
            break;
         }
      }
   }*/
}
