package com.gymtracker.gymtracker.controller;

import com.gymtracker.gymtracker.HelloApplication;
import com.gymtracker.gymtracker.model.Exercise;
import com.gymtracker.gymtracker.model.MuscleGroup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainController {
   //Instance variables for ExercisePanel
   @FXML
   private ListView<Exercise> exercisesList = new ListView<>();
   @FXML
   private Label infoPanelName;
   @FXML
   private Label infoPanelDescription;
   @FXML
   private ImageView infoPanelImage;
   private ObservableList<Exercise> exercises = FXCollections.observableArrayList();

   //Instance variables for AddWindow
   @FXML
   private TextField addNameField;
   @FXML
   private ImageView addImageField;
   @FXML
   private TextField imageSourceField;
   @FXML
   private Button chooseImageButton;
   @FXML
   private ChoiceBox addMuscleGroups = new ChoiceBox();
   @FXML
   private TextArea addDescriptionField;

   //Methods for ExercisePanel
   public void initialize() {
      populateExercisesList();
      populateMuscleGroups();
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
         FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AddWindow.fxml"));
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

   //Methods for AddWindow
   public void populateMuscleGroups(){
      for(MuscleGroup mg : MuscleGroup.values()){
         addMuscleGroups.getItems().add(mg);
      }
   }

   public void chooseImage(){
      Stage stage = new Stage();
      FileChooser fc = new FileChooser();
      FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
      FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
      fc.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

      File selectedFile = fc.showOpenDialog(stage);
      imageSourceField.setText(selectedFile.toURI().toString());
      Image exerciseImage = new Image(selectedFile.toURI().toString());
      addImageField.setImage(exerciseImage);
   }

   public void AddNewExercise(){
      Exercise newExercise;
      String name = addNameField.getText();
      String description = addDescriptionField.getText();
      Image image = addImageField.getImage();
      MuscleGroup muscleGroup = (MuscleGroup) addMuscleGroups.getSelectionModel().getSelectedItem();

      newExercise = new Exercise((exercises.size() + 1 + 10),name, description, image, muscleGroup);
      System.out.println(newExercise);
      exercises.add(newExercise);
      exercisesList.setItems(exercises);
   }
}
