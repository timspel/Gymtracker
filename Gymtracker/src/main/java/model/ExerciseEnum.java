package model;

public enum ExerciseEnum {
    BenchPress,
    Squats,
    Deadlift;

    @Override
    public String toString() {
        return switch (this) {
            case BenchPress -> "Bench Press";
            case Squats -> "Squats";
            case Deadlift -> "Deadlift";
            default -> null;
        };
    }
}
