package model;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkoutManager {
    private HashMap<ExerciseEnum, ArrayList<Set>> exercises;
    private ArrayList<Set> sets;

    public void addExercise(ExerciseEnum exercise, ArrayList<Set> sets){
        exercises.put(exercise, sets);
    }

    public void addSet(Set set){
        sets.add(set);
    }

    public ArrayList<Set> getSets(){
        return sets;
    }

    public HashMap<ExerciseEnum, ArrayList<Set>> getExercises(){
        return exercises;
    }
}
