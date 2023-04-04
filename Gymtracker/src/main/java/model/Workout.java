package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Workout {
    private String workoutName;
    private LocalDate date;
    private Category category;
    private HashMap<ExerciseEnum, ArrayList<Set>> exercises;

    public Workout(String workoutName, LocalDate date, Category category, HashMap<ExerciseEnum, ArrayList<Set>> exercises){
        this.workoutName = workoutName;
        this.date = date;
        this.category = category;
        this.exercises = exercises;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", workoutName, category, date);
    }

}
