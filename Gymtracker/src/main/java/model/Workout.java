package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Workout {
    private String workoutName;
    private LocalDate date;
    private Category category;

    public Workout(String workoutName, LocalDate date, Category category){
        this.workoutName = workoutName;
        this.date = date;
        this.category = category;
    }



    public String getWorkoutName(){
        return workoutName;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", workoutName, category, date);
    }

}
