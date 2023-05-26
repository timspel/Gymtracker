package com.gymtracker.gymtracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Exercise;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddOrEditExerciseController {
    @FXML
    private Label headerLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private TextField nameField;
    @FXML
    private ImageView imagePreview;
    @FXML
    private TextField imageSourceField;
    @FXML
    private ChoiceBox<String> muscleGroups = new ChoiceBox<String>();
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button cancelButton;
    @FXML
    private Label invalidURLMessage;
    @FXML
    private Label invalidMuscleGroup;

    private ExerciseController exerciseController;
    private Exercise exerciseToEdit;
    private URL imageURL;
    private boolean imageChosen = false;

    public void initializeWindow(ExerciseController exerciseController, Exercise exercise){
        this.exerciseController = exerciseController;
        exerciseController.populateMuscleGroups(muscleGroups);
        muscleGroups.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> hideMuscleGroupErrorLabel());

        if(exercise == null){
            headerLabel.setText("Add new exercise");
            confirmButton.setText("Add");
            confirmButton.setOnAction(event -> addNewExercise());
        }else{
            headerLabel.setText("Editing: " + exercise.getName());
            confirmButton.setText("Confirm");

            try {
                imageURL = new URL(exercise.getImage().getUrl());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            nameField.setText(exercise.getName());
            imageSourceField.setText(exercise.getImage().getUrl());
            imagePreview.setImage(exercise.getImage());
            muscleGroups.setValue(exercise.getMuscleGroup());
            descriptionField.setText(exercise.getDescription());

            confirmButton.setOnAction(event -> confirmExerciseEdit());
            exerciseToEdit = exercise;
        }
    }

    public void chooseImage(){
        if(imageSourceField != null){
            try{
                imageURL = new URL(imageSourceField.getText()); //Set to URL-variable to verify input as URL

                if(invalidURLMessage.isVisible()){
                    invalidURLMessage.setVisible(false);
                }

                imagePreview.setImage(new Image(String.valueOf(imageURL.toURI())));
                imageChosen = true;
            } catch (MalformedURLException | URISyntaxException e) {
                //throw new RuntimeException(e);
                imagePreview.setImage(null);
                invalidURLMessage.setVisible(true);
            }
        }
    }

    public void closeWindow(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public boolean checkInputFields(){
        if(nameField.getText().equals("") || !imageChosen || muscleGroups.getSelectionModel().getSelectedItem() == null || descriptionField.getText().equals("")){
            return false;
        }
        if(nameField.getText().equals("Name can't be empty") || descriptionField.getText().equals("Description can't be empty")){
            return false;
        }
        return true;
    }

    public void throwInputFieldError(){
        if(nameField.getText().equals("")){
            nameField.setText("Name can't be empty");
        }
        if(!imageChosen){
            invalidURLMessage.setVisible(true);
        }
        if(muscleGroups.getSelectionModel().getSelectedItem() == null){
            invalidMuscleGroup.setVisible(true);
        }
        if(descriptionField.getText().equals("")){
            descriptionField.setText("Description can't be empty");
        }
    }

    public void hideMuscleGroupErrorLabel(){
        if(invalidMuscleGroup.visibleProperty().get()){
            invalidMuscleGroup.setVisible(false);
        }
    }

    public void addNewExercise(){
        if(checkInputFields()){
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet result = null;

            try {
                con = Database.getDatabase();
                con.setAutoCommit(false);
                String sql = ("SELECT MAX(exercise_id) FROM exercise");
                stmt = con.prepareStatement(sql);
                result = stmt.executeQuery();
                int higestID = -1;
                if(result.next()){
                    higestID = (result.getInt(1)) + 1;
                }

                sql = ("SELECT workout_type_id FROM workout_type WHERE workout_type_name = ?");
                stmt = con.prepareStatement(sql);
                String muscleGroup = muscleGroups.getSelectionModel().getSelectedItem().toString();
                stmt.setString(1, muscleGroup);
                result = stmt.executeQuery();
                int muscleGroupID = -1;
                if(result.next()){
                    muscleGroupID = result.getInt("workout_type_id");
                }

                String name = nameField.getText();
                String description = descriptionField.getText();
                String image = String.valueOf(imageURL.toURI());

                sql = ("INSERT INTO exercise (exercise_id, exercise_name, exercise_description, exercise_picture, workout_type_id) VALUES (?, ?, ?, ?, ?)");
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, higestID);
                stmt.setString(2, name);
                stmt.setString(3, description);
                stmt.setString(4, image);
                stmt.setInt(5, muscleGroupID);
                stmt.executeUpdate();
                stmt.close();
                con.commit();
                con.close();

                System.out.println("Successfully added new exercise");

                Exercise newExercise = new Exercise(higestID, name, description, new Image(image), muscleGroup);
                exerciseController.readInNewExercise(newExercise);
                closeWindow();
            } catch (SQLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }else {
            throwInputFieldError();
        }
    }

    public void confirmExerciseEdit(){
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet result = null;

        try{
            con = Database.getDatabase();
            con.setAutoCommit(false);

            String sql = ("SELECT workout_type_id FROM workout_type WHERE workout_type_name = ?");
            stmt = con.prepareStatement(sql);
            String muscleGroup = muscleGroups.getSelectionModel().getSelectedItem().toString();
            System.out.println("IS THIS IT: " + muscleGroup);
            stmt.setString(1, muscleGroup);
            result = stmt.executeQuery();
            int muscleGroupID = -1;
            if(result.next()){
                muscleGroupID = result.getInt("workout_type_id");
            }

            String name = nameField.getText();
            String description = descriptionField.getText();
            String image = String.valueOf(imageURL.toURI());

            System.out.println(muscleGroupID + " | " + name + " | " + description + " | " + image);

            stmt.close();
            con.commit();
            con.close();
        } catch (SQLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
