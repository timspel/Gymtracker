package com.gymtracker.gymtracker;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
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
import model.ExerciseRecord;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.Date;

/**
 * A class that handles all exercises and their information. A ListView will show all created exercises along with
 * their name, muscle group and a description. In addition, clicking on an Exercise will also show you your personal
 * best sets, reps and weight have these been added. Existing records can be updated and should you update an empty
 * record it will create a new one. You can add a new Exercise or edit an existing one with the respective buttons,
 * which opens a new window managed by the AddOrEditExerciseController. Exercises can be sorted by muscle group
 */
public class ExerciseController {
   @FXML
   private ListView<Exercise> exercisesList = new ListView<>();
   @FXML
   private Label infoPanelMuscleGroup;
   @FXML
   private Label infoPanelName;
   @FXML
   private TextArea infoPanelDescription;
   @FXML
   private ImageView infoPanelImage;
   @FXML
   private ChoiceBox<String> muscleGroupFilter = new ChoiceBox<>();
   @FXML
   private TextField currentSetsField;
   @FXML
   private TextField currentRepsField;
   @FXML
   private TextField currentWeightField;
   @FXML
   private Button editRecordButton;
   @FXML
   private Button cancelEditButton;
   @FXML
   private Button updateEditButton;
   @FXML
   private Label exerciseImplementMessage;

   private ObservableList<Exercise> exercises = FXCollections.observableArrayList();
   private Comparator<Exercise> listSort = Comparator.comparing(Exercise::getMuscleGroup);
   private ArrayList<ExerciseCreator> threads = new ArrayList<>();
   private ArrayList<ExerciseRecord> exerciseRecords = new ArrayList<>();

   /**
    * Does setup like sorting lists and populates ChoiceBoxes as well as read in information from database.
    * Exception are to final lines, where the first one adds a listener to the filter, enabling the functionality to
    * filter exercises based on a muscle group. The second line simply displays the first Exercise as if it had been
    * clicked to not show an empty space
    * Please refer to the other methods themselves for a more in-depth description
    */
   public void initialize(){
      populateExerciseRecords();
      populateExercises();
      exercisesList.setItems(exercises);
      exercises.sort(listSort);
      populateMuscleGroups(muscleGroupFilter);
      muscleGroupFilter.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> sortExercises(newValue));
      populateInfoPanel(exercises.get(0));
   }

   /**
    * Reads in exercises from database and creates an instance of ExerciseCreator which will create Exercise-instances
    * on threads, then add these threads to an ArrayList. By the end it delays the program so that all exercises can
    * be added before it is used elsewhere
    */
   public void populateExercises(){
      PreparedStatement stmt;
      ResultSet result;

      try(Connection con = Database.getDatabase()){
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

            ExerciseCreator exerciseCreator = new ExerciseCreator(id, name, desc, pic, muscleGroup);
            exerciseCreator.start();
            threads.add(exerciseCreator);
         }
         stmt.close();
         con.commit();
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
   }

   /**
    * Reads in muscle groups from database to then initialize a ChoiceBox with muscle groups. Also adds the
    * "Show all exercises" alternative to remove any filtering after one has been applied in the program
    * @param choiceBox The choice box to be initialized
    */
   public void populateMuscleGroups(ChoiceBox choiceBox){
      choiceBox.getItems().add("Show all exercises");

      PreparedStatement stmt;
      ResultSet result;

      try(Connection con = Database.getDatabase()){
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
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * To read in Exercise records from database, then save this as an ArrayList. The information is then displayed when
    * a specific Exercise is clicked
    */
   public void populateExerciseRecords(){
      PreparedStatement stmt;
      ResultSet result;

      try(Connection con = Database.getDatabase()){
         con.setAutoCommit(false);

         String sql = ("SELECT exercise_id, sets, reps, weight FROM exercise_record WHERE user_id = ?");
         stmt = con.prepareStatement(sql);
         stmt.setInt(1, Singleton.getInstance().getUserId());
         result = stmt.executeQuery();

         while(result.next()){
            int exerciseID = result.getInt("exercise_id");
            int sets = result.getInt("sets");
            int reps = result.getInt("reps");
            int weight = result.getInt("weight");
            exerciseRecords.add(new ExerciseRecord(exerciseID, sets, reps, weight));
         }
         stmt.close();
         con.commit();
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * To find the specific Exercise that has been clicked in the ListView and then return in
    * @return The clicked on Exercise, or null if no Exercise was clicked
    */
   public Exercise getExercise(){
      if(exercisesList.getSelectionModel().getSelectedItem() != null){
         int exerciseID;
         exerciseID = exercisesList.getSelectionModel().getSelectedItem().getId();
         Exercise selectedExercise;

         for (Exercise exercise : exercises) {
            if (exercise.getId() == exerciseID) {
               selectedExercise = exercise;
               return selectedExercise;
            }
         }
      }
      return null;
   }

   /**
    * Finds record information matching a specific Exercise id and returns the record
    * @param selectedExercise The Exercise which records are shown
    * @param specifiedRecord An input to specify which value that gets returned
    * @return An Exercise record, depending on which was specified
    */
   public String getExerciseRecordInfo(Exercise selectedExercise, String specifiedRecord){
      for(ExerciseRecord er : exerciseRecords){
         if(selectedExercise.getId() == er.getExerciseID()){
            switch (specifiedRecord){
               case "sets":
                  return String.valueOf(er.getSets());
               case "reps":
                  return String.valueOf(er.getReps());
               case "weight":
                  return String.valueOf(er.getWeight());
            }
         }
      }
      return null;
   }

   /**
    * Populates the information panel with an Exercise's information and records
    * @param selectedExercise The Exercise that was selected to be displayed
    */
   public void populateInfoPanel(Exercise selectedExercise) {
      infoPanelMuscleGroup.setText(selectedExercise.getMuscleGroup());
      infoPanelName.setText(selectedExercise.getName());
      infoPanelDescription.setText(formatDescriptionText(selectedExercise.getDescription()));
      infoPanelImage.setImage(selectedExercise.getImage());
      currentSetsField.setText(getExerciseRecordInfo(selectedExercise, "sets"));
      currentRepsField.setText(getExerciseRecordInfo(selectedExercise, "reps"));
      currentWeightField.setText(getExerciseRecordInfo(selectedExercise, "weight"));
   }

   /**
    * Called whenever an Exercise is clicked in the ListView to show the clicked on Exercise's information in the
    * info panel
    */
   public void viewSelectedExercise(){
      populateInfoPanel(getExercise());
   }

   /**
    * Takes the description of an Exercise and formats it into a more readable form
    * @param exerciseDesc The text that is to be formatted
    * @return The text in its formatted form
    */
   public String formatDescriptionText(String exerciseDesc){
      String formattedString = "";
      String[] splitString = exerciseDesc.split("\\. ");
      for(String s : splitString){
         formattedString += "- " + s + "\n";
      }
      return formattedString;
   }

   /**
    * Allows the sorting of exercises based on which muscle group was selected in the ChoiceBox. Will show all
    * exercises if "Show all exercises" is selected
    * @param muscleGroupToSortBy The muscle group to sort by
    */
   public void sortExercises(String muscleGroupToSortBy){
      if(muscleGroupToSortBy.equals("Show all exercises")){
         exercisesList.setItems(exercises);
      }else{
         ObservableList<Exercise> sortedMuscleGroup = FXCollections.observableArrayList();

         for (Exercise exercise : exercises) {
            if (exercise.getMuscleGroup().equals(muscleGroupToSortBy)) {
               sortedMuscleGroup.add(exercise);
            }
         }
         exercisesList.setItems(sortedMuscleGroup);
      }
   }

   /**
    * Shows the cancel and edit button upon clicking edit and makes fields editable. Does the inverse when clicked
    * again.
    */
   public void changeRecordFieldsVisibility(){
      boolean visible = editRecordButton.visibleProperty().get();
      editRecordButton.setVisible(!visible);
      cancelEditButton.setVisible(visible);
      updateEditButton.setVisible(visible);
      currentSetsField.setEditable(visible);
      currentRepsField.setEditable(visible);
      currentWeightField.setEditable(visible);
   }

   /**
    * Called when edit button is clicked, hiding it to instead show cancel and update button. Gives error message
    * if no Exercise has been clicked prior
    */
   public void editRecords(){
      if(getExercise() != null){
         changeRecordFieldsVisibility();
      }else{
         Alert errorAlert = new Alert(Alert.AlertType.ERROR);
         errorAlert.setTitle("No exercise selected");
         errorAlert.setHeaderText(null);
         errorAlert.setContentText("An exercise must be selected before it can be edited");
         errorAlert.showAndWait();
      }
   }

   /**
    * Called when cancel button is clicked, hiding it and update button to instead show edit button. Also reads in
    * information again to effectively discard unconfirmed changes
    */
   public void cancelEdit(){
      changeRecordFieldsVisibility();
      populateInfoPanel(getExercise());
   }

   /**
    * Checks if Exercise record exists. If it does, updates said record with the new values in the text fields and adds
    * this information to the database. If it does not, creates a new record and adds it to the database.
    */
   public void createOrUpdateRecords(){
      boolean foundRecord = false;
      int exerciseID = getExercise().getId();

      for(ExerciseRecord er : exerciseRecords){
         if(exerciseID == er.getExerciseID()){
            foundRecord = true;

            PreparedStatement stmt;

            try(Connection con = Database.getDatabase()){
               con.setAutoCommit(false);

               long time = System.currentTimeMillis();
               Date date = new Date(time);
               int sets = Integer.parseInt(currentSetsField.getText());
               int reps = Integer.parseInt(currentRepsField.getText());
               int weight = Integer.parseInt(currentWeightField.getText());

               String sql = ("UPDATE exercise_record SET date = ?, sets = ?, reps = ?, weight = ? WHERE user_id = ? AND exercise_id = ?");
               stmt = con.prepareStatement(sql);
               stmt.setDate(1, date);
               stmt.setInt(2, sets);
               stmt.setInt(3, reps);
               stmt.setInt(4, weight);
               stmt.setInt(5, Singleton.getInstance().getUserId());
               stmt.setInt(6, er.getExerciseID());
               stmt.executeUpdate();
               System.out.println("Update successful");
               stmt.close();
               con.commit();

               er.setSets(sets);
               er.setReps(reps);
               er.setWeight(weight);
            } catch (SQLException e) {
               throw new RuntimeException(e);
            }
         }
      }

      if(!foundRecord){
         PreparedStatement stmt;

         try(Connection con = Database.getDatabase()){
            con.setAutoCommit(false);

            long time = System.currentTimeMillis();
            Date date = new Date(time);
            int sets = Integer.parseInt(currentSetsField.getText());
            int reps = Integer.parseInt(currentRepsField.getText());
            int weight = Integer.parseInt(currentWeightField.getText());

            String sql = ("INSERT INTO exercise_record(user_id, exercise_id, date, sets, reps, weight)" +
                    "VALUES (?, ?, ?, ?, ?, ?)");
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, Singleton.getInstance().getUserId());
            stmt.setInt(2, exerciseID);
            stmt.setDate(3, date);
            stmt.setInt(4, sets);
            stmt.setInt(5, reps);
            stmt.setInt(6, weight);
            stmt.executeUpdate();
            stmt.close();
            con.commit();
            exerciseRecords.add(new ExerciseRecord(exerciseID, sets, reps, weight));
         } catch (SQLException e) {
            throw new RuntimeException(e);
         }
      }
      changeRecordFieldsVisibility();
   }

   /**
    * Props the user to confirm if Exercise is to be removed. If OK is pressed the selected Exercise is removed from
    * the ListView and the database. If cancel is pressed the operation is canceled
    */
   public void removeExercise(){
      Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
      deleteAlert.setTitle("Delete exercise?");
      deleteAlert.setHeaderText(null);
      deleteAlert.setContentText("Are you sure you want to delete this exercise?");
      Optional<ButtonType> buttonResult = deleteAlert.showAndWait();

      if (buttonResult.get() == ButtonType.OK && getExercise() != null){
         Exercise exerciseToRemove = getExercise();
         exercises.remove(exerciseToRemove);

         PreparedStatement stmt;
         try(Connection con = Database.getDatabase()){
            con.setAutoCommit(false);

            String sql = ("DELETE FROM exercise WHERE exercise_id = ?");
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, exerciseToRemove.getId());
            stmt.executeUpdate();
            stmt.close();
            con.commit();
            populateInfoPanel(getExercise());
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

   /**
    * Sets up new FXMLLoader to display a new window where an Exercise can be added. Passes instance of
    * ExerciseController to enable add window to update exercises once a new one has been added
    */
   public void openAddExerciseWindow(){
      try{
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddOrEditExerciseWindow.fxml"));
         Scene scene = new Scene(fxmlLoader.load());
         AddOrEditExerciseController addEditController = fxmlLoader.getController();
         addEditController.initializeWindow(this, null);
         Stage stage = new Stage();
         stage.setTitle("Add Exercise");
         stage.setScene(scene);
         stage.setResizable(false);
         stage.getIcons().add(new Image("icon.png"));
         stage.show();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Sets up new FXMLLoader to display a new window where an Exercise can be edited. Passes instance of
    * ExerciseController to enable edit window to update exercises once an Exercise has been edited along with the
    * selected Exercise to be able to display the Exercise's information. Throws error if no Exercise is selected
    * before edit button is pressed
    */
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
            throw new RuntimeException(e);
         }
      }else{
         Alert errorAlert = new Alert(Alert.AlertType.ERROR);
         errorAlert.setTitle("No exercise selected");
         errorAlert.setHeaderText(null);
         errorAlert.setContentText("An exercise must be selected before it can be edited");
         errorAlert.showAndWait();
      }
   }

   /**
    * Shows message that a new exercise has been added or that an exercise has been edited, depending on which is
    * specified. Also creates a Timeline that hides this message after a set amount of seconds
    * @param specifyMessage Specifies if add-message or edit-message is to be displayed
    * @param exerciseName The name of the exercise that was added or edited
    */
   public void showCompletionMessage(String specifyMessage, String exerciseName){
      if(specifyMessage.equals("add")){
         exerciseImplementMessage.setText("Successfully added new exercise!");
         exerciseImplementMessage.setVisible(true);
      }else if(specifyMessage.equals("edit")){
         exerciseImplementMessage.setText("Successfully edited exercise: " + exerciseName + "!");
         exerciseImplementMessage.setVisible(true);
      }

      Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4),e -> {
         exerciseImplementMessage.setVisible(false);
      }));
      timeline.setCycleCount(1);
      timeline.play();
   }

   /**
    * Called after a new Exercise has been added to also add it to the ObservableList and sort this list again.
    * Displays a message saying that a new Exercise has been added
    * @param exercise The exercise that has been added
    */
   public void readInNewExercise(Exercise exercise){
      exercises.add(exercise);
      exercises.sort(listSort);
      showCompletionMessage("add", exercise.getName());
   }

   /**
    * Updates exercise in ObservableList with all the new values from the edit. Sorts list of exercises and displays a
    * message saying which Exercise has been updated
    * @param id Exercise id
    * @param name Exercise name
    * @param description Exercise description
    * @param image Exercise image
    * @param muscleGroup Exercise muscle group
    */
   public void updateExercise(int id, String name, String description, Image image, String muscleGroup){
      for(Exercise exercise : exercises){
         if(exercise.getId() == id){
            exercise.setName(name);
            exercise.setDescription(description);
            exercise.setImage(image);
            exercise.setMuscleGroup(muscleGroup);
            populateInfoPanel(exercise);
            break;
         }
      }
      exercises.sort(listSort);
      showCompletionMessage("edit", name);
   }

   /**
    * Inner class that, via Threads, create Exercises
    */
   private class ExerciseCreator extends Thread {
      private int id;
      private String name;
      private String desc;
      private String image;
      private String muscleGroup;

      public ExerciseCreator(int id, String name, String desc, String image, String muscleGroup){
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
