package com.gymtracker.gymtracker;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class Controller implements Initializable {
@FXML
private Button workoutsButton;
   public void enter(ActionEvent e){
      workoutsButton.setStyle("-fx-background-color: #0000007a;");
   }
   public void exit(ActionEvent e){
      workoutsButton.setStyle("-fx-background-color: transparent;");
   }
   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {

   }
}
