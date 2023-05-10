package model;

public class Template {
    private String templateName;
    private String description;
    private String category;

    public Template(String workoutName, String description, String category){
        this.templateName = workoutName;
        this.description = description;
        this.category = category;
    }

    public void setTemplateName(String workoutName){
        this.templateName = workoutName;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setCategory(String category){
        this.category = category;
    }

    public String getTemplateName(){
        return templateName;
    }
    public String getDescription(){
        return description;
    }
    public String getCategory(){
        return category;
    }
}
