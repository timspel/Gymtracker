package model;

import javafx.scene.image.Image;

public class Friend {

    private int id;
    private String name;
    private Image image;

    public Friend(int id, String name, Image profileImage){
        this.id = id;
        this.name = name;
        this.image = profileImage;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }
}

