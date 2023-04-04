package model;

import java.time.LocalDate;

public class Workout {
    private String workoutName;
    private LocalDate date;
    private Category category;
    public Workout(String workoutName, LocalDate date, Category category){
        this.workoutName = workoutName;
        this.date = date;
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", workoutName, category, date);
    }
}
