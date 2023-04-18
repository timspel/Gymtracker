package model;

public class Template {
    private String workoutName;
    private String date;
    private String category;

    public Template(String workoutName, String date, String category){
        this.workoutName = workoutName;
        this.date = date;
        this.category = category;
    }

    public void setWorkoutName(String workoutName){
        this.workoutName = workoutName;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setCategory(String category){
        this.category = category;
    }

    public String getWorkoutName(){
        return workoutName;
    }
    public String getDate(){
        return date;
    }
    public String getCategory(){
        return category;
    }
}
