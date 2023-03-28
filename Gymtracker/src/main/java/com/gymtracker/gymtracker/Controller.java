package com.gymtracker.gymtracker;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
@FXML
private Label coolLabel;

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      coolLabel.setText("Hejsan");
   }
}
