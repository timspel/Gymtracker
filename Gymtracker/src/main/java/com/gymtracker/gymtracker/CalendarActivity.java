package com.gymtracker.gymtracker;

import java.sql.*;
import java.time.*;
import java.util.*;

public class CalendarActivity {

    private String username;
    private ZonedDateTime date;
    private String workoutName;

    public CalendarActivity(String username, ZonedDateTime date, String workoutName) {
        this.username = username;
        this.date = date;
        this.workoutName = workoutName;
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

    @Override
    public String toString() {
        return "CalendarActivity{" +
                "date=" + date +
                ", WorkoutName='" + workoutName + '\'' +
                '}';
    }
}