package com.gymtracker.gymtracker;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import model.*;

import java.sql.*;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller class for the calendar dialog window.
 * It handles the interaction with the user interface and performs database operations.
 */
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

    /**
     * Initializes the controller class.
     * This method is automatically called after the FXML file has been loaded.
     */
    public void initialize() {
        backButton.setOnAction(this::handleBackButtonClick);
    }

    /**
     * Populates the participants table with the participants of a workout.
     *
     * @param workoutId the ID of the workout
     */
    public void populateWorkoutParticipant(int workoutId) {
        workoutParticipants = new ArrayList<>();

        participantName.setCellValueFactory(new PropertyValueFactory<>("participantName"));

        String sql = "SELECT DISTINCT u.username, u.user_id " +
                "FROM workout_participants wp " +
                "JOIN \"User\" u ON wp.user_id = u.user_id " +
                "WHERE wp.workout_id = (SELECT workout_id FROM workout WHERE workout_id = ?)";

        try (Connection conn = Database.getDatabase()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, workoutId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String participantName = rs.getString("username");
                    int id = rs.getInt("user_id");
                    workoutParticipants.add(new WorkoutParticipant(participantName, id));
                }
            }

            participantsTable.setItems(FXCollections.observableList(workoutParticipants));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeParticipant(int participantId) {
        WorkoutParticipant participantToRemove = null;
        for (WorkoutParticipant participant : workoutParticipants) {
            if (participant.getId() == participantId) {
                participantToRemove = participant;
                break;
            }
        }
        if (participantToRemove != null) {
            workoutParticipants.remove(participantToRemove);
        }
    }

    /**
     * Populates the exercise table with the exercises and their sets of a workout.
     *
     * @param workoutId the ID of the workout
     */
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

        try (Connection conn = Database.getDatabase()) {
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


    /**
     * Handles the join workout button click event.
     * Adds the current user as a participant to the selected workout.
     */
    public void joinWorkout() {
        int userId = Singleton.getInstance().getUserId();
        int newWorkoutId = -1;

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
                populateWorkoutParticipant(workoutId);
            }

            // Insert a new row in the workout table
            String insertSql = "INSERT INTO workout (user_id, workout_name, workout_description, workout_type_id, date, is_original) " +
                    "SELECT ?, workout_name, workout_description, workout_type_id, date, false FROM workout WHERE workout_id = ?";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, workoutId);
            int insertAffectedRows = insertStmt.executeUpdate();
            if (insertAffectedRows == 1) {
                System.out.println("New workout added successfully");

                // Get the ID of the newly inserted workout
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newWorkoutId = generatedKeys.getInt(1);

                        // Copy and insert exercise_set entries connected to the original workout
                        String exerciseSetSql = "INSERT INTO exercise_set (workout_id, exercise_id, set_number, reps, weight) " +
                                "SELECT ?, exercise_id, set_number, reps, weight FROM exercise_set WHERE workout_id = ?";
                        PreparedStatement exerciseSetStmt = conn.prepareStatement(exerciseSetSql);
                        exerciseSetStmt.setInt(1, newWorkoutId);
                        exerciseSetStmt.setInt(2, workoutId);
                        int exerciseSetAffectedRows = exerciseSetStmt.executeUpdate();
                        System.out.println("Copied " + exerciseSetAffectedRows + " exercise_set entries to the new workout.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        System.out.println("New workout ID: " + newWorkoutId);
    }

    /**
     * Handles the leave workout button click event.
     * removes the current user as a participant to the selected workout.
     */
    public void leaveWorkout() {
        int userId = Singleton.getInstance().getUserId();

        try (Connection conn = Database.getDatabase()) {
            // Check if the user is a participant of the workout
            String sqlCheck = "SELECT * FROM workout_participants WHERE workout_id = ? AND user_id = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setInt(1, workoutId);
            stmtCheck.setInt(2, userId);
            ResultSet rs = stmtCheck.executeQuery();
            if (!rs.next()) {
                setJoinedStatus("You haven't joined " + selectedWorkoutName.getText() + "!");
                joinedStatus.setFill(Color.RED);
                return;
            }

            // Delete the participant's row from workout_participants table
            String deleteSql = "DELETE FROM workout_participants WHERE workout_id = ? AND user_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, workoutId);
            deleteStmt.setInt(2, userId);
            int affectedRows = deleteStmt.executeUpdate();
            if (affectedRows == 1) {
                setJoinedStatus("You have left " + selectedWorkoutName.getText() + "!");
                joinedStatus.setFill(Color.GREEN);
                removeParticipant(userId);
                populateWorkoutParticipant(workoutId);
            }

            // Get the copied workout ID based on workout name and time
            String selectCopiedWorkoutSql = "SELECT workout_id FROM workout WHERE workout_name = ? AND user_id = ? AND is_original = false ORDER BY date DESC LIMIT 1";
            PreparedStatement selectCopiedWorkoutStmt = conn.prepareStatement(selectCopiedWorkoutSql);
            selectCopiedWorkoutStmt.setString(1, selectedWorkoutName.getText());
            selectCopiedWorkoutStmt.setInt(2, userId);
            ResultSet copiedWorkoutRs = selectCopiedWorkoutStmt.executeQuery();
            if (copiedWorkoutRs.next()) {
                int copiedWorkoutId = copiedWorkoutRs.getInt("workout_id");

                // Delete the exercise_set entries associated with the copied workout
                String deleteExerciseSetSql = "DELETE FROM exercise_set WHERE workout_id = ?";
                PreparedStatement deleteExerciseSetStmt = conn.prepareStatement(deleteExerciseSetSql);
                deleteExerciseSetStmt.setInt(1, copiedWorkoutId);
                int deleteExerciseSetAffectedRows = deleteExerciseSetStmt.executeUpdate();
                System.out.println("Deleted " + deleteExerciseSetAffectedRows + " exercise_set entries from the copied workout.");

                // Delete the copied workout
                String deleteWorkoutSql = "DELETE FROM workout WHERE workout_id = ?";
                PreparedStatement deleteWorkoutStmt = conn.prepareStatement(deleteWorkoutSql);
                deleteWorkoutStmt.setInt(1, copiedWorkoutId);
                int deleteWorkoutAffectedRows = deleteWorkoutStmt.executeUpdate();
                System.out.println("Deleted the copied workout with ID: " + copiedWorkoutId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the back button click event.
     * Closes the current stage.
     *
     * @param event The action event triggered by the back button click.
     */
    private void handleBackButtonClick(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();

    }
    /**
     * Sets the joined status text with the specified status.
     *
     * @param status The status message to set.
     */
    private void setJoinedStatus(String status) {
        joinedStatus.setText(status);
    }
    /**
     * Sets the workout ID for the current dialog.
     *
     * @param workoutId The workout ID to set.
     */
    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }
    /**
     * Sets the selected workout name text with the specified name.
     *
     * @param workoutName The workout name to set.
     */
    public void setSelectedWorkoutName(String workoutName) {
        selectedWorkoutName.setText(workoutName);
    }
    /**
     * Sets the workout date for the current dialog.
     *
     * @param workoutDate The workout date to set.
     */

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
