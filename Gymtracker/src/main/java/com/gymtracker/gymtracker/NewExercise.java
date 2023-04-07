package com.gymtracker.gymtracker;

import com.gymtracker.gymtracker.model.Exercise;
import com.gymtracker.gymtracker.model.MuscleGroup;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class NewExercise {
    @FXML
    private ChoiceBox<MuscleGroup> muscleGroups;

    public NewExercise(ObservableList<Exercise> exercises){

    }
}
