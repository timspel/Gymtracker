package model;

import javafx.scene.image.Image;

public class Exercise {
    private int id;
    private String name;
    private String description;
    private Image picture;
    private String muscleGroup;

    public Exercise(int id, String name, String description, Image exercisePicture, String muscleGroup){
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = exercisePicture;
        this.muscleGroup = muscleGroup;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getPicture() {
        return picture;
    }

    public void setPicture(Image picture) {
        this.picture = picture;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    @Override
    public String toString() {
        return String.format("%s | %s", muscleGroup, name);
    }
}
