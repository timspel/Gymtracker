package com.gymtracker.gymtracker;

import com.gymtracker.gymtracker.model.Exercise;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;

import java.util.LinkedList;

public class ExercisesController{
   @FXML
   private ListView viewList;

   @FXML
   private Button editButton;

   private ObservableList<Exercise> exercisesList = FXCollections.observableArrayList();

   public void initialize() {
      populateExercisesList();
      viewList.getItems().setAll(exercisesList);
   }

   public void populateExercisesList(){
      for(int i = 0; i < 5; i++){
         exercisesList.add(new Exercise("Test " + 1, new Image("icon.png")));
      }
   }



   public void removeExercise(ActionEvent event){
      System.out.println("Run method");
      exercisesList.remove(0);
   }
}
