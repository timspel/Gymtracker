package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerWorkouts implements Initializable{
    @FXML
    private Button saveWorkoutButton;
    @FXML
    private Label savedTemplatesLabel;
    @FXML
    private ChoiceBox<ExerciseEnum> exercisesChoiceBox;
    @FXML
    private Label newWorkoutLabel;
    @FXML
    private Label exerciseLabel;
    @FXML
    private Button addExerciseButton;
    @FXML
    private DatePicker workoutDatePicker;
    @FXML
    private TextField workoutNameTextField;
    @FXML
    private Label categoryLabel;
    @FXML
    private ChoiceBox<Category> categoriesChoiceBox;
    @FXML
    private Label exercisesLabel;
    @FXML
    private Button removeExerciseButton;
    @FXML
    private TableView exercisesTable;
    @FXML
    private TableColumn exerciseColumn;
    @FXML
    private Spinner<Integer> repetitionsSpinner;
    @FXML
    private Label repetitionsLabel;
    @FXML
    private Button addSetButton;
    @FXML
    private TableView setsTable;
    @FXML
    private TableColumn setColumn;
    @FXML
    private Button saveAsTemplateButton;
    @FXML
    private TableView templatesTable;
    @FXML
    private TableColumn templateColumn;
    @FXML
    private Button editExerciseButton;
    @FXML
    private Button removeTemplateButton;
    @FXML
    private Button editTemplateButton;
    @FXML
    private Button loadTemplateButton;
    @FXML
    private Button editSetButton;
    @FXML
    private Button removeSetButton;
    @FXML
    private Label weightLabel;
    @FXML
    private Spinner<Integer> weightSpinner;
    private Workout workout;
    private Set set;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUp();
    }

    @FXML
    public void buttonPressed(ActionEvent e){
        if(e.getSource() == saveWorkoutButton){
            saveWorkout();
        }
        if(e.getSource() == removeExerciseButton){

        }
        if(e.getSource() == editExerciseButton){

        }
        if(e.getSource() == saveAsTemplateButton){

        }
        if(e.getSource() == addSetButton){
            addSet();
        }
        if(e.getSource() == editSetButton){

        }
        if(e.getSource() == removeSetButton){

        }
        if(e.getSource() == addExerciseButton){
            addExercise();
        }
        if(e.getSource() == removeTemplateButton){

        }
        if(e.getSource() == editTemplateButton){

        }
        if(e.getSource() == loadTemplateButton){

        }
    }

    public void setUp(){
        categoriesChoiceBox.getItems().setAll(Category.values());
        exercisesChoiceBox.getItems().setAll(ExerciseEnum.values());
    }

    public void saveWorkout(){

    }

    public void addSet(){
        set = new Set(repetitionsSpinner.getValue(), weightSpinner.getValue());
    }

    public void addExercise(){
        
    }
}
