package model;

/**
 * A class that stores values from the database as objects, allowing for easier and more frequent access than what
 * the database allows
 */
public class ExerciseRecord {
    private int exerciseID;
    private int sets;
    private int reps;
    private int weight;

    public ExerciseRecord(int exerciseID, int sets, int reps, int weight) {
        this.exerciseID = exerciseID;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
