package com.gymtracker.gymtracker;

import javafx.scene.control.ScrollPane;

/**
 * The Singleton class represents a singleton object that holds a single instance of the class throughout the application.
 * @author Samuel Carlsson
 */
public class Singleton {
    private static Singleton instance;
    private int userId;
    private ScrollPane workoutScroll;

    /**
     * Private constructor to prevent instantiation of the Singleton class from outside.
     */
    private Singleton(){}

    /**
     * Retrieves the instance of the Singleton class.
     * If the instance does not exist, a new instance is created.
     * @return the instance of the Singleton class
     */
    public static synchronized Singleton getInstance(){
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }

    /**
     * Retrieves the user ID stored in the Singleton instance.
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID in the Singleton instance.
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the workout scroll pane stored in the Singleton instance.
     * @return the workout scroll pane
     */
    public ScrollPane getWorkoutScroll() {
        return workoutScroll;
    }

    /**
     * Sets the workout scroll pane in the Singleton instance.
     * @param workoutScroll the workout scroll pane to set
     */
    public void setWorkoutScroll(ScrollPane workoutScroll) {
        this.workoutScroll = workoutScroll;
    }
}
