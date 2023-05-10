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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @FXML
    private TextField workoutTimeTextField;
    @FXML
    private ChoiceBox<String> workoutChoiceBox;
    @FXML
    private Button loadWorkoutButton;
    @FXML
    private Button removeWorkoutButton;
    private Set set;
    private int numberOfSets = 0;
    private List<Set> sets;
    private List<ExerciseWorkoutTab> exercises;
    private int selectedSetRow;
    private int selectedTemplateRow = -1;
    private int selectedExerciseRow;
    private int selectedExerciseTemplateRow;
    private ExerciseWorkoutTab exercise;
    private int templateId;
    private int workoutId;
    private List<ExerciseWorkoutTab> exercisesTemplate;
    private List<Template> templates;
    private List<ExerciseWorkoutTab> loadedTemplateExercises;
    private boolean loaded = false;

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
        templates = new ArrayList<>();

        SpinnerValueFactory<Integer> repetitionsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        repetitionsSpinner.setValueFactory(repetitionsValueFactory);

        templateName.setCellValueFactory(new PropertyValueFactory<>("templateName"));
        templateDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        templateCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        setTemplates(UserIdSingleton.getInstance().getUserId());
        templatesTable.setItems(getObservableTemplates());

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

                    if(loaded){
                        exercisesChoiceBox.setValue(loadedTemplateExercises.get(selectedExerciseRow).getExerciseName());
                    }
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
            removeTemplate();
        }
        if(e.getSource() == loadTemplateButton){
            loadTemplate();
        }
        if(e.getSource() == createWorkoutButton){
            createWorkout();
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

    public void loadTemplate(){
        loaded = templateLoaded(templates.get(selectedTemplateRow).getTemplateName(), UserIdSingleton.getInstance().getUserId());

        if(loaded){
            workoutNameTextField.setText(templates.get(selectedTemplateRow).getTemplateName());
            workoutDescriptionTextField.setText(templates.get(selectedTemplateRow).getDescription());
            categoriesChoiceBox.setValue(Category.valueOf(templates.get(selectedTemplateRow).getCategory()));
            exercisesTable.setItems(FXCollections.observableArrayList(loadedTemplateExercises));
            addExerciseButton.setText("Update exercise");
        }
        else{
            System.out.println("could not load template");
        }
    }

    public boolean templateLoaded(String templateName, int userId){
        loadedTemplateExercises = new ArrayList<>();
        int templateId = 0;
        int exerciseId = 0;
        try(Connection conn = Database.getDatabase()){
            String query = "select template_id from template where template_name = ? and user_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, templateName);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                templateId = resultSet.getInt("template_id");
            }

            query = "select exercise_id from template_exercise where template_id = ?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, templateId);
            resultSet = statement.executeQuery();

            while(resultSet.next()){
                exerciseId = resultSet.getInt("exercise_id");

                query = "select exercise_name from exercise where exercise_id = ?";
                statement = conn.prepareStatement(query);
                statement.setInt(1, exerciseId);
                ResultSet result = statement.executeQuery();

                if(result.next()){
                    loadedTemplateExercises.add(new ExerciseWorkoutTab(result.getString("exercise_name")));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void addExerciseToTemplate(){
        exercisesTemplate.add(new ExerciseWorkoutTab(exercisesTemplateChoiceBox.getValue()));
        exercisesTemplateTable.setItems(getObservableExercisesTemplate());
        
    }

    public void saveTemplate(){
        boolean registered = false;
        boolean setTemplates = false;
        if(exercisesTemplate.size() > 0){
            for(int i = 0; i < exercisesTemplate.size(); i++){
                registered = templateRegistered(templateId, exercisesTemplate.get(i).getExerciseName());
            }
            if(!registered){
                System.out.println("could not save template");
            }
        }

        templates = new ArrayList<>();
        setTemplates = setTemplates(UserIdSingleton.getInstance().getUserId());

        if(setTemplates){
            templatesTable.setItems(getObservableTemplates());
        }
        else{
            System.out.println("could not set templates");
        }

    }

    public void removeTemplate(){
        boolean deleted = false;
        boolean setTemplates = false;
        int templateId = 0;

        if(selectedTemplateRow != -1) {
            exercisesTemplate = getSelectedTemplateExercises(templates.get(selectedTemplateRow).getTemplateName(), UserIdSingleton.getInstance().getUserId());
            templateId = getTemplateId(templates.get(selectedTemplateRow).getTemplateName());

            if(exercisesTemplate.size() > 0){
                for(int i = 0; i < exercisesTemplate.size(); i++){
                    deleted = templateDeleted(templateId);
                }
                if(!deleted){
                    System.out.println("could not remove template");
                }
            }

            templates = new ArrayList<>();
            setTemplates = setTemplates(UserIdSingleton.getInstance().getUserId());

            if(setTemplates){
                templatesTable.setItems(getObservableTemplates());
            }
            else{
                System.out.println("could not set templates");
            }
        }
    }

    public int getTemplateId(String templateName){
        int templateId = 0;
        try(Connection conn = Database.getDatabase()){
            String query = "select template_id from template where template_name = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, templateName);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                templateId = resultSet.getInt("template_id");
            }
            statement.close();
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return templateId;
    }

    public ArrayList<ExerciseWorkoutTab> getSelectedTemplateExercises(String templateName, int userId){
        ArrayList<ExerciseWorkoutTab> exercises = new ArrayList<>();
        PreparedStatement stmt = null;

        try(Connection conn = Database.getDatabase()){
            String sql = "SELECT e.exercise_name \n" +
                    "FROM public.exercise e\n" +
                    "INNER JOIN public.template_exercise te ON e.exercise_id = te.exercise_id\n" +
                    "INNER JOIN public.template t ON t.template_id = te.template_id\n" +
                    "WHERE t.template_name = ? AND t.user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, templateName);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exercises.add(new ExerciseWorkoutTab(rs.getString("exercise_name")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("could not get template exercises");
            e.printStackTrace();
        }
        return exercises;
    }

    public boolean setTemplates(int userId){
        try (Connection conn = Database.getDatabase()){
            // Prepare the SQL query with a parameter for the user ID
            String sql = "SELECT Template.template_name, Template.template_description, Workout_Type.workout_type_name " +
                    "FROM Template " +
                    "JOIN Workout_Type ON Template.workout_type_id = Workout_Type.workout_type_id " +
                    "WHERE Template.user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);

            // Set the parameter for the user ID in the query
            statement.setInt(1, userId);

            // Execute the query and iterate over the results
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                templates.add(new Template(rs.getString("template_name"), rs.getString("template_description"), rs.getString("workout_type_name")));
            }

            // Close the statement and result set
            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean templateRegistered(int templateId, String exerciseName) {
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
            try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Template_Exercise (template_id, exercise_id) VALUES (?, ?)")) {
                insertStmt.setInt(1, templateId);
                insertStmt.setInt(2, exerciseId);

                insertStmt.executeUpdate();
                System.out.println("Exercise added to template successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean templateDeleted(int templateId){
        try (Connection con = Database.getDatabase()) {

            String sql = "DELETE FROM template WHERE template_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, templateId);
            pstmt.executeUpdate();

            // delete the corresponding records from the workout_exercise table
            sql = "DELETE FROM template_exercise WHERE template_id = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, templateId);
            pstmt.executeUpdate();

            // Close the statement and connection objects
            pstmt.close();
            con.close();
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
            System.out.println("could not create template");
        }
        else{
            System.out.println("template created");
        }
    }

    public void createWorkout(){
        int categoryId = switch (categoriesChoiceBox.getValue()) {
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

        if(workoutNameTextField.getText().length() > 1 && workoutDescriptionTextField.getText().length() > 1 && categoryId > 0 && workoutDatePicker.getValue() != null && workoutTimeTextField.getText().length() > 1){
            created = newWorkoutRegistered(UserIdSingleton.getInstance().getUserId(), workoutNameTextField.getText(), workoutDescriptionTextField.getText(), categoryId, workoutDatePicker.getValue(), workoutTimeTextField.getText(), true);
        }
        else{
            System.out.println("fill in all fields");
        }

        if(!created){
            System.out.println("could not create workout, try filling in correct time format");
        }
        else{
            System.out.println("workout created");
        }
    }

    public boolean newWorkoutRegistered(int userId, String workoutName, String workoutDescription, int categoryId, LocalDate date, String time, boolean isOriginal) {
        Timestamp timestamp = null;
        try{
            // Get the time String and parse it to LocalTime
            LocalTime formatTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

            // Combine the LocalDate and LocalTime into a LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.of(date, formatTime);

            // Format the LocalDateTime into a String of the format yy-MM-dd HH-mm-ss
            String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, formatter);
            timestamp = Timestamp.valueOf(dateTime);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        try (Connection con = Database.getDatabase()) {
            // Insert a new workout
            try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Workout (user_id, workout_name, workout_description, workout_type_id, date, is_original) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, workoutName);
                insertStmt.setString(3, workoutDescription);
                insertStmt.setInt(4, categoryId);
                insertStmt.setTimestamp(5, timestamp);
                insertStmt.setBoolean(6, isOriginal);
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

    public boolean newTemplateRegistered(int userId, String templateName, String templateDescription, int categoryId) {
        try (Connection con = Database.getDatabase()) {
            // Insert a new workout
            try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Template (user_id, template_name, template_description, workout_type_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, templateName);
                insertStmt.setString(3, templateDescription);
                insertStmt.setInt(4, categoryId);
                insertStmt.executeUpdate();

                // Get the generated template ID
                templateId = -1;
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        templateId = generatedKeys.getInt(1);
                    }
                }

                System.out.println("New template added successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    public ObservableList<Set> getObservableSets(){
        return FXCollections.observableArrayList(sets);
    }

    public ObservableList<Template> getObservableTemplates(){
        return FXCollections.observableArrayList(templates);
    }

    public ObservableList<ExerciseWorkoutTab> getObservableExercises(){
        return FXCollections.observableArrayList(exercises);
    }

    public ObservableList<ExerciseWorkoutTab> getObservableExercisesTemplate(){
        return FXCollections.observableArrayList(exercisesTemplate);
    }

    public void saveWorkout(){
        if(exercises.size() > 0){
            for(int i = 0; i < exercises.size(); i++){
                boolean added = false;

                for(int j = 0; j < sets.size(); j++){
                    added = addExerciseToWorkout(workoutId, exercises.get(i).getExerciseName(), sets.get(j).getSetNumber(), sets.get(j).getRepetitions(), sets.get(j).getWeight());
                }

                if(!added){
                    System.out.println("could not add exercise");
                }
                else{
                    clearWorkoutSection();
                    setsTable.setItems(getObservableSets());
                    exercisesTable.setItems(getObservableExercises());
                }
            }
        }
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
        if(loaded){
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
            loadedTemplateExercises.remove(selectedExerciseRow);
            loadedTemplateExercises.add(selectedExerciseRow, exercise);
            exercisesTable.setItems(FXCollections.observableArrayList(loadedTemplateExercises));

        }
        else{
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

            exercises.add(exercise);
            exercisesTable.setItems(getObservableExercises());
        }
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

    public void clearWorkoutSection(){
        exercises = new ArrayList<>();
        sets = new ArrayList<>();
        workoutNameTextField.clear();
        workoutDescriptionTextField.clear();
        workoutDatePicker.setValue(null);
        workoutTimeTextField.clear();
        categoriesChoiceBox.setValue(null);
        exercisesChoiceBox.setValue(null);
        repetitionsSpinner.getValueFactory().setValue(0);
        weightTextField.clear();
        numberOfSets = 0;
    }
}

class SetComparator implements Comparator<Set>{
    @Override
    public int compare(Set a, Set b) {
        return b.getSetNumber() - a.getSetNumber();
    }
}
