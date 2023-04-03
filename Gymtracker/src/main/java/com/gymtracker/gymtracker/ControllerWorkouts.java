package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerWorkouts {
    private Button saveWorkoutButton;
    private Label savedTemplatesLabel;
    private ChoiceBox exercisesChoiceBox;
    private Label newWorkoutLabel;
    private Label exerciseLabel;
    private Button addExerciseButton;
    private DatePicker workoutDatePicker;
    private TextField workoutNameTextField;
    private Label categoryLabel;
    private ChoiceBox categoriesChoiceBox;
    private Label exercisesLabel;
    private Button removeExerciseButton;
    private TableView exercisesTable;
    private TableColumn exerciseColumn;
    private Spinner repetitionsSpinner;
    private Label repetitionsLabel;
    private Button addSetButton;
    private TableView setsTable;
    private TableColumn setColumn;
    private Button saveAsTemplateButton;
    private TableView templatesTable;
    private TableColumn templateColumn;
    private Button editExerciseButton;
    private Button removeTemplateButton;
    private Button editTemplateButton;
    private Button loadTemplateButton;
    private Button editSetButton;
    private Button removeSetButton;

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == saveWorkoutButton){
            System.out.println("Hej");
        }
    }
}
