package com.gymtracker.gymtracker;

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
import java.util.ArrayList;
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
   private Comparator<Exercise> listSort = Comparator.comparing(Exercise::getMuscleGroup);
   private ArrayList<exerciseCreator> threads = new ArrayList<>();

   public void initialize() {
      populateExercises();
      exercisesList.setItems(exercises);
      exercises.sort(listSort);
      populateMuscleGroups(muscleGroupSorter);
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

            exerciseCreator exerciseCreator = new exerciseCreator(id, name, desc, pic, muscleGroup);
            exerciseCreator.start();
            System.out.println("thread started");
            threads.add(exerciseCreator);
         }
         stmt.close();
         con.commit();
         con.close();
      }catch (Exception e){
         throw new RuntimeException(e);
      }

      boolean stillAlive = true;
      while(stillAlive){
         stillAlive = false;
         for(Thread t : threads){
            if(t.isAlive()){
               stillAlive = true;
               break;
            }
         }
      }
      populateInfoPanel(exercises.get(0)); //To immediately choose load in the first exercise so that it's viewed when you open the tab
      muscleGroupSorter.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> sortExercises(newValue.toString()));
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
      exercises.sort(listSort);
   }

   public Exercise getExercise(){
      if(exercisesList.getSelectionModel().getSelectedItem() != null){
         int exerciseID;
         exerciseID = exercisesList.getSelectionModel().getSelectedItem().getId();
         Exercise selectedExercise;

         for (Exercise exercise : exercises) {
            if (exercise.getId() == exerciseID) {
               selectedExercise = exercise;
               populateInfoPanel(selectedExercise);
               return selectedExercise;
            }
         }
      }
      return null;
   }

   public void populateInfoPanel(Exercise selectedExercise) {
      infoPanelName.setText(selectedExercise.getName());
      infoPanelDescription.setText(formatDescriptionText(selectedExercise.getDescription()));
      infoPanelImage.setImage(selectedExercise.getImage());
   }

   public String formatDescriptionText(String exerciseDesc){
      String formattedString = "";
      String[] splitString = exerciseDesc.split("\\. ");
      for(String s : splitString){
         formattedString += "- " + s + "\n";
      }
      return formattedString;
   }

   public void removeExercise(){
      Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
      deleteAlert.setTitle("Delete exercise?");
      deleteAlert.setHeaderText(null);
      deleteAlert.setContentText("Are you sure you want to delete this exercise?");
      Optional<ButtonType> buttonResult = deleteAlert.showAndWait();

      if (buttonResult.get() == ButtonType.OK && getExercise() != null){
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

            getExercise();
         } catch (SQLException e) {
            throw new RuntimeException(e);
         }
      } else if(buttonResult.get() == ButtonType.OK && getExercise() == null){
         Alert errorAlert = new Alert(Alert.AlertType.ERROR);
         if(getExercise() == null){
            errorAlert.setTitle("No exercise selected");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("An exercise must be selected before it can be deleted");
            errorAlert.showAndWait();
         }
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

   public void resetExerciseSort(){
      exercisesList.setItems(exercises);
   }

   public void openAddExerciseWindow(){
      try{
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddOrEditExerciseWindow.fxml"));
         Scene scene = new Scene(fxmlLoader.load());
         AddOrEditExerciseController aec = fxmlLoader.getController();
         aec.initializeWindow(this, null);
         Stage stage = new Stage();
         stage.setTitle("Add Exercise");
         stage.setScene(scene);
         stage.setResizable(false);
         stage.getIcons().add(new Image("icon.png"));
         stage.show();
      }catch (IOException e){
         System.out.println("Problem occurred opening Add window: " + e);
      }
   }

   public void openEditExerciseWindow(){
      if(getExercise() != null){
         try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddOrEditExerciseWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            AddOrEditExerciseController aec = fxmlLoader.getController();
            aec.initializeWindow(this, getExercise());
            Stage stage = new Stage();
            stage.setTitle("Edit Exercise");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image("icon.png"));
            stage.show();
         }catch (IOException e){
            System.out.println("Problem occurred opening Add window: " + e);
         }
      }else{
         Alert errorAlert = new Alert(Alert.AlertType.ERROR);
         errorAlert.setTitle("No exercise selected");
         errorAlert.setHeaderText(null);
         errorAlert.setContentText("An exercise must be selected before it can be edited");
         errorAlert.showAndWait();
      }
   }

   private class exerciseCreator extends Thread {
      private int id;
      private String name;
      private String desc;
      private String image;
      private String muscleGroup;

      public exerciseCreator(int id, String name, String desc, String image, String muscleGroup){
         this.id = id;
         this.name = name;
         this.desc = desc;
         this.image = image;
         this.muscleGroup = muscleGroup;
      }

      @Override
      public void run() {
         exercises.add(new Exercise(id, name, desc, new Image(image), muscleGroup));
      }
   }

}
