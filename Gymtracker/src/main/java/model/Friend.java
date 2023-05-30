package model;

/**
 * This class is a Friends object that us used in the FriendsListController.
 * @author Villie Brandt
 */
public class Friend {


    private int id;
    private String name;
    private double height;
    private String image;
    private double weight;

    /**
     * Constructor for the friendsobject with name, id, weight, height and an image.
     * @param name The name of the user.
     * @param id The id of the user.
     * @param weight The weight of the user.
     * @param height The height of the user.
     * @param image The image of the user.
     */
    public Friend(String name, int id, double weight, double height, String image){
        this.name = name;
        this.id = id;

        this.weight = weight;
        this.height = height;
        this.image = image;
    }

    /**
     * get method for weight.
     * @return users weight.
     */
    public double getWeight() {
        return weight;
    }
    /**
     * get method for image.
     * @return users image.
     */
    public String getImage() {
        return image;
    }
    /**
     * get method for height.
     * @return users height.
     */
    public double getHeight() {
        return height;
    }
    /**
     * get method for name.
     * @return users name.
     */
    public String getName() {
        return name;
    }
    /**
     * get method for id.
     * @return users id.
     */
    public int getId() {
        return id;
    }

}
