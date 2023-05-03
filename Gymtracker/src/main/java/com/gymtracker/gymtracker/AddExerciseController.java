package com.gymtracker.gymtracker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void chooseImage(){
        if(imageSourceField != null){
            //System.out.println(imageSourceField.getText());
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

    public void AddNewExercise(){
        String name = nameField.getText();
        String image = imageSourceField.getText();
        String muscleGroup = muscleGroups.getSelectionModel().getSelectedItem().toString();
        String description = descriptionField.getText();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {
            con = Database.getDatabase();
            con.setAutoCommit(false);

            String sql = ("SELECT MAX(exercise_id) FROM exercise;");
            stmt = con.prepareStatement(sql);
            result = stmt.executeQuery();

            int higestID;
            if(result.next()){
                higestID = result.getInt("MAX(exercise_id)");
            }

            sql = ("INSERT INTO exercise (exercise_id, exercise_name, exercise_description, exercise_picture, workout_type_id) VALUES (?, ?, ?, ?, ?)");
            stmt = con.prepareStatement(sql);
            result = stmt.executeQuery();

            if(result.next()){
                
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
