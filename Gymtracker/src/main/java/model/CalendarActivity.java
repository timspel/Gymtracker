package model;

import java.time.ZonedDateTime;

public class CalendarActivity {

    private String username;
    private ZonedDateTime date;
    private String workoutName;



    private int workoutId;


    public CalendarActivity(String username, ZonedDateTime date, String workoutName, int workoutId) {
        this.username = username;
        this.date = date;
        this.workoutName = workoutName;
        this.workoutId = workoutId;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }


    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }


    @Override
    public String toString() {
        return "CalendarActivity{" +
                "username='" + username + '\'' +
                ", date=" + date +
                ", workoutName='" + workoutName + '\'' +
                '}';
    }

}
