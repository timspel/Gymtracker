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
import model.Template;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private TableView setsTable;
    @FXML
    private TableColumn setColumn;
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
    private Spinner<Integer> weightSpinner;
    private Set set;
    @FXML
    private AnchorPane scrollAnchorPane;
    @FXML
    private Parent workoutPane;

    private Scene scene;
    private Stage stage;

    public ControllerWorkouts() throws IOException {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoriesChoiceBox.getItems().setAll(Category.values());
        exercisesChoiceBox.getItems().setAll(ExerciseEnum.values());

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
                    Object val = tablePosition.getTableColumn().getCellData(newValue);
                    System.out.println("Selected value: " + val);
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

    public void saveWorkout(){

    }

    public void addSet(){
        set = new Set(repetitionsSpinner.getValue(), weightSpinner.getValue());
    }

    public void addExercise(){
        
    }
}
