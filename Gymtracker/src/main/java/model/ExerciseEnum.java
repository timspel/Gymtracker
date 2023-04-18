package model;

public enum ExerciseEnum {
    BenchPress,
    Squats,
    Deadlift;

    @Override
    public String toString() {
        switch (this){
            case BenchPress:
                return "Bench Press";
            case Squats:
                return "Squats";
            case Deadlift:
                return "Deadlift";
            default:
                return null;
        }
    }
}
