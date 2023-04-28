package com.gymtracker.gymtracker;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ButtonSkin extends javafx.scene.control.skin.ButtonSkin {
   public ButtonSkin(Button control) {
      super(control);
      control.setOnMouseEntered(e -> {
         control.setStyle("-fx-background-color: #0000007a;");
      });
      control.setOnMouseExited(e -> {
         control.setStyle("-fx-background-color: TRANSPARENT;");
      });
   }
}
