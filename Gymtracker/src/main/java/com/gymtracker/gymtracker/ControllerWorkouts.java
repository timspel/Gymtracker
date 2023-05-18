package com.gymtracker.gymtracker;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.*;
import model.Set;
import model.Template;

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
    private ChoiceBox<String> exercisesChoiceBox;
    @FXML
    private Button addExerciseButton;
    @FXML
    private DatePicker workoutDatePicker;
    @FXML
    private TextField workoutNameTextField;
    @FXML
    private ChoiceBox<Category> categoriesChoiceBox;
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
    private TextField weightTextField;
    @FXML
    private TextField workoutDescriptionTextField;
    @FXML
    private TextField templateNameTextField;
    @FXML
    private TextField templateDescriptionTextField;
    @FXML
    private ChoiceBox<Category> categoriesTemplateChoiceBox;
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
    @FXML
    private Button newWorkoutButton;
    @FXML
    private Button loadedAddExerciseButton;
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
    private boolean loadedTemplate = false;
    private boolean loadedWorkout = false;
    private List<ExerciseWorkoutTab> loadedWorkoutExercises;
    private List<Set> loadedWorkoutSets;
    private boolean created;
    private boolean removed;

    public ControllerWorkouts() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadedAddExerciseButton.setVisible(false);
        categoriesChoiceBox.getItems().setAll(Category.values());
        categoriesTemplateChoiceBox.getItems().setAll(Category.values());
        populateExerciseChoiceBox();
        populateWorkoutChoiceBox();

        sets = new ArrayList<>();
        exercises = new ArrayList<>();
        exercisesTemplate = new ArrayList<>();
        templates = new ArrayList<>();

        SpinnerValueFactory<Integer> repetitionsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        repetitionsSpinner.setValueFactory(repetitionsValueFactory);

        templateName.setCellValueFactory(new PropertyValueFactory<>("templateName"));
        templateDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        templateCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        setTemplates(Singleton.getInstance().getUserId());
        templatesTable.setItems(FXCollections.observableArrayList(templates));

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

        setsTable.setItems(FXCollections.observableArrayList(sets));

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

        exercisesTable.setItems(FXCollections.observableArrayList(exercises));

        exercisesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExerciseWorkoutTab>() {
            @Override
            public void changed(ObservableValue<? extends ExerciseWorkoutTab> observableValue, ExerciseWorkoutTab exerciseWorkoutTab, ExerciseWorkoutTab t1) {
                if(exercisesTable.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = exercisesTable.getSelectionModel();
                    ObservableList selectedCells = selectionModel.getSelectedCells();
                    TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                    selectedExerciseRow = tablePosition.getRow();

                    if(loadedTemplate){
                        exercisesChoiceBox.setValue(loadedTemplateExercises.get(selectedExerciseRow).getExerciseName());
                        if(loadedTemplateExercises.get(selectedExerciseRow).getNumberOfSets() > 0){
                            sets = loadedTemplateExercises.get(selectedExerciseRow).getSets();
                            numberOfSets = loadedTemplateExercises.get(selectedExerciseRow).getNumberOfSets();
                        }
                        else{
                            sets = new ArrayList<>();
                            numberOfSets = 0;
                        }
                        setsTable.setItems(FXCollections.observableArrayList(sets));
                    }
                    if(loadedWorkout){
                        exercisesChoiceBox.setValue(loadedWorkoutExercises.get(selectedExerciseRow).getExerciseName());
                        loadedWorkoutSets = loadedWorkoutExercises.get(selectedExerciseRow).getSets();
                        numberOfSets = loadedWorkoutExercises.get(selectedExerciseRow).getNumberOfSets();
                        setsTable.setItems(FXCollections.observableArrayList(loadedWorkoutSets));
                    }
                }
            }
        });

        templateExerciseNameColumn.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        exercisesTemplateTable.setItems(FXCollections.observableArrayList(exercisesTemplate));

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
            if(loadedWorkout){
                updateWorkout();
            }
            else{
                saveWorkout();
            }

            if(created){
                populateWorkoutChoiceBox();
                clearWorkoutSection();
            }
        }
        if(e.getSource() == removeExerciseButton){
            removeExercise();
            clearExerciseSection();
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
            clearExerciseSection();
        }
        if(e.getSource() == removeTemplateButton){
            removeTemplate();
        }
        if(e.getSource() == loadTemplateButton){
            loadTemplate();
        }
        if(e.getSource() == newWorkoutButton){
            clearWorkoutSection();
        }
        if(e.getSource() == templateRemoveExerciseButton){
            removeTemplateExercise();
        }
        if(e.getSource() == templateAddExerciseButton){
            addExerciseToTemplate();
        }
        if(e.getSource() == loadedAddExerciseButton){
            loadedAddExercise();
        }
        if(e.getSource() == removeWorkoutButton){
            removeWorkout();

            if(removed){
                populateWorkoutChoiceBox();
                clearWorkoutSection();
            }
        }
        if(e.getSource() == loadWorkoutButton){
            loadWorkout();
        }
    }

    public void loadWorkout(){
        loadedWorkout = workoutLoaded(workoutChoiceBox.getValue(), Singleton.getInstance().getUserId());

        if(loadedWorkout){
            workoutNameTextField.setText(workoutChoiceBox.getValue());
            addExerciseButton.setText("Update exercise");
            saveWorkoutButton.setText("Save changes to calendar");
            scrollToPosition(Singleton.getInstance().getWorkoutScroll(), 250);
            loadedAddExerciseButton.setVisible(true);
        }
    }

    public boolean workoutLoaded(String workoutName, int userId){
        loadedWorkoutExercises = new ArrayList<>();
        loadedWorkoutSets = new ArrayList<>();
        int workoutTypeId;
        Time workoutTime;
        int exerciseId;
        String exerciseName;
        int numberOfSets = 0;

        try(Connection conn = Database.getDatabase()){
            String query = "select workout_id, workout_description, workout_type_id, date from Workout where workout_name = ? and user_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, workoutName);
            statement.setInt(2, userId);
            ResultSet result1 = statement.executeQuery();

            if(result1.next()){
                workoutId = result1.getInt("workout_id");
                workoutDescriptionTextField.setText(result1.getString("workout_description"));
                workoutTime = result1.getTime("date");
                workoutDatePicker.setValue(result1.getDate("date").toLocalDate());

                LocalTime formatTime = workoutTime.toLocalTime();
                // Create a DateTimeFormatter for the desired format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                // Format the LocalTime to a string
                String formattedTime = formatter.format(formatTime);
                workoutTimeTextField.setText(formattedTime);

                workoutTypeId = result1.getInt("workout_type_id");

                query = "SELECT workout_type_name FROM workout_type WHERE workout_type_id = ?";
                statement = conn.prepareStatement(query);
                statement.setInt(1, workoutTypeId);
                ResultSet result2 = statement.executeQuery();

                if(result2.next()){
                    categoriesChoiceBox.setValue(Category.valueOf(result2.getString("workout_type_name")));
                }
            }

            query = "select exercise_id from workout_exercise where workout_id = ?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, workoutId);
            ResultSet result3 = statement.executeQuery();

            while(result3.next()){
                exerciseId = result3.getInt("exercise_id");

                query = "select exercise_name from exercise where exercise_id = ?";
                statement = conn.prepareStatement(query);
                statement.setInt(1, exerciseId);
                ResultSet result4 = statement.executeQuery();

                if(result4.next()){
                    exerciseName = result4.getString("exercise_name");

                    query = "select set_number, weight, reps from exercise_set where exercise_id = ? and workout_id = ?";
                    statement = conn.prepareStatement(query);
                    statement.setInt(1, exerciseId);
                    statement.setInt(2, workoutId);
                    ResultSet result5 = statement.executeQuery();

                    numberOfSets = 0;
                    loadedWorkoutSets = new ArrayList<>();

                    while(result5.next()){
                        loadedWorkoutSets.add(new Set(result5.getInt("set_number"), result5.getInt("reps"), result5.getDouble("weight")));
                        numberOfSets++;
                    }

                    if(numberOfSets == 1){
                        exercise = new ExerciseWorkoutTab(exerciseName, loadedWorkoutSets.get(0).toString(), loadedWorkoutSets);
                    }
                    if(numberOfSets == 2){
                        exercise = new ExerciseWorkoutTab(exerciseName, loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets);
                    }
                    if(numberOfSets == 3){
                        exercise = new ExerciseWorkoutTab(exerciseName, loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets);
                    }
                    if(numberOfSets == 4){
                        exercise = new ExerciseWorkoutTab(exerciseName, loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets.get(3).toString(), loadedWorkoutSets);
                    }
                    if(numberOfSets == 5){
                        exercise = new ExerciseWorkoutTab(exerciseName, loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets.get(3).toString(), loadedWorkoutSets.get(4).toString(), loadedWorkoutSets);
                    }
                    loadedWorkoutExercises.add(exercise);

                    exercisesTable.setItems(FXCollections.observableArrayList(loadedWorkoutExercises));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void loadTemplate(){
        loadedTemplate = templateLoaded(templates.get(selectedTemplateRow).getTemplateName(), Singleton.getInstance().getUserId());

        if(loadedTemplate){
            workoutNameTextField.setText(templates.get(selectedTemplateRow).getTemplateName());
            workoutDescriptionTextField.setText(templates.get(selectedTemplateRow).getDescription());
            categoriesChoiceBox.setValue(Category.valueOf(templates.get(selectedTemplateRow).getCategory()));
            exercisesTable.setItems(FXCollections.observableArrayList(loadedTemplateExercises));
            addExerciseButton.setText("Update exercise");
            scrollToPosition(Singleton.getInstance().getWorkoutScroll(), 250);
            loadedAddExerciseButton.setVisible(true);
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
        exercisesTemplateTable.setItems(FXCollections.observableArrayList(exercisesTemplate));
    }

    public void saveTemplate(){
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
            created = newTemplateRegistered(Singleton.getInstance().getUserId(), templateNameTextField.getText(), templateDescriptionTextField.getText(), categoryId);
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
        setTemplates = setTemplates(Singleton.getInstance().getUserId());

        if(setTemplates){
            templatesTable.setItems(FXCollections.observableArrayList(templates));
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
            exercisesTemplate = getSelectedTemplateExercises(templates.get(selectedTemplateRow).getTemplateName(), Singleton.getInstance().getUserId());
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
            setTemplates = setTemplates(Singleton.getInstance().getUserId());

            if(setTemplates){
                templatesTable.setItems(FXCollections.observableArrayList(templates));
            }
            else{
                System.out.println("could not set templates");
            }
        }
    }

    public int getTemplateId(String templateName){
        int templateId = 0;
        try(Connection conn = Database.getDatabase()){
            String query = "select template_id from template where template_name = ? and user_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, templateName);
            statement.setInt(2, Singleton.getInstance().getUserId());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                templateId = resultSet.getInt("template_id");
            }
            statement.close();
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

            // delete the corresponding records from the workout_exercise table
            String sql = "DELETE FROM template_exercise WHERE template_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, templateId);
            pstmt.executeUpdate();

            sql = "DELETE FROM template WHERE template_id = ?";
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

    public boolean addExerciseToWorkout(int workoutId, String exerciseName) {
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
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean addSetToExercise(int workoutId, String exerciseName, int setNumber, int reps, double weight){
        try(Connection con = Database.getDatabase()){
            String sql = "SELECT exercise_id FROM Exercise WHERE exercise_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, exerciseName);

            // Execute the query and retrieve the exercise ID
            ResultSet rs = pstmt.executeQuery();
            int exerciseId = -1; // Default value in case exercise name not found
            if (rs.next()) {
                exerciseId = rs.getInt("exercise_id");
            }

            PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Exercise_set (workout_id, exercise_id, set_number, reps, weight) VALUES (?, ?, ?, ?, ?)");
            insertStmt.setInt(1, workoutId);
            insertStmt.setInt(2, exerciseId);
            insertStmt.setInt(3, setNumber);
            insertStmt.setInt(4, reps);
            insertStmt.setDouble(5, weight);
            insertStmt.executeUpdate();

            System.out.println("Exercise set added successfully");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
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

    public boolean addParticipantToWorkout(int workoutId, int userId){
        try(Connection conn = Database.getDatabase()){
            String query = "INSERT INTO Workout_participants (workout_id, user_id) VALUES (?,?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, workoutId);
            statement.setInt(2, userId);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void saveWorkout(){
        int categoryId;
        try{
            categoryId = switch (categoriesChoiceBox.getValue()) {
                case Chest -> 1;
                case Back -> 2;
                case Legs -> 3;
                case Arms -> 4;
                case Shoulders -> 5;
                case Push -> 6;
                case Pull -> 7;
                case Abs -> 8;
            };

            created = false;
            if(exercises.size() > 0 || loadedWorkoutExercises.size() > 0 || loadedTemplateExercises.size() > 0){
                if(workoutNameTextField.getText().length() > 1 && workoutDescriptionTextField.getText().length() > 1 && workoutDatePicker.getValue() != null && workoutTimeTextField.getText().length() > 1){
                    created = newWorkoutRegistered(Singleton.getInstance().getUserId(), workoutNameTextField.getText(), workoutDescriptionTextField.getText(), categoryId, workoutDatePicker.getValue(), workoutTimeTextField.getText(), true);

                    if(created){
                        boolean addParticipant = addParticipantToWorkout(workoutId, Singleton.getInstance().getUserId());

                        if(addParticipant){
                            if(loadedTemplate){
                                for(int i = 0; i < loadedTemplateExercises.size(); i++){
                                    boolean addedExercise = false;
                                    boolean addedSet = false;

                                    addedExercise = addExerciseToWorkout(workoutId, loadedTemplateExercises.get(i).getExerciseName());

                                    if(addedExercise){
                                        for(int j = 0; j < loadedTemplateExercises.get(i).getSets().size(); j++){
                                            addedSet = addSetToExercise(workoutId, loadedTemplateExercises.get(i).getExerciseName(), loadedTemplateExercises.get(i).getSets().get(j).getSetNumber(), loadedTemplateExercises.get(i).getSets().get(j).getRepetitions(), loadedTemplateExercises.get(i).getSets().get(j).getWeight());

                                            if(!addedSet){
                                                System.out.println("could not add set to exercise");
                                            }
                                        }
                                    }
                                    else{
                                        System.out.println("could not add exercise to workout");
                                    }
                                }
                            }
                            if(loadedWorkout){
                                for(int i = 0; i < loadedWorkoutExercises.size(); i++){
                                    boolean addedExercise = false;
                                    boolean addedSet = false;

                                    addedExercise = addExerciseToWorkout(workoutId, loadedWorkoutExercises.get(i).getExerciseName());

                                    if(addedExercise){
                                        for(int j = 0; j < loadedWorkoutExercises.get(i).getSets().size(); j++){
                                            addedSet = addSetToExercise(workoutId, loadedWorkoutExercises.get(i).getExerciseName(), loadedWorkoutExercises.get(i).getSets().get(j).getSetNumber(), loadedWorkoutExercises.get(i).getSets().get(j).getRepetitions(), loadedWorkoutExercises.get(i).getSets().get(j).getWeight());

                                            if(!addedSet){
                                                System.out.println("could not add set to exercise");
                                            }
                                        }
                                    }
                                    else{
                                        System.out.println("could not add exercise to workout");
                                    }
                                }
                            }
                            else{
                                for(int i = 0; i < exercises.size(); i++){
                                    boolean addedExercise = false;
                                    boolean addedSet = false;

                                    addedExercise = addExerciseToWorkout(workoutId, exercises.get(i).getExerciseName());

                                    if(addedExercise){
                                        for(int j = 0; j < exercises.get(i).getSets().size(); j++){
                                            addedSet = addSetToExercise(workoutId, exercises.get(i).getExerciseName(), exercises.get(i).getSets().get(j).getSetNumber(), exercises.get(i).getSets().get(j).getRepetitions(), exercises.get(i).getSets().get(j).getWeight());

                                            if(!addedSet){
                                                System.out.println("could not add set to exercise");
                                            }
                                        }
                                    }
                                    else{
                                        System.out.println("could not add exercise to workout");
                                    }
                                }
                            }
                        }
                        else{
                            System.out.println("could not add participant");
                        }
                    }
                    else{
                        showErrorMessage(2);
                    }
                }
                else{
                    showErrorMessage(1);
                }
            }
            else{
                showErrorMessage(3);
            }

        } catch (Exception e){
            showErrorMessage(1);
        }
    }

    public void updateWorkout(){
        //lägg till if satser som checkar samma som i saveWorkout
        removeWorkout();
        saveWorkout();
    }

    public void removeWorkout(){
        int workoutId = getWorkoutId(workoutChoiceBox.getValue(), Singleton.getInstance().getUserId());
        removed = workoutRemoved(workoutId);

        if(removed){
            System.out.println("Workout successfully removed");
        }
        else{
            System.out.println("could not remove workout");
        }
    }

    public int getWorkoutId(String workoutName, int userId){
        int workoutId = 0;
        try(Connection conn = Database.getDatabase()){
            String query = "SELECT workout_id FROM Workout WHERE workout_name = ? and user_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, workoutName);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                workoutId = resultSet.getInt("workout_id");
            }
            statement.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return workoutId;
    }

    public boolean workoutRemoved(int workoutId){
        try(Connection conn = Database.getDatabase()){
            String query = "DELETE FROM Workout_exercise where workout_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, workoutId);
            statement.executeUpdate();

            query = "DELETE FROM Exercise_set where workout_id = ?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, workoutId);
            statement.executeUpdate();

            query = "DELETE FROM Workout_participants where workout_id = ?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, workoutId);
            statement.executeUpdate();

            query = "DELETE FROM Workout where workout_id = ?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, workoutId);
            statement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void addSet(){
        if(numberOfSets < 5){
            if(loadedWorkout){
                set = new Set((numberOfSets + 1), repetitionsSpinner.getValue(), Double.parseDouble(weightTextField.getText()));
                loadedWorkoutSets.add(set);
                setsTable.setItems(FXCollections.observableArrayList(loadedWorkoutSets));
            }
            else{
                set = new Set((numberOfSets + 1), repetitionsSpinner.getValue(), Double.parseDouble(weightTextField.getText()));
                sets.add(set);
                setsTable.setItems(FXCollections.observableArrayList(sets));
            }
            numberOfSets++;
        }
    }

    public void removeSet(){
        if(loadedWorkout){
            loadedWorkoutSets.remove(selectedSetRow);
            loadedWorkoutSets.sort(new SetComparator());
        }
        else{
            sets.remove(selectedSetRow);
            sets.sort(new SetComparator());
        }
        numberOfSets--;
        updateSetsTable();
    }

    public void updateSetsTable(){
        numberOfSets = 0;

        if(loadedWorkout){
            for(int i = loadedWorkoutSets.size()-1; i >= 0; i--){
                set = loadedWorkoutSets.get(i);
                loadedWorkoutSets.remove(i);
                loadedWorkoutSets.add(new Set(numberOfSets + 1, set.getRepetitions(), set.getWeight()));
                numberOfSets++;
            }
            setsTable.setItems(FXCollections.observableArrayList(loadedWorkoutSets));
        }
        else{
            for(int i = sets.size()-1; i >= 0; i--){
                set = sets.get(i);
                sets.remove(i);
                sets.add(new Set(numberOfSets + 1, set.getRepetitions(), set.getWeight()));
                numberOfSets++;
            }
            setsTable.setItems(FXCollections.observableArrayList(sets));
        }
    }

    public void addExercise(){
        if(loadedWorkout){
            if(numberOfSets == 1){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets);
            }
            if(numberOfSets == 2){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets);
            }
            if(numberOfSets == 3){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets);
            }
            if(numberOfSets == 4){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets.get(3).toString(), loadedWorkoutSets);
            }
            if(numberOfSets == 5){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets.get(3).toString(), loadedWorkoutSets.get(4).toString(), loadedWorkoutSets);
            }
            loadedWorkoutExercises.remove(selectedExerciseRow);
            loadedWorkoutExercises.add(selectedExerciseRow, exercise);
            exercisesTable.setItems(FXCollections.observableArrayList(loadedWorkoutExercises));
        }
        else{
            if(numberOfSets == 1){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets);
            }
            if(numberOfSets == 2){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets);
            }
            if(numberOfSets == 3){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString(), sets);
            }
            if(numberOfSets == 4){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString(), sets.get(3).toString(), sets);
            }
            if(numberOfSets == 5){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString(), sets.get(3).toString(), sets.get(4).toString(), sets);
            }
        }

        if(loadedTemplate){
            loadedTemplateExercises.remove(selectedExerciseRow);
            loadedTemplateExercises.add(selectedExerciseRow, exercise);
            exercisesTable.setItems(FXCollections.observableArrayList(loadedTemplateExercises));
        }
        if(!loadedWorkout && !loadedTemplate){
            exercises.add(exercise);
            exercisesTable.setItems(FXCollections.observableArrayList(exercises));
        }
    }

    public void loadedAddExercise(){
        if(loadedTemplate){
            if(numberOfSets == 1){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets);
            }
            if(numberOfSets == 2){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets);
            }
            if(numberOfSets == 3){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString(), sets);
            }
            if(numberOfSets == 4){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString(), sets.get(3).toString(), sets);
            }
            if(numberOfSets == 5){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), sets.get(0).toString(), sets.get(1).toString(), sets.get(2).toString(), sets.get(3).toString(), sets.get(4).toString(), sets);
            }

            loadedTemplateExercises.add(exercise);
            exercisesTable.setItems(FXCollections.observableArrayList(loadedTemplateExercises));
        }
        if(loadedWorkout){
            if(numberOfSets == 1){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets);
            }
            if(numberOfSets == 2){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets);
            }
            if(numberOfSets == 3){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets);
            }
            if(numberOfSets == 4){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets.get(3).toString(), loadedWorkoutSets);
            }
            if(numberOfSets == 5){
                exercise = new ExerciseWorkoutTab(exercisesChoiceBox.getValue(), loadedWorkoutSets.get(0).toString(), loadedWorkoutSets.get(1).toString(), loadedWorkoutSets.get(2).toString(), loadedWorkoutSets.get(3).toString(), loadedWorkoutSets.get(4).toString(), loadedWorkoutSets);
            }

            loadedWorkoutExercises.add(exercise);
            exercisesTable.setItems(FXCollections.observableArrayList(loadedWorkoutExercises));
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

    public void populateWorkoutChoiceBox(){
        workoutChoiceBox.getItems().clear();
        try(Connection conn = Database.getDatabase()){
            String query = "SELECT workout_name FROM Workout where user_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, Singleton.getInstance().getUserId());
            ResultSet rs = statement.executeQuery();

            // Add the workout names to the ComboBox
            while (rs.next()) {
                String workoutName = rs.getString("workout_name");
                workoutChoiceBox.getItems().add(workoutName);
            }

            rs.close();
            statement.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void removeExercise(){
        if(loadedTemplate){
            loadedTemplateExercises.remove(selectedExerciseRow);
            exercisesTable.setItems(FXCollections.observableArrayList(loadedTemplateExercises));
        }
        if(loadedWorkout){
            loadedWorkoutExercises.remove(selectedExerciseRow);
            exercisesTable.setItems(FXCollections.observableArrayList(loadedWorkoutExercises));
        }
        else{
            exercises.remove(selectedExerciseRow);
            exercisesTable.setItems(FXCollections.observableArrayList(exercises));
        }
    }

    public void removeTemplateExercise(){
        exercisesTemplate.remove(selectedExerciseTemplateRow);
        exercisesTemplateTable.setItems(FXCollections.observableArrayList(exercisesTemplate));
    }

    public void clearWorkoutSection(){
        workoutNameTextField.clear();
        workoutDescriptionTextField.clear();
        workoutDatePicker.setValue(null);
        workoutTimeTextField.clear();
        categoriesChoiceBox.setValue(null);
        exercisesChoiceBox.setValue(null);
        repetitionsSpinner.getValueFactory().setValue(0);
        weightTextField.clear();
        numberOfSets = 0;
        loadedAddExerciseButton.setVisible(false);
        saveWorkoutButton.setText("Save workout to calendar");
        addExerciseButton.setText("Add exercise");

        if(loadedTemplate){
            loadedTemplateExercises = new ArrayList<>();
            sets = new ArrayList<>();
            exercisesTable.setItems(FXCollections.observableArrayList(loadedTemplateExercises));
            setsTable.setItems(FXCollections.observableArrayList(sets));
        }
        if(loadedWorkout){
            loadedWorkoutExercises = new ArrayList<>();
            loadedWorkoutSets = new ArrayList<>();
            exercisesTable.setItems(FXCollections.observableArrayList(loadedWorkoutExercises));
            setsTable.setItems(FXCollections.observableArrayList(loadedWorkoutSets));
        }
        else{
            exercises = new ArrayList<>();
            sets = new ArrayList<>();
            exercisesTable.setItems(FXCollections.observableArrayList(exercises));
            setsTable.setItems(FXCollections.observableArrayList(sets));
        }
    }

    public void clearExerciseSection(){
        if(loadedWorkout){
            loadedWorkoutSets = new ArrayList<>();
            setsTable.setItems(FXCollections.observableArrayList(loadedWorkoutSets));
        }
        else{
            sets = new ArrayList<>();
            setsTable.setItems(FXCollections.observableArrayList(sets));
        }
        numberOfSets = 0;
        exercisesChoiceBox.setValue(null);
        weightTextField.clear();
        repetitionsSpinner.getValueFactory().setValue(0);
    }

    public void scrollToPosition(ScrollPane scrollPane, int position) {
        javafx.scene.Node content = scrollPane.getContent();
        AnchorPane anchorPaneContent = (AnchorPane) content;
        Bounds bounds = anchorPaneContent.getBoundsInLocal();
        double contentHeight = bounds.getHeight();

        // Calculate the position within the AnchorPane based on the desired position

        // Adjust the vertical scroll position
        double vValue = position / contentHeight;
        scrollPane.setVvalue(vValue);

        // Adjust the horizontal scroll position if needed
        double hValue = 0.0; // Set the desired horizontal scroll position
        scrollPane.setHvalue(hValue);
    }

    public void showErrorMessage(int errorCode){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Please fill in all fields");

        switch (errorCode){
            case 1:
                alert.setContentText("Please fill in all fields");
                break;
            case 2:
                alert.setContentText("Please enter correct time format");
                break;
            case 3:
                alert.setContentText("Please add at least one exercise");
        }

        alert.showAndWait();
    }

}

class SetComparator implements Comparator<Set>{
    @Override
    public int compare(Set a, Set b) {
        return b.getSetNumber() - a.getSetNumber();
    }
}
