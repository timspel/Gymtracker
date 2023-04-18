package model;

public class ExerciseWorkoutTab {
    private String exerciseName;
    private String set1;
    private String set2;
    private String set3;
    private String set4;
    private String set5;

    public ExerciseWorkoutTab(String exerciseName, String set1, String set2, String set3, String set4, String set5) {
        this.exerciseName = exerciseName;
        this.set1 = set1;
        this.set2 = set2;
        this.set3 = set3;
        this.set4 = set4;
        this.set5 = set5;
    }

    public ExerciseWorkoutTab(String exerciseName, String set1, String set2, String set3, String set4) {
        this.exerciseName = exerciseName;
        this.set1 = set1;
        this.set2 = set2;
        this.set3 = set3;
        this.set4 = set4;
    }

    public ExerciseWorkoutTab(String exerciseName, String set1, String set2, String set3) {
        this.exerciseName = exerciseName;
        this.set1 = set1;
        this.set2 = set2;
        this.set3 = set3;
    }

    public ExerciseWorkoutTab(String exerciseName, String set1, String set2) {
        this.exerciseName = exerciseName;
        this.set1 = set1;
        this.set2 = set2;
    }

    public ExerciseWorkoutTab(String exerciseName, String set1) {
        this.exerciseName = exerciseName;
        this.set1 = set1;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
    public void setSet1(String set1) {
        this.set1 = set1;
    }
    public void setSet2(String set2) {
        this.set2 = set2;
    }
    public void setSet3(String set3) {
        this.set3 = set3;
    }
    public void setSet4(String set4) {
        this.set4 = set4;
    }
    public void setSet5(String set5) {
        this.set5 = set5;
    }

    public String getExerciseName() {
        return exerciseName;
    }
    public String getSet1() {
        return set1;
    }
    public String getSet2() {
        return set2;
    }
    public String getSet3() {
        return set3;
    }
    public String getSet4() {
        return set4;
    }
    public String getSet5() {
        return set5;
    }
}
