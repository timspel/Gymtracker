package com.gymtracker.gymtracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ExerciseWorkoutTab;
import model.Set;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CalendarDialog {

    @FXML
    private TableView<ExerciseWorkoutTab> exerciseTable;

    @FXML
    private TableColumn<ExerciseWorkoutTab, String> exerciseName;

    @FXML
    private TableColumn<ExerciseWorkoutTab, String> exerciseSet1;

    @FXML
    private TableColumn<ExerciseWorkoutTab, String> exerciseSet2;

    @FXML
    private TableColumn<ExerciseWorkoutTab, String> exerciseSet3;

    @FXML
    private TableColumn<ExerciseWorkoutTab, String> exerciseSet4;

    @FXML
    private TableColumn<ExerciseWorkoutTab, String> exerciseSet5;

    @FXML
    private Text selectedWorkoutName;

    private List<ExerciseWorkoutTab> exercises;



    /*static Dialog<ButtonType> loadDialog(String workoutName) {
        try {


            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(CalendarDialog.class.getResource("CalendarDialog.fxml"));
            Parent root = loader.load();

            // Get the controller for the FXML file
            CalendarDialog controller = loader.getController();

            // Set the selected workout name
            controller.setSelectedWorkoutName(workoutName);

            // Populate the exercise table
            controller.populateExerciseTable(workoutName);


            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(root);

            // Add the OK button to the dialog pane and return the dialog
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            // Show the dialog only if it is not null
            if (dialog != null) {
                dialog.showAndWait();
            }

            // Create a new scene with the root node and show the dialog
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/




    public void populateExerciseTable(String workoutName) {

        exercises = new ArrayList<>();

        exerciseName.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        exerciseSet1.setCellValueFactory(new PropertyValueFactory<>("set1"));
        exerciseSet2.setCellValueFactory(new PropertyValueFactory<>("set2"));
        exerciseSet3.setCellValueFactory(new PropertyValueFactory<>("set3"));
        exerciseSet4.setCellValueFactory(new PropertyValueFactory<>("set4"));
        exerciseSet5.setCellValueFactory(new PropertyValueFactory<>("set5"));



        String sql = "SELECT exercise.exercise_name, exercise_set.set_number, exercise_set.reps " +
                "FROM workout " +
                "JOIN workout_exercise ON workout.workout_id = workout_exercise.workout_id " +
                "JOIN exercise ON workout_exercise.exercise_id = exercise.exercise_id " +
                "JOIN exercise_set ON exercise_set.workout_id = workout.workout_id AND exercise_set.exercise_id = exercise.exercise_id " +
                "WHERE workout.workout_name = ?";

        try (Connection conn = Database.getDatabase()){

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, workoutName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String exerciseName = rs.getString("exercise_name");
                    int setNumber = rs.getInt("set_number");
                    int reps = rs.getInt("reps");

                    // Find or create the exercise object in the list
                    ExerciseWorkoutTab exercise = null;
                    for (ExerciseWorkoutTab e : exercises) {
                        if (e.getExerciseName().equals(exerciseName)) {
                            exercise = e;
                            break;
                        }
                    }


                    if (exercise == null) {
                        exercise = new ExerciseWorkoutTab(exerciseName);
                        exercises.add(exercise);
                    }

                    // Set the reps for the appropriate set number
                    if (setNumber == 1) {
                        exercise.setSet1(String.valueOf(reps));
                    } else if (setNumber == 2) {
                        exercise.setSet2(String.valueOf(reps));
                    } else if (setNumber == 3) {
                        exercise.setSet3(String.valueOf(reps));
                    } else if (setNumber == 4) {
                        exercise.setSet4(String.valueOf(reps));
                    } else if (setNumber == 5) {
                        exercise.setSet5(String.valueOf(reps));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convert the exercise list to an observable list and set it in the table
        ObservableList<ExerciseWorkoutTab> observableExercises = FXCollections.observableArrayList(exercises);
        exerciseTable.setItems(observableExercises);
    }


    /*public void populateExerciseTable(String workoutName) {

        exercises = new ArrayList<>();

        exerciseName.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        exerciseSet1.setCellValueFactory(new PropertyValueFactory<>("set1"));
        exerciseSet2.setCellValueFactory(new PropertyValueFactory<>("set2"));
        exerciseSet3.setCellValueFactory(new PropertyValueFactory<>("set3"));
        exerciseSet4.setCellValueFactory(new PropertyValueFactory<>("set4"));
        exerciseSet5.setCellValueFactory(new PropertyValueFactory<>("set5"));

        exerciseTable.setItems(getObservableExercises());

        // Clear any existing data from the table
        exerciseTable.getItems().clear();

        String sql = "SELECT exercise.exercise_name, exercise_set.set_number, exercise_set.reps " +
                "FROM workout " +
                "JOIN workout_exercise ON workout.workout_id = workout_exercise.workout_id " +
                "JOIN exercise ON workout_exercise.exercise_id = exercise.exercise_id " +
                "JOIN exercise_set ON exercise_set.workout_id = workout.workout_id AND exercise_set.exercise_id = exercise.exercise_id " +
                "WHERE workout.workout_name = ?";

        try (Connection conn = Database.getDatabase()){

            PreparedStatement stmt = conn.prepareStatement(sql);
            // Set the parameter for the workout name
            stmt.setString(1, workoutName);

            // Execute the query and process the results
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    String exerciseName = rs.getString("exercise_name");
                    exercises.add(new ExerciseWorkoutTab(exerciseName));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/



    public void setSelectedWorkoutName(String workoutName) {
        selectedWorkoutName.setText(workoutName);
    }



}
