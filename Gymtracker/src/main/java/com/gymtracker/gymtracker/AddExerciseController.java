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
    @FXML
    private Label invalidURLMessage;
    @FXML
    private Label missingImageMessage;
    private ExerciseController exerciseController;
    private URL imageURL;
    private boolean imageChosen = false;

    public void initializeWindow(ExerciseController exerciseController){
        this.exerciseController = exerciseController;
        exerciseController.populateMuscleGroups(muscleGroups);
    }

    public void chooseImage(){
        if(imageSourceField != null){
            try{
                imageURL = new URL(imageSourceField.getText()); //Set to URL-variable to verify input as URL
                if(invalidURLMessage.isVisible()){
                    invalidURLMessage.setVisible(false);
                }
                if(missingImageMessage.isVisible()){
                    missingImageMessage.setVisible(false);
                }
                imagePreview.setImage(new Image(String.valueOf(imageURL.toURI())));
                imageChosen = true;
            } catch (MalformedURLException | URISyntaxException e) {
                //throw new RuntimeException(e);
                invalidURLMessage.setVisible(true);
            }
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
        if(imageChosen){
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
                cancelOperation();
            } catch (SQLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }else {
            missingImageMessage.setVisible(true);
        }

    }


}
