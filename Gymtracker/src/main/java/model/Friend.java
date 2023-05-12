package model;

import javafx.scene.image.Image;

public class Friend {

    private int id;
    private String name;
    private Image image;

    public Friend(String name, int id){
        this.name = name;
        this.id = id;

    }

    public String getName() {
        return name;
    }


}

