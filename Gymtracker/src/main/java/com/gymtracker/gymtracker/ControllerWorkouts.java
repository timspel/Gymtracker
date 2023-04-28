package com.gymtracker.gymtracker;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import model.Set;
import model.Template;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import static model.Category.*;

public class ControllerWorkouts implements Initializable{
    @FXML
    private Button saveWorkoutButton;
    @FXML
    private Label savedTemplatesLabel;
    @FXML
    private ChoiceBox<String> exercisesChoiceBox;
    @FXML
    private Label newWorkoutLabel;
    @FXML
    private Label exerciseLabel;
    @FXML
    private Button addExerciseButton;
    @FXML
    private DatePicker workoutDatePicker;
    @FXML
    private TextField workoutNameTextField;
    @FXML
    private Label categoryLabel;
    @FXML
    private ChoiceBox<Category> categoriesChoiceBox;
    @FXML
    private Label exercisesLabel;
    @FXML
    private Button removeExerciseButton;
    @FXML
    private TableView<ExerciseWorkoutTab> exercisesTable;
    @FXML
    private TableColumn<ExerciseWorkoutTab, String> exerciseName;
    @FXML
    private TableColumn<ExerciseWorkoutTab, Set> exerciseSet1;
    @FXML
    private TableColumn<ExerciseWorkoutTab, Set> exerciseSet2;
    @FXML
    private TableColumn<ExerciseWorkoutTab, Set> exerciseSet3;
    @FXML
    private TableColumn<ExerciseWorkoutTab, Set> exerciseSet4;
    @FXML
    private TableColumn<ExerciseWorkoutTab, Set> exerciseSet5;
    @FXML
    private Spinner<Integer> repetitionsSpinner;
    @FXML
    private Label repetitionsLabel;
    @FXML
    private Button addSetButton;
    @FXML
    private TableView<Set> setsTable;
    @FXML
    private TableColumn<Set, Integer> setRepetitions;
    @FXML
    private TableColumn<Set, Integer> setWeight;
    @FXML
    private TableColumn<Set, Integer> setNumber;
    @FXML
    private TableView<Template> templatesTable;
    @FXML
    private TableColumn<Template, String> templateName;
    @FXML
    private TableColumn<Template, String> templateDescription;
    @FXML
    private TableColumn<Template, String> templateCategory;
    @FXML
    private Button removeTemplateButton;
    @FXML
    private Button loadTemplateButton;
    @FXML
    private Button removeSetButton;
    @FXML
    private Label weightLabel;
    @FXML
    private TextField weightTextField;
    @FXML
    private Label kgLabel;
    @FXML
    private Button createWorkoutButton;
    @FXML
    private TextField workoutDescriptionTextField;
    @FXML
    private TextField templateNameTextField;
    @FXML
    private TextField templateDescriptionTextField;
    @FXML
    private ChoiceBox<Category> categoriesTemplateChoiceBox;
    @FXML
    private Button createTemplateButton;
    @FXML
    private ChoiceBox<String> exercisesTemplateChoiceBox;
    @FXML
    private Button templateAddExerciseButton;
    @FXML
    private TableView<ExerciseWorkoutTab> exercisesTemplateTable;
    @FXML
    private TableColumn<ExerciseWorkoutTab, String> templateExerciseNameColumn;
    @FXML
    private Button templateRemoveExerciseButton;
    @FXML
    private Button saveTemplateButton;
    private Set set;
    private int numberOfSets = 0;
    private List<Set> sets;
    private ObservableList<Set> dataSets;
    private List<ExerciseWorkoutTab> exercises;
    private ObservableList<ExerciseWorkoutTab> dataExercises;
    private int selectedSetRow;
    private int selectedTemplateRow;
    private int selectedExerciseRow;
    private int selectedExerciseTemplateRow;
    private ExerciseWorkoutTab exercise;
    private int workoutId;
    private List<ExerciseWorkoutTab> exercisesTemplate;

    public ControllerWorkouts() throws IOException {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoriesChoiceBox.getItems().setAll(Category.values());
        categoriesTemplateChoiceBox.getItems().setAll(Category.values());
        populateExerciseChoiceBox();

        sets = new ArrayList<>();
        exercises = new ArrayList<>();
        exercisesTemplate = new ArrayList<>();

        SpinnerValueFactory<Integer> repetitionsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        repetitionsSpinner.setValueFactory(repetitionsValueFactory);

        templateName.setCellValueFactory(new PropertyValueFactory<>("workoutName"));
        templateDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        templateCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        templatesTable.setItems(getTemplates());

        templatesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Template>() {
            @Override
            public void changed(ObservableValue<? extends Template> observableValue, Template oldValue, Template newValue) {
                if(templatesTable.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = templatesTable.getSelectionModel();
                    ObservableList selectedCells = selectionModel.getSelectedCells();
                    TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                    selectedTemplateRow = tablePosition.getRow();
                }
            }
        });

        setNumber.setCellValueFactory(new PropertyValueFactory<>("setNumber"));
        setWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        setRepetitions.setCellValueFactory(new PropertyValueFactory<>("repetitions"));

        setsTable.setItems(getObservableSets());

        setsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Set>() {
            @Override
            public void changed(ObservableValue<? extends Set> observableValue, Set oldValue, Set newValue) {
                if(setsTable.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = setsTable.getSelectionModel();
                    ObservableList selectedCells = selectionModel.getSelectedCells();
                    TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                    selectedSetRow = tablePosition.getRow();
                }
            }
        });

        exerciseName.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        exerciseSet1.setCellValueFactory(new PropertyValueFactory<>("set1"));
        exerciseSet2.setCellValueFactory(new PropertyValueFactory<>("set2"));
        exerciseSet3.setCellValueFactory(new PropertyValueFactory<>("set3"));
        exerciseSet4.setCellValueFactory(new PropertyValueFactory<>("set4"));
        exerciseSet5.setCellValueFactory(new PropertyValueFactory<>("set5"));

        exercisesTable.setItems(getObservableExercises());

        exercisesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExerciseWorkoutTab>() {
            @Override
            public void changed(ObservableValue<? extends ExerciseWorkoutTab> observableValue, ExerciseWorkoutTab exerciseWorkoutTab, ExerciseWorkoutTab t1) {
                if(exercisesTable.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = exercisesTable.getSelectionModel();
                    ObservableList selectedCells = selectionModel.getSelectedCells();
                    TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                    selectedExerciseRow = tablePosition.getRow();
                }
            }
        });

        templateExerciseNameColumn.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        exercisesTemplateTable.setItems(getObservableExercisesTemplate());

        exercisesTemplateTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExerciseWorkoutTab>() {
            @Override
            public void changed(ObservableValue<? extends ExerciseWorkoutTab> observableValue, ExerciseWorkoutTab exerciseWorkoutTab, ExerciseWorkoutTab t1) {
                if(exercisesTemplateTable.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = exercisesTemplateTable.getSelectionModel();
                    ObservableList selectedCells = selectionModel.getSelectedCells();
                    TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                    selectedExerciseTemplateRow = tablePosition.getRow();
                }
            }
        });
    }

    @FXML
    public void buttonPressed(ActionEvent e){
        if(e.getSource() == saveWorkoutButton){
            saveWorkout();
        }
        if(e.getSource() == removeExerciseButton){
            removeExercise();
        }
        if(e.getSource() == saveTemplateButton){
            saveTemplate();
        }
        if(e.getSource() == addSetButton){
            addSet();
        }
        if(e.getSource() == removeSetButton){
            removeSet();
        }
        if(e.getSource() == addExerciseButton){
            addExercise();
        }
        if(e.getSource() == removeTemplateButton){

        }
        if(e.getSource() == loadTemplateButton){

        }
        if(e.getSource() == createWorkoutButton){

        }
        if(e.getSource() == templateRemoveExerciseButton){
            removeTemplateExercise();
        }
        if(e.getSource() == templateAddExerciseButton){
            addExerciseToTemplate();
        }
        if(e.getSource() == createTemplateButton){
            createTemplate();
        }
    }

    public void addExerciseToTemplate(){
        exercisesTemplate.add(new ExerciseWorkoutTab(exercisesTemplateChoiceBox.getValue()));
        exercisesTemplateTable.setItems(getObservableExercisesTemplate());
        
    }

    public void saveTemplate(){
        boolean registered = false;
        if(exercisesTemplate.size() > 0){
            for(int i = 0; i < exercisesTemplate.size(); i++){
                registered = templateRegistered(workoutId, exercisesTemplate.get(i).getExerciseName());
            }
            if(!registered){
                System.out.println("could not save template");
            }
        }
    }

    public boolean templateRegistered(int workoutId, String exerciseName) {
        try (Connection con = Database.getDatabase()) {
            String sql = "SELECT exercise_id FROM Exercise WHERE exercise_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, exerciseName);

            // Execute the query and retrieve the exercise ID
            ResultSet rs = pstmt.executeQuery();
            int exerciseId = -1; // Default value in case exercise name not found
            if (rs.next()) {
                exerciseId = rs.getInt("exercise_id");
            }
            // Add the exercise to the workout
            try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Workout_Exercise (workout_id, exercise_id) VALUES (?, ?)")) {
                insertStmt.setInt(1, workoutId);
                insertStmt.setInt(2, exerciseId);

                insertStmt.executeUpdate();
                System.out.println("Exercise added to workout successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean addExerciseToWorkout(int workoutId, String exerciseName, int setNumber, int reps, double weight) {
        try (Connection con = Database.getDatabase()) {
            String sql = "SELECT exercise_id FROM Exercise WHERE exercise_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, exerciseName);

            // Execute the query and retrieve the exercise ID
            ResultSet rs = pstmt.executeQuery();
            int exerciseId = -1; // Default value in case exercise name not found
            if (rs.next()) {
                exerciseId = rs.getInt("exercise_id");
            }
            // Add the exercise to the workout
            try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Workout_Exercise (workout_id, exercise_id) VALUES (?, ?)")) {
                insertStmt.setInt(1, workoutId);
                insertStmt.setInt(2, exerciseId);

                insertStmt.executeUpdate();
                System.out.println("Exercise added to workout successfully");
            }
            // Add the exercise set to the workout
            try (PreparedStatement selectStmt = con.prepareStatement("SELECT workout_exercise_id FROM Workout_Exercise WHERE workout_id = ? AND exercise_id = ?")) {
                selectStmt.setInt(1, workoutId);
                selectStmt.setInt(2, exerciseId);

                try (ResultSet rs2 = selectStmt.executeQuery()) {
                    if (rs2.next()) {
                        int workoutExerciseId = rs2.getInt("workout_exercise_id");

                        try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Exercise_set (workout_id, exercise_id, set_number, reps, weight) VALUES (?, ?, ?, ?, ?)")) {
                            insertStmt.setInt(1, workoutId);
                            insertStmt.setInt(2, exerciseId);
                            insertStmt.setInt(3, setNumber);
                            insertStmt.setInt(4, reps);
                            insertStmt.setDouble(5, weight);

                            insertStmt.executeUpdate();
                            System.out.println("Exercise set added successfully");
                        }
                    } else {
                        System.out.println("Error: exercise not found in the workout");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    public void createTemplate(){
        int categoryId = switch (categoriesTemplateChoiceBox.getValue()) {
            case Chest -> 1;
            case Back -> 2;
            case Legs -> 3;
            case Arms -> 4;
            case Shoulders -> 5;
            case Push -> 6;
            case Pull -> 7;
            case Abs -> 8;
            default -> 0;
        };

        boolean created = false;

        if(templateNameTextField.getText().length() > 1 && templateDescriptionTextField.getText().length() > 1 && categoryId > 0){
            created = newTemplateRegistered(UserIdSingleton.getInstance().getUserId(), templateNameTextField.getText(), templateDescriptionTextField.getText(), categoryId);
        }
        else{
            System.out.println("fill in all fields");
        }

        if(!created){
            System.out.println("could not create workout");
        }
        else{
            System.out.println("workout created");
        }
    }

    public boolean newTemplateRegistered(int userId, String workoutName, String workoutDescription, int categoryId){
        try (Connection con = Database.getDatabase()) {
            // Insert a new workout
            try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Workout (user_id, workout_name, workout_description, workout_type_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, workoutName);
                insertStmt.setString(3, workoutDescription);
                insertStmt.setInt(4, categoryId);
                insertStmt.executeUpdate();

                // Get the generated workout ID
                workoutId = -1;
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        workoutId = generatedKeys.getInt(1);
                    }
                }

                System.out.println("New workout added successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }
    public ObservableList<Template> getTemplates(){
        return FXCollections.observableArrayList(
                new Template("Morning workout", "2023-04-04", "Push"),
                new Template("Lunch workout", "2023-04-07", "Pull"),
                new Template("Evening workout", "2023-04-11", "Legs")
        );
    }

    public ObservableList<Set> getObservableSets(){
        dataSets = FXCollections.observableArrayList(sets);
        return dataSets;
    }

    public ObservableList<ExerciseWorkoutTab> getObservableExercises(){
        dataExercises = FXCollections.observableArrayList(exercises);
        return dataExercises;
    }

    public ObservableList<ExerciseWorkoutTab> getObservableExercisesTemplate(){
        return FXCollections.observableArrayList(exercisesTemplate);
    }

    public void saveWorkout(){

    }

    public void addSet(){
        if(numberOfSets < 5){
            set = new Set((numberOfSets + 1), repetitionsSpinner.getValue(), Double.parseDouble(weightTextField.getText()));
            sets.add(set);
            numberOfSets++;
            setsTable.setItems(getObservableSets());
        }
    }

    public void removeSet(){
        sets.remove(selectedSetRow);
        numberOfSets--;
        sets.sort(new SetComparator());
        updateSetsTable();
    }

    public void updateSetsTable(){
        numberOfSets = 0;
        for(int i = sets.size()-1; i >= 0; i--){
            set = sets.get(i);
            sets.remove(i);
            sets.add(new Set(numberOfSets + 1, set.getRepetitions(), set.getWeight()));
            numberOfSets++;
        }
        setsTable.setItems(getObservableSets());
    }

    public void addExercise(){
        if(numberOfSets == 1){
            exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString());
        }
        if(numberOfSets == 2){
            exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString());
        }
        if(numberOfSets == 3){
            exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString());
        }
        if(numberOfSets == 4){
            exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString(), sets.get(3).toString());
        }
        if(numberOfSets == 5){
            exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString(), sets.get(3).toString(), sets.get(4).toString());
        }

        boolean added = false;

        for(int i = 0; i < sets.size(); i++){
            added = addExerciseToWorkout(workoutId, exercisesChoiceBox.getValue(), sets.get(i).getSetNumber(), sets.get(i).getRepetitions(), sets.get(i).getWeight());
        }

        if(!added){
            System.out.println("could not add exercise");
        }

        exercises.add(exercise);
        exercisesTable.setItems(getObservableExercises());
    }

    public void populateExerciseChoiceBox(){
        try(Connection conn = Database.getDatabase()){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT exercise_name FROM Exercise");

            // Add the exercise names to the ComboBox
            while (rs.next()) {
                String exerciseName = rs.getString("exercise_name");
                exercisesChoiceBox.getItems().add(exerciseName);
                exercisesTemplateChoiceBox.getItems().add(exerciseName);
            }

            // Close the database connection and statement
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void removeExercise(){
        exercises.remove(selectedExerciseRow);
        exercisesTable.setItems(getObservableExercises());
    }

    public void removeTemplateExercise(){
        exercisesTemplate.remove(selectedExerciseTemplateRow);
        exercisesTemplateTable.setItems(getObservableExercisesTemplate());
    }
}

class SetComparator implements Comparator<Set>{
    @Override
    public int compare(Set a, Set b) {
        return b.getSetNumber() - a.getSetNumber();
    }
}
