package com.gymtracker.gymtracker;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CalendarDialog {
    @FXML
    private TableView<WorkoutParticipant> participantsTable;

    @FXML
    private TableColumn<WorkoutParticipant, String> participantName;

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
    @FXML
    private Text selectedWorkoutDate;
    private int workoutId;
    @FXML
    private Text joinedStatus;
    @FXML
    private Button backButton;

    private List<ExerciseWorkoutTab> exercises;
    private List<WorkoutParticipant> workoutParticipants;

    public void initialize() {
        backButton.setOnAction(this::handleBackButtonClick);
    }

    public void populateWorkoutParticipant(int workoutId) {
        workoutParticipants = new ArrayList<>();

        participantName.setCellValueFactory(new PropertyValueFactory<>("participantName"));

        String sql = "SELECT DISTINCT u.username " +
                "FROM workout_participants wp " +
                "JOIN \"User\" u ON wp.user_id = u.user_id " +
                "WHERE wp.workout_id = (SELECT workout_id FROM workout WHERE workout_id = ?)";

        try (Connection conn = Database.getDatabase()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, workoutId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String participantName = rs.getString("username");
                    workoutParticipants.add(new WorkoutParticipant(participantName));
                }
            }

            participantsTable.setItems(FXCollections.observableList(workoutParticipants));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void populateExerciseTable(int workoutId) {

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
                "WHERE workout.workout_id = ?";

        try (Connection conn = Database.getDatabase()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, workoutId);

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
        exerciseTable.setItems(FXCollections.observableArrayList(exercises));
    }


// WORK IN PROGRESS
    public void joinWorkout(){
        int userId = UserIdSingleton.getInstance().getUserId();

        try (Connection conn = Database.getDatabase()) {
            // Check if the user is already a participant of the workout
            String sqlCheck = "SELECT * FROM workout_participants WHERE workout_id = ? AND user_id = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setInt(1, workoutId);
            stmtCheck.setInt(2, userId);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next()) {
                setJoinedStatus("You have already joined " + selectedWorkoutName.getText() + "!");
                joinedStatus.setFill(Color.RED);
                return;
            }

            // Insert a new row in the workout_participants table
            String sql = "INSERT INTO workout_participants (workout_id, user_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, workoutId);
            stmt.setInt(2, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 1) {
                setJoinedStatus("You have joined " + selectedWorkoutName.getText() + "!");
                joinedStatus.setFill(Color.BLUE);
            }

            // Insert a new row in the workout table
            String insertSql = "INSERT INTO workout (user_id, workout_name, workout_description, workout_type_id, is_original) " +
                    "SELECT ?, workout_name, workout_description, workout_type_id, false FROM workout WHERE workout_id = ?";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, workoutId);
            int insertAffectedRows = insertStmt.executeUpdate();
            if (insertAffectedRows == 1) {
                System.out.println("New workout added successfully");

                // Copy and insert exercise_set entries connected to the original workout
                String exerciseSetSql = "INSERT INTO exercise_set (workout_id, exercise_id, set_number, reps, weight) " +
                        "SELECT ?, exercise_id, set_number, reps, weight FROM exercise_set WHERE workout_id = ?";
                PreparedStatement exerciseSetStmt = conn.prepareStatement(exerciseSetSql);
                exerciseSetStmt.setInt(1, workoutId);
                exerciseSetStmt.setInt(2, workoutId);
                int exerciseSetAffectedRows = exerciseSetStmt.executeUpdate();
                System.out.println("Copied " + exerciseSetAffectedRows + " exercise_set entries to the new workout.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void handleBackButtonClick(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();

    }

    private void setJoinedStatus(String status){
        joinedStatus.setText(status);
    }

    public void setWorkoutId(int workoutId){
        this.workoutId = workoutId;
    }

    public void setSelectedWorkoutName(String workoutName) {
        selectedWorkoutName.setText(workoutName);
    }


    public void setWorkoutDate(String workoutDate) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(workoutDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - EEEE, MMM dd");
            String formattedDate = dateTime.format(formatter);
            selectedWorkoutDate.setText(formattedDate);
        } catch (DateTimeException e) {
            // Handle the exception or display an error message
            e.printStackTrace();
        }
    }
}
