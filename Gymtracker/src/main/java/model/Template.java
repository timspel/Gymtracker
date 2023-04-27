package model;

public class Template {
    private String workoutName;
    private String description;
    private String category;

    public Template(String workoutName, String description, String category){
        this.workoutName = workoutName;
        this.description = description;
        this.category = category;
    }

    public void setWorkoutName(String workoutName){
        this.workoutName = workoutName;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setCategory(String category){
        this.category = category;
    }

    public String getWorkoutName(){
        return workoutName;
    }
    public String getDescription(){
        return description;
    }
    public String getCategory(){
        return category;
    }
}
