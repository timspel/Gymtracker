package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

public class CalendarController implements Initializable {

    ZonedDateTime dateFocus;
    ZonedDateTime today;
    @FXML
    private Text year;
    @FXML private Text month;

    @FXML
    private FlowPane calendar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
    }

    @FXML
    void backOneMonth(ActionEvent event){
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();

    }
    @FXML
    void ForwardOneMonth(ActionEvent event){
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }


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
                        rectangle.setStroke(Color.BLUE);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }
    private void createCalendarActivity(List<CalendarActivity> calendarActivities, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox calendarActivityBox = new VBox();
        for (int k = 0; k < calendarActivities.size(); k++) {
            if(k >= 2) {
                Text moreActivities = new Text("...");
                calendarActivityBox.getChildren().add(moreActivities);
                moreActivities.setOnMouseClicked(mouseEvent -> {
                    //On ... click print all activities for given date
                    System.out.println(calendarActivities);
                });
                break;
            }
            CalendarActivity calendarActivity = calendarActivities.get(k);
            Text text = new Text(calendarActivity.getUsername() + ", " + calendarActivity.getDate().toLocalTime());
            calendarActivityBox.getChildren().add(text);
            text.setOnMouseClicked(mouseEvent -> {
                //On Text clicked
                System.out.println(text.getText());
            });
        }
        calendarActivityBox.setTranslateY((rectangleHeight / 2) * 0.75);
        calendarActivityBox.setTranslateX((rectangleWidth / 2) * 0.75);
        stackPane.getChildren().add(calendarActivityBox);
    }


    private Map<Integer, List<CalendarActivity>> createCalendarMap(ZonedDateTime date) {
        Map<Integer, List<CalendarActivity>> calendarActivityMap = new HashMap<>();
        // create some dummy data
        for (int i = 1; i <= date.getMonth().maxLength(); i++) {
            List<CalendarActivity> activities = new ArrayList<>();
            activities.add(new CalendarActivity("Alice", ZonedDateTime.of(date.getYear(), date.getMonthValue(), i, 10, 0, 0, 0, ZoneId.systemDefault()), "Cycling"));
            activities.add(new CalendarActivity("Bob", ZonedDateTime.of(date.getYear(), date.getMonthValue(), i, 14, 0, 0, 0, ZoneId.systemDefault()), "Weightlifting"));
            activities.add(new CalendarActivity("Charlie", ZonedDateTime.of(date.getYear(), date.getMonthValue(), i, 17, 0, 0, 0, ZoneId.systemDefault()), "Running"));
            calendarActivityMap.put(i, activities);
        }
        return calendarActivityMap;
    }

    private Map<Integer, List<CalendarActivity>> getCalendarActivitiesMonth(ZonedDateTime date) {
        Map<Integer, List<CalendarActivity>> calendarActivityMap = new HashMap<>();
        try (Connection conn = Database.getDatabase()) {
            String sql = "SELECT uwh.date, u.username, w.workout_name\n" +
                    "FROM user_workout_history uwh\n" +
                    "JOIN user u ON uwh.user_id = u.user_id\n" +
                    "JOIN workout w ON uwh.workout_id = w.workout_id\n" +
                    "WHERE EXTRACT(YEAR FROM uwh.date) = EXTRACT(YEAR FROM date_column)\n" +
                    "AND EXTRACT(MONTH FROM uwh.date) = EXTRACT(MONTH FROM date_column)\n" +
                    "ORDER BY uwh.date\n";

            /*String sql = "SELECT user_workout_history.date, user.username, workout.workout_name " +
                    "FROM user_workout_history " +
                    "JOIN user ON user_workout_history.user_id = user.user_id " +
                    "JOIN workout ON user_workout_history.workout_id = workout.workout_id " +
                    "WHERE EXTRACT(YEAR FROM user_workout_history.date) = ? " +
                    "AND EXTRACT(MONTH FROM user_workout_history.date) = ? " +
                    "ORDER BY user_workout_history.date";*/

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, date.getYear());
            stmt.setInt(2, date.getMonthValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ZonedDateTime activityDate = rs.getTimestamp("date").toLocalDateTime().atZone(ZoneId.systemDefault());
                String username = rs.getString("username");
                String workoutName = rs.getString("workout_name");
                int dayOfMonth = activityDate.getDayOfMonth();
                List<CalendarActivity> activities = calendarActivityMap.getOrDefault(dayOfMonth, new ArrayList<>());
                activities.add(new CalendarActivity(username, activityDate, workoutName));
                calendarActivityMap.put(dayOfMonth, activities);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return calendarActivityMap;
    }
}

