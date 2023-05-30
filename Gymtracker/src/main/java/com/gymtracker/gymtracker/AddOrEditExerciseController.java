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
    private ChoiceBox<String> muscleGroups = new ChoiceBox<>();
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

    /**
     * Initializes window based on werther choice was made to add Exercise or to edit one. Gives blank slate if add was
     * selected but fills in fields if edit was selected.
     * @param exerciseController The ExerciseController used to call readInNewExercise() and updateExercise()
     * @param exercise Exercises passed if you chose to edit an exercise
     */
    public void initializeWindow(ExerciseController exerciseController, Exercise exercise){
        this.exerciseController = exerciseController;
        exerciseController.populateMuscleGroups(muscleGroups);
        muscleGroups.getItems().remove(0);
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
            imageChosen = true;
            muscleGroups.setValue(exercise.getMuscleGroup());
            descriptionField.setText(exercise.getDescription());

            confirmButton.setOnAction(event -> confirmExerciseEdit());
            exerciseToEdit = exercise;
        }
    }

    /**
     * Called when you choose an image. Checks if the source of the image is a URL and, if it is, displays said image.
     * If the URL is invalid an error message is displayed
     */
    public void chooseImage(){
        if(imageSourceField != null){
            try{
                imageURL = new URL(imageSourceField.getText());

                if(invalidURLMessage.isVisible()){
                    invalidURLMessage.setVisible(false);
                }

                imagePreview.setImage(new Image(String.valueOf(imageURL.toURI())));
                imageChosen = true;
            } catch (MalformedURLException | URISyntaxException e) {
                imagePreview.setImage(null);
                invalidURLMessage.setVisible(true);
            }
        }
    }

    /**
     * Gets stage via FXML-element and closes it down
     */
    public void closeWindow(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Checks input fields for empty strings and null values. If invalid values are found, return a boolean reflecting this
     * @return A boolean reflecting if missing/invalid values were found
     */
    public boolean checkInputFields(){
        if(nameField.getText().equals("") || !imageChosen || muscleGroups.getSelectionModel().getSelectedItem() == null || descriptionField.getText().equals("")){
            return false;
        }
        if(nameField.getText().equals("Name can't be empty") || descriptionField.getText().equals("Description can't be empty")){
            return false;
        }
        return true;
    }

    /**
     * Throws errors depending on which input fields have invalid values.
     */
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

    /**
     * Specifically hides error label for missing muscle group
     */
    public void hideMuscleGroupErrorLabel(){
        if(invalidMuscleGroup.visibleProperty().get()){
            invalidMuscleGroup.setVisible(false);
        }
    }

    /**
     * Adds a new exercise to the database and the ObservableList in the ExerciseController. An extra call is made to
     * the database to get the muscle group id (workout_type_id) from a separate table. Throws error if any field is
     * empty or invalid
     */
    public void addNewExercise(){
        if(checkInputFields()){
            PreparedStatement stmt;
            ResultSet result;

            try(Connection con = Database.getDatabase()){
                con.setAutoCommit(false);

                String sql = ("SELECT MAX(exercise_id) FROM exercise");
                stmt = con.prepareStatement(sql);
                result = stmt.executeQuery();
                int highestID = -1;
                if(result.next()){
                    highestID = (result.getInt(1)) + 1;
                }

                sql = ("SELECT workout_type_id FROM workout_type WHERE workout_type_name = ?");
                stmt = con.prepareStatement(sql);
                String muscleGroup = muscleGroups.getSelectionModel().getSelectedItem();
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
                stmt.setInt(1, highestID);
                stmt.setString(2, name);
                stmt.setString(3, description);
                stmt.setString(4, image);
                stmt.setInt(5, muscleGroupID);
                stmt.executeUpdate();
                stmt.close();
                con.commit();
                con.close();

                System.out.println("Successfully added new exercise");

                Exercise newExercise = new Exercise(highestID, name, description, new Image(image), muscleGroup);
                exerciseController.readInNewExercise(newExercise);
                closeWindow();
            } catch (SQLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }else {
            throwInputFieldError();
        }
    }

    /**
     * Confirms edit of an Exercise, updating its values in the database and in the ObservableList in the
     * ExerciseController. An extra call is made to the database to get the muscle group id (workout_type_id) from a
     * separate table. Throws error if any field is empty or invalid
     */
    public void confirmExerciseEdit(){
        if(checkInputFields()){
            PreparedStatement stmt;
            ResultSet result;

            try(Connection con = Database.getDatabase()){
                con.setAutoCommit(false);

                String sql = ("SELECT workout_type_id FROM workout_type WHERE workout_type_name = ?");
                stmt = con.prepareStatement(sql);

                String muscleGroup = muscleGroups.getSelectionModel().getSelectedItem();
                stmt.setString(1, muscleGroup);
                result = stmt.executeQuery();
                int muscleGroupID = -1;
                if(result.next()){
                    muscleGroupID = result.getInt("workout_type_id");
                }

                String name = nameField.getText();
                String description = descriptionField.getText();
                String image = String.valueOf(imageURL.toURI());

                sql = ("UPDATE exercise SET exercise_name = ?, exercise_description = ?, exercise_picture = ?, workout_type_id = ?" +
                        "WHERE exercise_id = ?");
                stmt = con.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setString(3, image);
                stmt.setInt(4, muscleGroupID);
                stmt.setInt(5, exerciseToEdit.getId());
                stmt.executeUpdate();
                stmt.close();
                con.commit();

                exerciseController.updateExercise(exerciseToEdit.getId(), name, description, new Image(image), muscleGroup);
                closeWindow();
            } catch (SQLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }else{
            throwInputFieldError();
        }
    }
}
