package model;

public class Friend {


    private int id;
    private String name;

    public double getWeight() {
        return weight;
    }

    public String getImage() {
        return image;
    }

    private double weight;

    public double getHeight() {
        return height;
    }


    private double height;
    private String image;



    public Friend(String name, int id, double weight, double height, String image){
        this.name = name;
        this.id = id;

        this.weight = weight;
        this.height = height;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}

