package com.gymtracker.gymtracker;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;
import model.*;
import model.Set;
import model.Template;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ControllerWorkouts implements Initializable{
    @FXML
    private Button saveWorkoutButton;
    @FXML
    private Label savedTemplatesLabel;
    @FXML
    private ChoiceBox<ExerciseEnum> exercisesChoiceBox;
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
    private TableView exercisesTable;
    @FXML
    private TableColumn exerciseColumn;
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
    private Button saveAsTemplateButton;
    @FXML
    private TableView<Template> templatesTable;
    @FXML
    private TableColumn<Template, String> templateName;
    @FXML
    private TableColumn<Template, String> templateDate;
    @FXML
    private TableColumn<Template, String> templateCategory;
    @FXML
    private Button editExerciseButton;
    @FXML
    private Button removeTemplateButton;
    @FXML
    private Button editTemplateButton;
    @FXML
    private Button loadTemplateButton;
    @FXML
    private Button editSetButton;
    @FXML
    private Button removeSetButton;
    @FXML
    private Label weightLabel;
    @FXML
    private TextField weightTextField;
    @FXML
    private Label kgLabel;
    private Set set;
    private int numberOfSets = 0;
    private List<Set> sets;
    private ObservableList<Set> dataSets;
    private int selectedSetRow;
    private int selectedTemplateRow;

    public ControllerWorkouts() throws IOException {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoriesChoiceBox.getItems().setAll(Category.values());
        exercisesChoiceBox.getItems().setAll(ExerciseEnum.values());

        sets = new ArrayList<>();

        SpinnerValueFactory<Integer> repetitionsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        repetitionsSpinner.setValueFactory(repetitionsValueFactory);

        templateName.setCellValueFactory(new PropertyValueFactory<>("workoutName"));
        templateDate.setCellValueFactory(new PropertyValueFactory<>("date"));
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
    }

    @FXML
    public void buttonPressed(ActionEvent e){
        if(e.getSource() == saveWorkoutButton){
            saveWorkout();
        }
        if(e.getSource() == removeExerciseButton){

        }
        if(e.getSource() == editExerciseButton){

        }
        if(e.getSource() == saveAsTemplateButton){

        }
        if(e.getSource() == addSetButton){
            addSet();
        }
        if(e.getSource() == editSetButton){

        }
        if(e.getSource() == removeSetButton){
            removeSet();
        }
        if(e.getSource() == addExerciseButton){
            addExercise();
        }
        if(e.getSource() == removeTemplateButton){

        }
        if(e.getSource() == editTemplateButton){

        }
        if(e.getSource() == loadTemplateButton){

        }
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

    public void saveWorkout(){

    }

    public void addSet(){
        set = new Set((numberOfSets + 1), repetitionsSpinner.getValue(), Integer.parseInt(weightTextField.getText()));
        sets.add(set);
        numberOfSets++;
        setsTable.setItems(getObservableSets());
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
        
    }
}

class SetComparator implements Comparator<Set>{
    @Override
    public int compare(Set a, Set b) {
        return b.getSetNumber() - a.getSetNumber();
    }
}
