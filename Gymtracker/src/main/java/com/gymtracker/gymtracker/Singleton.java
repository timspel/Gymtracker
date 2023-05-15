package com.gymtracker.gymtracker;

import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class Singleton {
    private static Singleton instance;
    private int userId;
    private ScrollPane workoutScroll;

    private Singleton(){}

    public static synchronized Singleton getInstance(){
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ScrollPane getWorkoutScroll() {
        return workoutScroll;
    }

    public void setWorkoutScroll(ScrollPane workoutScroll) {
        this.workoutScroll = workoutScroll;
    }
}
