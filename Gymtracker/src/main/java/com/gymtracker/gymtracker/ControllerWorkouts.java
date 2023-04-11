package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
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
    private Set set;
    @FXML
    private AnchorPane scrollAnchorPane;
    @FXML
    private Parent workoutPane;
    @FXML
    private Parent workoutScrollPane;
    private Scene scene;
    private Stage stage;

    public ControllerWorkouts() throws IOException {
        workoutPane = FXMLLoader.load(getClass().getResource("WorkoutPane.fxml"));
        scene = new Scene(workoutPane);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUp();
        stage.setScene(scene);
        stage.show();
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
