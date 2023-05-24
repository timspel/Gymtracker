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
import javafx.scene.paint.Color;
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

/**
 * This class is responsible for handling events and actions related to the workout tab.
 * @author Samuel Carlsson
 */
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
    @FXML
    private Label errorAddSetLabel;
    @FXML
    private Label errorRemoveSetLabel;
    @FXML
    private Label errorAddNewExerciseLabel;
    @FXML
    private Label errorAddExerciseWorkoutLabel;
    @FXML
    private Label errorRemoveExerciseWorkoutLabel;
    @FXML
    private Label errorNewWorkoutLabel;
    @FXML
    private Label errorSaveWorkoutLabel;
    @FXML
    private Label errorUpdateWorkoutLabel;
    @FXML
    private Label errorAddExerciseTemplateLabel;
    @FXML
    private Label errorRemoveExerciseTemplateLabel;
    @FXML
    private Label errorSaveTemplateLabel;
    @FXML
    private Label errorRemoveTemplateLabel;
    @FXML
    private Label errorLoadTemplateLabel;
    private Set set;
    private int numberOfSets = 0;
    private List<Set> sets;
    private List<ExerciseWorkoutTab> exercises;
    private int selectedSetRow = -1;
    private int selectedTemplateRow = -1;
    private int selectedExerciseRow = -1;
    private int selectedExerciseTemplateRow = -1;
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
    private boolean exerciseAdded = false;
    private boolean exerciseRemoved = false;

    /**
     * Initializes the controller class.
     * Creates all arrayes, sets default values for components, sets the users saved templates and adds listeners for when clicking in the tables.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        disableAllErrorMessages();
        loadedAddExerciseButton.setVisible(false);
        workoutDatePicker.setEditable(false);
        repetitionsSpinner.setEditable(false);
        categoriesChoiceBox.getItems().setAll(Category.values());
        categoriesTemplateChoiceBox.getItems().setAll(Category.values());
        populateExerciseChoiceBox();
        populateWorkoutChoiceBox();

        sets = new ArrayList<>();
        loadedWorkoutSets = new ArrayList<>();
        exercises = new ArrayList<>();
        exercisesTemplate = new ArrayList<>();
        templates = new ArrayList<>();
        loadedWorkoutExercises = new ArrayList<>();
        loadedTemplateExercises = new ArrayList<>();

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

    /**
     * Handles the button press events.
     * @param e The ActionEvent object representing the button press event.
     */
    @FXML
    public void buttonPressed(ActionEvent e){
        disableAllErrorMessages();
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
                created = false;
            }
        }
        if(e.getSource() == removeExerciseButton){
            removeExercise();
            if(exerciseRemoved){
                clearExerciseSection();
                exerciseRemoved = false;
            }
        }
        if(e.getSource() == saveTemplateButton){
            saveTemplate();

            if(created){
                clearTemplateSection();
                created = false;
            }
        }
        if(e.getSource() == addSetButton){
            addSet();
        }
        if(e.getSource() == removeSetButton){
            removeSet();
        }
        if(e.getSource() == addExerciseButton){
            addExercise();
            if(exerciseAdded){
                clearExerciseSection();
                exerciseAdded = false;
            }
        }
        if(e.getSource() == removeTemplateButton){
            removeTemplate();
        }
        if(e.getSource() == loadTemplateButton){
            loadTemplate();
        }
        if(e.getSource() == newWorkoutButton){
            newWorkout();
        }
        if(e.getSource() == templateRemoveExerciseButton){
            removeTemplateExercise();
        }
        if(e.getSource() == templateAddExerciseButton){
            addExerciseToTemplate();
        }
        if(e.getSource() == loadedAddExerciseButton){
            loadedAddExercise();
            if(exerciseAdded){
                clearExerciseSection();
                exerciseAdded = false;
            }
        }
        if(e.getSource() == removeWorkoutButton){
            removeWorkout();
            if(removed){
                populateWorkoutChoiceBox();
                clearWorkoutSection();
                removed = false;
            }
        }
        if(e.getSource() == loadWorkoutButton){
            loadWorkout();
        }
    }

    /**
     * Clears the workout section and disables error messages if there are exercises or sets present.
     * Shows an error message if there are missing inputs.
     */
    public void newWorkout(){
        if(exercises.size() > 0 || loadedWorkoutExercises.size() > 0 || loadedTemplateExercises.size() > 0){
            clearWorkoutSection();
            disableAllErrorMessages();
        }
        else if(sets.size() > 0 || loadedWorkoutSets.size() > 0){
            clearWorkoutSection();
            disableAllErrorMessages();
        }
        else if(workoutNameTextField.getText().length() > 1 || workoutDescriptionTextField.getText().length() > 1 || workoutDatePicker.getValue() != null || workoutTimeTextField.getText().length() > 1 || categoriesChoiceBox.getValue() != null){
            clearWorkoutSection();
            disableAllErrorMessages();
        }
        else if(exercisesChoiceBox.getValue() != null || repetitionsSpinner.getValue() > 0 || weightTextField.getText().length() > 0){
            clearWorkoutSection();
            disableAllErrorMessages();
        }
        else{
            showErrorMessage(4);
        }
    }

    /**
     * Loads a selected workout.
     * Clears the workout section and loads the selected workout if available.
     * Shows an error message if no workout is selected.
     */
    public void loadWorkout(){
        if(workoutChoiceBox.getValue() != null){
            clearWorkoutSection();
            loadedWorkout = workoutLoaded(workoutChoiceBox.getValue(), Singleton.getInstance().getUserId());

            if(loadedWorkout){
                workoutNameTextField.setText(workoutChoiceBox.getValue());
                addExerciseButton.setText("Update exercise");
                saveWorkoutButton.setText("Save changes to calendar");
                scrollToPosition(Singleton.getInstance().getWorkoutScroll(), 250);
                loadedAddExerciseButton.setVisible(true);
            }
        }
        else{
            showErrorMessage(11);
        }
    }

    /**
     * Checks if a workout is successfully loaded when trying to retrieve its details from the database.
     * @param workoutName The name of the workout that should load.
     * @param userId The ID of the user associated with the workout.
     * @return True if the workout is successfully loaded, false otherwise.
     */
    public boolean workoutLoaded(String workoutName, int userId){
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

    /**
     * Loads a selected template.
     * Clears the workout section and loads the selected template if available.
     * Shows an error message if no template is selected.
     */
    public void loadTemplate(){
        if(selectedTemplateRow != -1){
            clearWorkoutSection();
            loadedTemplate = templateLoaded(templates.get(selectedTemplateRow).getTemplateName(), Singleton.getInstance().getUserId());

            if(loadedTemplate){
                workoutNameTextField.setText(templates.get(selectedTemplateRow).getTemplateName());
                workoutDescriptionTextField.setText(templates.get(selectedTemplateRow).getDescription());
                categoriesChoiceBox.setValue(Category.valueOf(templates.get(selectedTemplateRow).getCategory()));
                exercisesTable.setItems(FXCollections.observableArrayList(loadedTemplateExercises));
                addExerciseButton.setText("Update exercise");
                scrollToPosition(Singleton.getInstance().getWorkoutScroll(), 250);
                loadedAddExerciseButton.setVisible(true);
                selectedTemplateRow = -1;
            }
            else{
                System.out.println("could not load template");
            }
        }
        else{
            showErrorMessage(16);
        }
    }

    /**
     * Checks if a template is successfully loaded when trying to retrieve its details from the database.
     * @param templateName The name of the loaded template.
     * @param userId The ID of the user associated with the template.
     * @return True if the template is successfully loaded, false otherwise.
     */
    public boolean templateLoaded(String templateName, int userId){
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

    /**
     * Adds an exercise to the template.
     * Shows the added exercise in the template table.
     * Shows an error message if no exercise is selected.
     */
    public void addExerciseToTemplate(){
        if(exercisesTemplateChoiceBox.getValue() != null){
            exercisesTemplate.add(new ExerciseWorkoutTab(exercisesTemplateChoiceBox.getValue()));
            exercisesTemplateTable.setItems(FXCollections.observableArrayList(exercisesTemplate));
            exercisesTemplateChoiceBox.setValue(null);
        }
        else{
            showErrorMessage(12);
        }
    }

    /**
     * Tries saving the template to the database.
     * Clears the template section if the template is successfully saved.
     */
    public void saveTemplate(){
        int categoryId = 0;
        try{
            categoryId = switch (categoriesTemplateChoiceBox.getValue()) {
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
        } catch (NullPointerException e){
            showErrorMessage(14);
        }

        created = false;

        if(templateNameTextField.getText().length() > 1 && templateDescriptionTextField.getText().length() > 1 && categoryId > 0){
            if(exercisesTemplate.size() > 0){
                created = newTemplateRegistered(Singleton.getInstance().getUserId(), templateNameTextField.getText(), templateDescriptionTextField.getText(), categoryId);

                boolean registered = false;
                boolean setTemplates = false;
                for(int i = 0; i < exercisesTemplate.size(); i++){
                    registered = templateRegistered(templateId, exercisesTemplate.get(i).getExerciseName());
                }
                if(!registered){
                    System.out.println("could not save template");
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
            else{
                showErrorMessage(15);
            }
        }
        else{
            showErrorMessage(14);
        }
    }

    /**
     * Removes the selected template from the list and updates the templates table.
     * If no template is selected, an error message is shown.
     */
    public void removeTemplate(){
        if(selectedTemplateRow != -1) {
            boolean deleted = false;
            boolean setTemplates = false;
            int templateId = 0;

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
            selectedTemplateRow = -1;
        }
        else{
            showErrorMessage(17);
        }
    }

    /**
     * Retrieves the template ID for the given template name from the database.
     * @param templateName the name of the template
     * @return the template ID
     */
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

    /**
     * Retrieves the exercises associated with the selected template from the database.
     * @param templateName the name of the template
     * @param userId the ID of the user
     * @return a list of ExerciseWorkoutTab objects representing the template exercises
     */
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

    /**
     * Retrieves and sets the templates for the specified user.
     * @param userId the ID of the user
     * @return true if the templates are added to the array successfully, false otherwise
     */
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

    /**
     * Inserts a template with the given template ID and exercise name to the database.
     * @param templateId the ID of the template
     * @param exerciseName the name of the exercise
     * @return true if the template is registered, false otherwise
     */
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

    /**
     * Deletes the template with the specified template ID.
     * @param templateId the ID of the template to be deleted
     * @return true if the template is deleted successfully, false otherwise
     */
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

    /**
     * Adds an exercise to the specified workout.
     * @param workoutId the ID of the workout
     * @param exerciseName the name of the exercise to be added
     * @return true if the exercise is added successfully, false otherwise
     */
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

    /**
     * Adds a set to the specified exercise in the workout.
     * @param workoutId the ID of the workout
     * @param exerciseName the name of the exercise
     * @param setNumber the set number
     * @param reps the number of repetitions
     * @param weight the weight lifted
     * @return true if the set is added successfully, false otherwise
     */
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

    /**
     * Inserts a new workout in the database.
     * @param userId the ID of the user registering the workout
     * @param workoutName the name of the workout
     * @param workoutDescription the description of the workout
     * @param categoryId the ID of the workout category
     * @param date the date of the workout
     * @param time the time of the workout
     * @param isOriginal indicates that the workout belongs to the user who created it
     * @return true if the workout is successfully registered, false otherwise
     */
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

    /**
     * Registers a new template in the database.
     * @param userId the ID of the user registering the template
     * @param templateName the name of the template
     * @param templateDescription the description of the template
     * @param categoryId the ID of the template category
     * @return true if the template is successfully registered, false otherwise
     */
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

    /**
     * Adds the user as a participant to a workout.
     * @param workoutId the ID of the workout
     * @param userId the ID of the participant
     * @return true if the participant is successfully added, false otherwise
     */
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

    /**
     * Saves the workout data to the database.
     * Checks that all fields are filled in and exercises are added to the workout.
     */
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
            if(exercises.size() > 0 || loadedTemplateExercises.size() > 0 || loadedWorkoutExercises.size() > 0){
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

        } catch (NullPointerException e){
            showErrorMessage(1);
        } catch (IndexOutOfBoundsException e){
            showErrorMessage(3);
        }
    }

    /**
     * Updates the existing workout in the database by first removing the old values and then adding new.
     * Checks that all fields are filled in and exercises are added to the workout.
     */
    public void updateWorkout(){
        try{
            if(loadedWorkoutExercises.get(0).getExerciseName() != null) {
                if(workoutNameTextField.getText().length() > 1 && workoutDescriptionTextField.getText().length() > 1 && workoutDatePicker.getValue() != null && workoutTimeTextField.getText().length() > 1) {
                    removeWorkout();
                    saveWorkout();
                }
                else{
                    showErrorMessage(1);
                }
            }
        } catch (NullPointerException e){
            showErrorMessage(1);
        } catch (IndexOutOfBoundsException e){
            showErrorMessage(3);
        }
    }

    /**
     * Removes a workout from the database.
     * Checks that a workout is selected.
     */
    public void removeWorkout(){
        if(workoutChoiceBox.getValue() != null){
            int workoutId = getWorkoutId(workoutChoiceBox.getValue(), Singleton.getInstance().getUserId());
            removed = workoutRemoved(workoutId);

            if(removed){
                System.out.println("Workout successfully removed");
            }
            else{
                System.out.println("could not remove workout");
            }
        }
        else{
            showErrorMessage(11);
        }
    }

    /**
     * Retrieves the ID of a workout based on its name and user ID.
     * @param workoutName the name of the workout
     * @param userId the ID of the user
     * @return the ID of the workout, or 0 if not found
     */
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

    /**
     * Tries to remove a workout from the database.
     * @param workoutId the ID of the workout to be removed
     * @return true if the workout is successfully removed, false otherwise
     */
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

    /**
     * Adds a set to the workout.
     * Validates the number of repetitions and the maximum number of sets allowed.
     */
    public void addSet(){
        try{
            if(repetitionsSpinner.getValue() > 0){
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
                    disableAllErrorMessages();
                }
                else{
                    showErrorMessage(6);
                }
            }
            else{
                showErrorMessage(5);
            }
        } catch (Exception e){
            showErrorMessage(5);
        }
    }

    /**
     * Removes a set from the workout and checks that a set is selected.
     */
    public void removeSet(){
        if(selectedSetRow > -1){
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
            selectedSetRow = -1;
            disableAllErrorMessages();
        }
        else{
            showErrorMessage(7);
        }
    }

    /**
     * Updates the sets table by reordering the sets and updating the table view.
     */
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

    /**
     * Adds an exercise to the workout.
     * Validates that an exercise is selected and the presence of sets.
     */
    public void addExercise(){
        exerciseAdded = false;
        try{
            if(exercisesChoiceBox.getValue() != null){
                if(sets.size() > 0 || loadedWorkoutSets.size() > 0){
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

                    exercise = null;
                    numberOfSets = 0;
                    sets = new ArrayList<>();
                    loadedWorkoutSets = new ArrayList<>();
                    disableAllErrorMessages();
                    exerciseAdded = true;
                }
            }
            else{
                showErrorMessage(9);
            }
        } catch (Exception e){
            showErrorMessage(10);
        }
    }

    /**
     * Adds an exercise to the workout when the user has loaded a saved workout.
     * Validates that an exercise is selected and the presence of sets.
     */
    public void loadedAddExercise(){
        exerciseAdded = false;
        if(exercisesChoiceBox.getValue() != null) {
            if (sets.size() > 0 || loadedWorkoutSets.size() > 0) {
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
                    exerciseAdded = true;
                }
            }
            else{
                showErrorMessage(19);
            }
        }
        else{
            showErrorMessage(18);
        }
    }

    /**
     * Populates the exercise choice box with exercise names from the database.
     */
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

    /**
     * Populates the workout choice box with workout names connected to the user from the database.
     */
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

    /**
     * Removes an exercise from the workout.
     * Checks that an exercise is selected and what type of workout is being edited.
     */
    public void removeExercise(){
        if(selectedExerciseRow > -1){
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
            selectedExerciseRow = -1;
            exerciseRemoved = true;
            disableAllErrorMessages();
        }
        else{
            showErrorMessage(8);
        }
    }

    /**
     * Removes the selected exercise from a template.
     * If an exercise is selected, it will be removed from the list of exercises.
     * The table view will be updated accordingly.
     * If no exercise is selected, an error message will be shown.
     */
    public void removeTemplateExercise(){
        if(selectedExerciseTemplateRow != -1){
            exercisesTemplate.remove(selectedExerciseTemplateRow);
            exercisesTemplateTable.setItems(FXCollections.observableArrayList(exercisesTemplate));
            selectedExerciseTemplateRow = -1;
        }
        else{
            showErrorMessage(13);
        }
    }

    /**
     * Clears the workout section.
     * Resets all fields and variables related to the workout section.
     * Resets all ArrayLists and tableViews.
     */
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
            loadedTemplate = false;
        }
        if(loadedWorkout){
            loadedWorkoutExercises = new ArrayList<>();
            loadedWorkoutSets = new ArrayList<>();
            exercisesTable.setItems(FXCollections.observableArrayList(loadedWorkoutExercises));
            setsTable.setItems(FXCollections.observableArrayList(loadedWorkoutSets));
            loadedWorkout = false;
        }
        else{
            exercises = new ArrayList<>();
            sets = new ArrayList<>();
            exercisesTable.setItems(FXCollections.observableArrayList(exercises));
            setsTable.setItems(FXCollections.observableArrayList(sets));
        }
    }

    /**
     * Clears the exercise section.
     * Resets all fields and variables related to the exercise section.
     * Resets all ArrayLists and the set table.
     */
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

    /**
     * Clears the template section.
     * Resets all fields and variables related to the template section.
     * Resets the exercise-ArrayList and the exercise table for templates.
     */
    public void clearTemplateSection(){
        exercisesTemplate = new ArrayList<>();
        exercisesTemplateTable.setItems(FXCollections.observableArrayList(exercisesTemplate));
        exercisesTemplateChoiceBox.setValue(null);
        templateNameTextField.clear();
        templateDescriptionTextField.clear();
        categoriesTemplateChoiceBox.setValue(null);
    }

    /**
     * Scrolls to the specified position within the scroll pane.
     * @param scrollPane The scroll pane to scroll.
     * @param position   The desired position to scroll to.
     */
    public void scrollToPosition(ScrollPane scrollPane, int position) {
        javafx.scene.Node content = scrollPane.getContent();
        AnchorPane anchorPaneContent = (AnchorPane) content;
        Bounds bounds = anchorPaneContent.getBoundsInLocal();
        double contentHeight = bounds.getHeight();

        // Adjust the vertical scroll position
        double vValue = position / contentHeight;
        scrollPane.setVvalue(vValue);

        // Adjust the horizontal scroll position if needed
        double hValue = 0.0; // Set the desired horizontal scroll position
        scrollPane.setHvalue(hValue);
    }

    /**
     * Shows an error message based on the given error code.
     * @param errorCode The error code indicating the type of error.
     */
    public void showErrorMessage(int errorCode){
        switch (errorCode){
            case 1:
                errorSaveWorkoutLabel.setText("Please fill in all fields");
                errorSaveWorkoutLabel.setTextFill(Color.RED);
                errorSaveWorkoutLabel.setVisible(true);
                break;
            case 2:
                errorSaveWorkoutLabel.setText("Please enter correct time format");
                errorSaveWorkoutLabel.setTextFill(Color.RED);
                errorSaveWorkoutLabel.setVisible(true);
                break;
            case 3:
                errorSaveWorkoutLabel.setText("Please add at least one exercise");
                errorSaveWorkoutLabel.setTextFill(Color.RED);
                errorSaveWorkoutLabel.setVisible(true);
                break;
            case 4:
                errorNewWorkoutLabel.setText("Workout is already cleared");
                errorNewWorkoutLabel.setTextFill(Color.RED);
                errorNewWorkoutLabel.setVisible(true);
                break;
            case 5:
                errorAddSetLabel.setText("Please fill in weight and reps");
                errorAddSetLabel.setTextFill(Color.RED);
                errorAddSetLabel.setVisible(true);
                break;
            case 6:
                errorAddSetLabel.setText("Max number of sets are 5");
                errorAddSetLabel.setTextFill(Color.RED);
                errorAddSetLabel.setVisible(true);
                break;
            case 7:
                errorRemoveSetLabel.setText("Please select a set");
                errorRemoveSetLabel.setTextFill(Color.RED);
                errorRemoveSetLabel.setVisible(true);
                break;
            case 8:
                errorRemoveExerciseWorkoutLabel.setText("Please select a exercise");
                errorRemoveExerciseWorkoutLabel.setTextFill(Color.RED);
                errorRemoveExerciseWorkoutLabel.setVisible(true);
                break;
            case 9:
                errorAddExerciseWorkoutLabel.setText("Please select exercise");
                errorAddExerciseWorkoutLabel.setTextFill(Color.RED);
                errorAddExerciseWorkoutLabel.setVisible(true);
                break;
            case 10:
                errorAddExerciseWorkoutLabel.setText("Please add at least one set");
                errorAddExerciseWorkoutLabel.setTextFill(Color.RED);
                errorAddExerciseWorkoutLabel.setVisible(true);
                break;
            case 11:
                errorUpdateWorkoutLabel.setText("Please select workout");
                errorUpdateWorkoutLabel.setTextFill(Color.RED);
                errorUpdateWorkoutLabel.setVisible(true);
                break;
            case 12:
                errorAddExerciseTemplateLabel.setText("Please select exercise");
                errorAddExerciseTemplateLabel.setTextFill(Color.RED);
                errorAddExerciseTemplateLabel.setVisible(true);
                break;
            case 13:
                errorRemoveExerciseTemplateLabel.setText("Please select a exercise");
                errorRemoveExerciseTemplateLabel.setTextFill(Color.RED);
                errorRemoveExerciseTemplateLabel.setVisible(true);
                break;
            case 14:
                errorSaveTemplateLabel.setText("Please fill in all fields");
                errorSaveTemplateLabel.setTextFill(Color.RED);
                errorSaveTemplateLabel.setVisible(true);
                break;
            case 15:
                errorSaveTemplateLabel.setText("Please add at least one exercise");
                errorSaveTemplateLabel.setTextFill(Color.RED);
                errorSaveTemplateLabel.setVisible(true);
                break;
            case 16:
                errorLoadTemplateLabel.setText("Please select a template");
                errorLoadTemplateLabel.setTextFill(Color.RED);
                errorLoadTemplateLabel.setVisible(true);
                break;
            case 17:
                errorRemoveTemplateLabel.setText("Please select a template");
                errorRemoveTemplateLabel.setTextFill(Color.RED);
                errorRemoveTemplateLabel.setVisible(true);
                break;
            case 18:
                errorAddNewExerciseLabel.setText("Please select exercise");
                errorAddNewExerciseLabel.setTextFill(Color.RED);
                errorAddNewExerciseLabel.setVisible(true);
                break;
            case 19:
                errorAddNewExerciseLabel.setText("Please add at least one set");
                errorAddNewExerciseLabel.setTextFill(Color.RED);
                errorAddNewExerciseLabel.setVisible(true);
                break;
        }
    }

    /**
     * Disables all error messages.
     * Hides all error message labels.
     */
    public void disableAllErrorMessages(){
        errorAddSetLabel.setVisible(false);
        errorRemoveSetLabel.setVisible(false);
        errorAddNewExerciseLabel.setVisible(false);
        errorAddExerciseWorkoutLabel.setVisible(false);
        errorRemoveExerciseWorkoutLabel.setVisible(false);
        errorNewWorkoutLabel.setVisible(false);
        errorSaveWorkoutLabel.setVisible(false);
        errorUpdateWorkoutLabel.setVisible(false);
        errorAddExerciseTemplateLabel.setVisible(false);
        errorRemoveExerciseTemplateLabel.setVisible(false);
        errorSaveTemplateLabel.setVisible(false);
        errorRemoveTemplateLabel.setVisible(false);
        errorLoadTemplateLabel.setVisible(false);
    }

}

/**
 * Comparator implementation for comparing Set objects.
 * Sets are compared based on their set number in descending order.
 * @author Samuel Carlsson
 */
class SetComparator implements Comparator<Set>{

    /**
     * Compares two Set objects based on their set numbers.
     * @param a the first Set object to compare
     * @param b the second Set object to compare
     * @return a negative integer, zero, or a positive integer as the first Set is less than, equal to, or greater than the second Set
     */
    @Override
    public int compare(Set a, Set b) {
        return b.getSetNumber() - a.getSetNumber();
    }
}
