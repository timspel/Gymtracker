package com.gymtracker.gymtracker;

import javafx.scene.input.MouseEvent;
import model.Exercise;
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
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

public class ExerciseController {
   @FXML
   private ListView<Exercise> exercisesList = new ListView<>();
   @FXML
   private Label infoPanelName;
   @FXML
   private TextArea infoPanelDescription;
   @FXML
   private ImageView infoPanelImage;
   @FXML
   private ChoiceBox muscleGroupSorter = new ChoiceBox();
   private ObservableList<Exercise> exercises = FXCollections.observableArrayList();
   private Comparator listSort = Comparator.comparing(Exercise::getMuscleGroup);

   public void initialize() {
      populateExercises();
      exercisesList.setItems(exercises);
      Collections.sort(exercises, listSort);
      populateMuscleGroups(muscleGroupSorter);
      populateInfoPanel(exercises.get(0)); //To immediately choose load in the first exercise so that it's viewed when you open the tab
      muscleGroupSorter.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> sortExercises(newValue.toString())); //Adds listener for choosing option in muscleGroupSorter
   }

   public void populateExercises(){
      Connection con = null;
      PreparedStatement stmt = null;
      ResultSet result = null;

      try{
         con = Database.getDatabase();
         con.setAutoCommit(false);

         String sql = ("SELECT exercise_id, exercise_name, exercise_description, exercise_picture, workout_type.workout_type_name FROM exercise, workout_type WHERE exercise.workout_type_id = workout_type.workout_type_id");
         stmt = con.prepareStatement(sql);
         result = stmt.executeQuery();

         while(result.next()){
            int id = result.getInt("exercise_id");
            String name = result.getString("exercise_name");
            String desc = result.getString("exercise_description");
            String pic = result.getString("exercise_picture");
            String muscleGroup = result.getString("workout_type_name");

            Exercise exercise = new Exercise(id, name, desc, new Image(pic), muscleGroup);
            exercises.add(exercise);
         }
         stmt.close();
         con.commit();
         con.close();

      }catch (Exception e){
         throw new RuntimeException(e);
      }
      /*Connection con = null;

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

         String sql1 = ("SELECT COUNT(*) AS totalExercises FROM exercise");
         stmt = con.prepareStatement(sql1);
         ResultSet test = stmt.executeQuery();
         int testInt = -1;

         if(test.next()){
            testInt = test.getInt("totalExercises");
         };

         String sql2 = ("SELECT ");
         for(int i = 0; i < testInt; i++){
            //stmt = con
         }

         stmt.close();
         con.commit();
         con.close();

      } catch (Exception e) {
         throw new RuntimeException(e);
      }*/
   }

   public void populateMuscleGroups(ChoiceBox choiceBox){
      Connection con = null;
      PreparedStatement stmt = null;
      ResultSet result = null;

      try{
         con = Database.getDatabase();
         con.setAutoCommit(false);

         String sql = ("SELECT workout_type_name FROM workout_type");
         stmt = con.prepareStatement(sql);
         result = stmt.executeQuery();
         while(result.next()){
            String workoutType = result.getString("workout_type_name");
            choiceBox.getItems().add(workoutType);
         }
         stmt.close();
         con.commit();
         con.close();
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   public void readInNewExercise(Exercise exercise){
      exercises.add(exercise);
      Collections.sort(exercises, listSort);
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

      /*Exercise selectedExercise;
      Connection con = null;
      PreparedStatement stmt = null;
      ResultSet res = null;

      if(exercisesList.getSelectionModel().getSelectedItems().get(0) != null){
         selectedExercise = exercisesList.getSelectionModel().getSelectedItems().get(0);
         //System.out.println(selectedExercise);

         try{
            con = Database.getDatabase();
            con.setAutoCommit(false);
            String sql = ("SELECT exercise_description, exercise_picture FROM exercise WHERE exercise_name = ?");
            stmt = con.prepareStatement(sql);
            //stmt.setString(1, selectedExercise);
            res = stmt.executeQuery();

            if(res.next()){
               String description = res.getString("exercise_description");
               String pictureURL = res.getString("exercise_picture");
               System.out.println(description);
               System.out.println(pictureURL);
            }

         }catch (Exception e) {
            throw new RuntimeException(e);
         }
      }*/
   }

   public void populateInfoPanel(Exercise selectedExercise) {
      infoPanelName.setText(selectedExercise.getName());
      infoPanelDescription.setText(selectedExercise.getDescription());
      infoPanelImage.setImage(selectedExercise.getPicture());
   }

   public void openAddExerciseWindow(){
      try{
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddExerciseWindow.fxml"));
         Scene scene = new Scene(fxmlLoader.load());
         AddExerciseController aec = fxmlLoader.getController();
         aec.initializeWindow(this);
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
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete exercise?");
      alert.setHeaderText(null);
      alert.setContentText("Are you sure you want to delete this exercise?");

      Optional<ButtonType> buttonResult = alert.showAndWait();
      if (buttonResult.get() == ButtonType.OK){
         Exercise exerciseToRemove = getExercise();
         exercises.remove(exerciseToRemove);

         Connection con = null;
         PreparedStatement stmt = null;
         try {
            con = Database.getDatabase();
            con.setAutoCommit(false);

            String sql = ("DELETE FROM exercise WHERE exercise_id = ?");
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, exerciseToRemove.getId());
            stmt.executeUpdate();

            stmt.close();
            con.commit();
            con.close();
         } catch (SQLException e) {
            throw new RuntimeException(e);
         }
      } else {
         System.out.println("Closed or canceled");
      }
   }

   public void sortExercises(String muscleGroupToSortBy){
      ObservableList<Exercise> sortedMuscleGroup = FXCollections.observableArrayList();

      for (Exercise exercise : exercises) {
         if (exercise.getMuscleGroup().equals(muscleGroupToSortBy)) {
            sortedMuscleGroup.add(exercise);
         }
      }
      exercisesList.setItems(sortedMuscleGroup);
   }

   public void resetSort(){
      exercisesList.setItems(exercises);
   }
}
