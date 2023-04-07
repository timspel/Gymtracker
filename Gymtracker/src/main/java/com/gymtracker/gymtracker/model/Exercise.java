package com.gymtracker.gymtracker.model;

import javafx.scene.image.Image;

public class Exercise {
    private String name;
    private Image exerciseImage;

    public Exercise(String name, Image exerciseImage){
        this.name = name;
        this.exerciseImage = exerciseImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getExerciseImage() {
        return exerciseImage;
    }

    public void setExerciseImage(Image exerciseImage) {
        this.exerciseImage = exerciseImage;
    }

    @Override
    public String toString() {
        return String.format("Exercise name: %s", name);
    }
}
