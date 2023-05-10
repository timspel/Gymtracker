package com.gymtracker.gymtracker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Exercise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddExerciseController {
    @FXML
    private TextField nameField;
    @FXML
    private ImageView imagePreview;
    @FXML
    private TextField imageSourceField;
    @FXML
    private ChoiceBox muscleGroups = new ChoiceBox();
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button cancelButton;
    private ExerciseController exerciseController;

    public void initialize() {
        populateMuscleGroups();
    }

    public void setExerciseController(ExerciseController exerciseController){
        this.exerciseController = exerciseController;
    }

    public void populateMuscleGroups(){
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet result = null;

        try{
            con = Database.getDatabase();
            con.setAutoCommit(false);

            String sql = ("SELECT workout_type_name FROM workout_type");
            stmt = con.prepareStatement(sql);
            result = stmt.executeQuery();
            while(result.next()){
                String workoutType = result.getString("workout_type_name");
                muscleGroups.getItems().add(workoutType);
            }
            stmt.close();
            con.commit();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void chooseImage(){
        if(imageSourceField != null){
            Image image = new Image(imageSourceField.getText());
            imagePreview.setImage(image);
        }
        /*Stage stage = new Stage();
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fc.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File selectedFile = fc.showOpenDialog(stage);
        imageSourceField.setText(selectedFile.toURI().toString());
        Image exerciseImage = new Image(selectedFile.toURI().toString());
        addImageField.setImage(exerciseImage);*/
    }

    public void cancelOperation(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void addNewExercise(){
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
            String image = imageSourceField.getText();

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
            cancelOperation();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
