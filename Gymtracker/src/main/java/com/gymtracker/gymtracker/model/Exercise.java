package com.gymtracker.gymtracker.model;

import javafx.scene.image.Image;

public class Exercise {
    private int id;
    private String name;
    private String description;
    private Image image;
    private MuscleGroup muscleGroup;


    public Exercise(int id, String name, String description, Image exerciseImage, MuscleGroup muscleGroup){
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = exerciseImage;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public MuscleGroup getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(MuscleGroup muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    @Override
    public String toString() {
        return String.format("Muscle Group: %s | Exercise name: %s", muscleGroup, name);
    }
}
