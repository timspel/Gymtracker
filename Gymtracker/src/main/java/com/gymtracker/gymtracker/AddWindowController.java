package com.gymtracker.gymtracker;

import model.MuscleGroup;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddWindowController {
    @FXML
    private TextField addNameField;
    @FXML
    private TextField testtest;
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

    public void initialize() {
        populateMuscleGroups();
    }

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

    /*public void AddNewExercise(){
        Exercise newExercise;
        String name = addNameField.getText();
        String description = addDescriptionField.getText();
        Image image = addImageField.getImage();
        MuscleGroup muscleGroup = (MuscleGroup) addMuscleGroups.getSelectionModel().getSelectedItem();

        newExercise = new Exercise((exercises.size() + 1 + 10),name, description, image, muscleGroup);
        System.out.println(newExercise);
        exercises.add(newExercise);
        exercisesList.setItems(exercises);
    }*/
}
