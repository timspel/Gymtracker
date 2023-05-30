package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CalendarActivity;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;

/**

 The CalendarController class implements the Initializable interface and handles the calendar view.
 */
public class CalendarController implements Initializable {

    ZonedDateTime dateFocus;
    ZonedDateTime today;

    private CalendarActivity selectedActivity = null;
    @FXML
    private Text year;
    @FXML private Text month;
    @FXML
    private FlowPane calendar;

    /**

     Initializes the calendar view.
     @param url The location used to resolve relative paths for the root object.
     @param resourceBundle The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
    }

    @FXML
    void currentMonth(ActionEvent event) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
    }
    /**

     Handles the event when the "Back" button is clicked to navigate to the previous month.
     @param event The action event triggered by clicking the "Back" button.
     */
    @FXML
    void backOneMonth(ActionEvent event){
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();

    }
    /**

     Handles the event when the "Forward" button is clicked to navigate to the next month.
     @param event The action event triggered by clicking the "Forward" button.
     */
    @FXML
    void ForwardOneMonth(ActionEvent event){
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    void refreshCalendar() {
        calendar.getChildren().clear();
        drawCalendar();
    }

    /**

     Draws the calendar for the selected month.

     Populates the calendar view with dates and activities for the month.
     */
    private void drawCalendar(){
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(dateFocus.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        Map<Integer, List<CalendarActivity>> calendarActivityMap = getCalendarActivitiesMonth(dateFocus);


        int monthMaxDate = dateFocus.getMonth().maxLength();
        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29){
            monthMaxDate= 28;
        }

        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1,0,0,0,0,dateFocus.getZone()).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth = (calendarWidth / 7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);

                double rectangleHeight = (calendarHeight / 6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);


                int calculatedDate = (j + 1) + (7 * i);
                if (calculatedDate > dateOffset) {
                    int currentDate = calculatedDate - dateOffset;
                    if (currentDate <= monthMaxDate) {
                        Text date = new Text(String.valueOf(currentDate));
                        double textTranslationY = -(rectangleHeight / 2) * 0.75;
                        date.setTranslateY(textTranslationY);
                        stackPane.getChildren().add(date);

                        List<CalendarActivity> calendarActivities = calendarActivityMap.get(currentDate);
                        if (calendarActivities != null) {
                            createCalendarActivity(calendarActivities, rectangleHeight, rectangleWidth, stackPane);
                        }
                    }
                    if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate) {
                        rectangle.setStroke(Color.FORESTGREEN);
                        rectangle.setFill(Color.LIGHTGRAY);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }

    /**

     Creates calendar activity boxes and populates them with activities for a specific date.

     @param calendarActivities The list of activities for a specific date.

     @param rectangleHeight The height of the rectangle representing a date.

     @param rectangleWidth The width of the rectangle representing a date.

     @param stackPane The StackPane to which the calendar activity boxes will be added.
     */
    private void createCalendarActivity(List<CalendarActivity> calendarActivities, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox calendarActivityBox = new VBox();
        double boxWidth = (rectangleWidth / 2) * 0.75; // calculate the width of the VBox
        double maxWidth = 0.0;

        // Create a list to hold the selected activity for each activity box
        List<CalendarActivity> selectedActivities = new ArrayList<>();

        for (int k = 0; k < calendarActivities.size(); k++) {
            CalendarActivity calendarActivity = calendarActivities.get(k);

            Text text = new Text(" - " + calendarActivity.getUsername() + ", " + calendarActivity.getDate().toLocalTime() + ", " + calendarActivity.getWorkoutName());
            text.setWrappingWidth(rectangleWidth);

            maxWidth = Math.max(maxWidth, text.getBoundsInLocal().getWidth());
            text.setMouseTransparent(false); // Set the Text object to receive mouse events

            // Add an OnMouseClicked event handler to the Text object
            int activityBoxIndex = k; // Index of the current activity box
            text.setOnMouseClicked(event -> {

                // Store the selected activity's data in the list for the current activity box
                selectedActivities.set(activityBoxIndex, calendarActivity);

                // Load the dialog box
                // Assume you have an activity object called "selectedActivity"
                String workoutName = calendarActivity.getWorkoutName();
                String date = String.valueOf(calendarActivity.getDate());
                int workoutId = calendarActivity.getWorkoutId();
                Dialog<ButtonType> dialog = loadDialog(workoutName, workoutId,date);


            });

            // Add hover effects
            text.setOnMouseEntered(event -> {
                // Apply hover effect when the mouse enters the text
                text.setFill(Color.BLUE); // Change the text color, for example
                text.setUnderline(true); // Add underline, for example

            });

            text.setOnMouseExited(event -> {
                // Remove hover effect when the mouse exits the text
                text.setFill(Color.BLACK); // Reset the text color, for example
                text.setUnderline(false); // Remove underline, for example
                // Reset other hover effects as desired
            });



            // Add the text object to the VBox
            calendarActivityBox.getChildren().add(text);

            // Add a null element to the list for the current activity box
            selectedActivities.add(null);
        }

        // Add the VBox to the StackPane
        calendarActivityBox.setPrefWidth(boxWidth);
        calendarActivityBox.setMaxWidth(maxWidth);


        calendarActivityBox.setStyle("-fx-padding: " + 18 + "px 0 0 0"); // set the top padding of the VBox

        calendarActivityBox.setFillWidth(true);
        stackPane.getChildren().add(calendarActivityBox);
    }

    /**

     Loads the dialog box for displaying the details of a selected workout.

     @param workoutName The name of the selected workout.

     @param workoutId The ID of the selected workout.

     @param workoutDate The date of the selected workout.

     @return The dialog box.
     */
    static Dialog<ButtonType> loadDialog(String workoutName, int workoutId, String workoutDate) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(CalendarDialog.class.getResource("CalendarDialog.fxml"));
            Parent root = loader.load();

            // Get the controller for the FXML file
            CalendarDialog controller = loader.getController();

            // Set the selected workout name
            controller.setSelectedWorkoutName(workoutName);
            controller.setWorkoutId(workoutId);
            controller.setWorkoutDate(workoutDate);

            // Populate the exercise table
            controller.populateExerciseTable(workoutId);
            controller.populateWorkoutParticipant(workoutId);

            // Create a new dialog with the root node
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(root);

            // Add the OK button to the dialog pane and return the dialog
            //dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            Image icon = new Image("icon.png");
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(icon);
            // Show the dialog only if it is not null
            if (dialog != null) {
                dialog.showAndWait();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**

     Retrieves the calendar activities for a specific month.

     @param date The date object representing the month.

     @return A map of date to a list of calendar activities.
     */
    private Map<Integer, List<CalendarActivity>> getCalendarActivitiesMonth(ZonedDateTime date) {
        Map<Integer, List<CalendarActivity>> calendarActivityMap = new HashMap<>();

        int userId = Singleton.getInstance().getUserId();
        try (Connection conn = Database.getDatabase()) {
            String sql = "SELECT DISTINCT w.date, u.username, w.workout_name, w.workout_id " +
                    "FROM workout w " +
                    "JOIN \"User\" u ON w.user_id = u.user_id " +
                    "LEFT JOIN friendship f ON (w.user_id = f.user2_id OR w.user_id = f.user1_id) AND f.status != 'pending' AND (f.user1_id = ? OR f.user2_id = ?) " +
                    "WHERE (w.user_id = ? OR f.user1_id = ? OR f.user2_id = ?) " +
                    "AND EXTRACT(YEAR FROM w.date) = ? " +
                    "AND EXTRACT(MONTH FROM w.date) = ? " +
                    "AND w.is_original = true " +
                    "ORDER BY w.date";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
            stmt.setInt(5, userId);
            stmt.setInt(6, date.getYear());
            stmt.setInt(7, date.getMonthValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ZonedDateTime activityDate = rs.getTimestamp("date").toLocalDateTime().atZone(ZoneId.systemDefault());
                String username = rs.getString("username");
                String workoutName = rs.getString("workout_name");
                int workoutId = rs.getInt("workout_id");
                int dayOfMonth = activityDate.getDayOfMonth();
                List<CalendarActivity> activities = calendarActivityMap.getOrDefault(dayOfMonth, new ArrayList<>());
                activities.add(new CalendarActivity(username, activityDate, workoutName, workoutId));
                calendarActivityMap.put(dayOfMonth, activities);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return calendarActivityMap;
    }



}

