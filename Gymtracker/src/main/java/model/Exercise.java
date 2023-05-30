package model;

import javafx.scene.image.Image;

/**
 * A class that stores values from the database as objects, allowing for easier and more frequent access than what
 * the database allows
 */
public class Exercise {
    private int id;
    private String name;
    private String description;
    private Image image;
    private String muscleGroup;

    public Exercise(int id, String name, String description, Image exerciseImage, String muscleGroup){
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

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    /**
     * Necessary as it determines the formatting of the exercises names in the program
     * @return Name of exercise as String
     */
    @Override
    public String toString() {
        return String.format("%s", name);
    }
}
