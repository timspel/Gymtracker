package com.gymtracker.gymtracker;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileController {
   //<editor-fold desc="Instance Variables">
   @FXML
   private Button editButton;
   @FXML
   private TextArea goalsText;
   @FXML
   private Button changeButton;
   @FXML
   private Button editInfoButton;
   @FXML
   private Button darkmodeButton;
   @FXML
   private TextField weightField;
   @FXML
   private TextField heightField;
   @FXML
   private TextField pictureUrlField;
   @FXML
   private Label lblName;
   @FXML
   private AnchorPane backgroundPane;
   @FXML
   private Button show;
   @FXML
   private Button hide;
   @FXML
   private AnchorPane slider;
   @FXML
   private ImageView profileImage;
   @FXML
   private Label lblUrlTip;

   private Parent profilePane;
   private MainController mainController;

   private int userId;
   private String username;
   private double height;
   private double weight;
   private String profilePicture;
   private boolean toggle;
   private String defaultStyle;
   //</editor-fold
   public ProfileController(MainController mainController) throws IOException {
      this.mainController = mainController;
      loadFXML();
      userId = Singleton.getInstance().getUserId();

      getUserInfo();
      showProfilePic();
      initComponents();
   }

   public void buttonHandler(ActionEvent event){
      if(event.getSource() == editButton){
         if(editButton.getText() != "Save") {
            editButton.setText("Save");
            goalsText.setEditable(true);
         }
         else {
            goalsText.setEditable(false);
            editButton.setText("Edit");
            Connection conn = null;
            try{
               conn = Database.getDatabase();
               PreparedStatement stmt = conn.prepareStatement("UPDATE \"User\" SET personal_goal = ? WHERE user_id = ? ");
               stmt.setString(1, goalsText.getText());
               stmt.setInt(2, userId);
               stmt.executeUpdate();
               stmt.close();
               conn.close();
            }catch (Exception e){e.printStackTrace();}
         }
      }
      if(event.getSource() == editInfoButton){
         if(editInfoButton.getText() != "Save") {
            editInfoButton.setText("Save");
            heightField.setEditable(true);
            weightField.setEditable(true);
            weightField.setStyle("-fx-background-color: WHITE;");
            heightField.setStyle("-fx-background-color: WHITE;");
         }
         else {
            editInfoButton.setText("Edit");
            heightField.setEditable(false);
            weightField.setEditable(false);
            weightField.setStyle("-fx-background-color: TRANSPARENT;");
            heightField.setStyle("-fx-background-color: TRANSPARENT;");
            if(Double.parseDouble(weightField.getText()) != weight && Double.parseDouble(heightField.getText()) != height) {
               System.out.println("Info changed.");
               Connection con = null;
               PreparedStatement stmt = null;
               try {
                  con = Database.getDatabase();
                  con.setAutoCommit(false);
                  System.out.println("Database Connected.");
                  System.out.println(userId);
                  String sql = ("UPDATE \"User\" SET height = ?, weight = ? WHERE user_id = ?");
                  stmt = con.prepareStatement(sql);
                  String path = pictureUrlField.getText();
                  System.out.println(pictureUrlField.getText());
                  stmt.setDouble(1, Double.parseDouble(heightField.getText()));
                  stmt.setDouble(2, Double.parseDouble(weightField.getText()));
                  stmt.setInt(3, userId);

                  stmt.executeUpdate();
                  stmt.close();
                  con.commit();
                  con.close();
               } catch (Exception e) {
                  e.printStackTrace();
                  System.err.println(e.getClass().getName() + ": " + e.getMessage());
               }
            }
         }
      }
      if(event.getSource() == changeButton){ //Handles the events when user presses Change button.
         if(changeButton.getText() != "Save") {
            pictureUrlField.setVisible(true);
            changeButton.setText("Save");
            lblUrlTip.setVisible(true);
         }
         else {
            if(!pictureUrlField.getText().isEmpty()) {
               pictureUrlField.setVisible(false);
               changeButton.setText("Change");
               Connection con = null;
               PreparedStatement stmt = null;
               try {
                  con = Database.getDatabase();
                  con.setAutoCommit(false);
                  System.out.println("Database Connected.");
                  System.out.println(userId);
                  String sql = ("UPDATE \"User\" SET profile_picture = ? WHERE user_id = ?");
                  stmt = con.prepareStatement(sql);
                  String path = pictureUrlField.getText();
                  System.out.println(pictureUrlField.getText());
                  stmt.setString(1, path);
                  stmt.setInt(2, userId);


                  stmt.executeUpdate();
                  stmt.close();
                  con.commit();
                  con.close();
                  lblUrlTip.setVisible(false);
               } catch (Exception e) {
                  e.printStackTrace();
                  System.err.println(e.getClass().getName() + ": " + e.getMessage());
               }
               profileImage.setImage(new Image(pictureUrlField.getText()));
            }
            else {
               pictureUrlField.setVisible(false);
               changeButton.setText("Edit");
               lblUrlTip.setVisible(false);
            }
         }
      }
      if(event.getSource() == darkmodeButton){
         if(toggle == true){
            toggle = false;
            backgroundPane.setStyle(defaultStyle);
            darkmodeButton.setText("On");
         }
         else {
            backgroundPane.setStyle("-fx-background-color: BLACK");
            darkmodeButton.setText("Off");
            toggle = true;
         }
      }
   }

   public Parent getParent(){
      return profilePane;
   }

   private void loadFXML() throws IOException{ //Loads FXML file and assigns it to parent variable.
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfilePane.fxml"));
      loader.setController(this);
      profilePane = loader.load();
   }

   private void initComponents(){ //Initializes the components such as buttons and text fields.
      goalsText.setEditable(false);
      weightField.setEditable(false);
      weightField.setStyle("-fx-background-color: TRANSPARENT;");
      heightField.setEditable(false);
      heightField.setStyle("-fx-background-color: TRANSPARENT;");
      pictureUrlField.setVisible(false);
      lblUrlTip.setVisible(false);
      editButton.setOnAction(l -> buttonHandler(l));
      editInfoButton.setOnAction(l -> buttonHandler(l));
      changeButton.setOnAction(l -> buttonHandler(l));
      darkmodeButton.setOnAction(l -> buttonHandler(l));
      changeButton.setOnAction(l -> buttonHandler(l));
      toggle = false;
      lblName.setText(username);
      heightField.setText("" + height);
      weightField.setText("" + weight);
      defaultStyle = backgroundPane.getStyle();
      slider.setTranslateX(-658);
      show.setOnAction(event -> {
         TranslateTransition slide = new TranslateTransition();
         slide.setDuration(Duration.seconds(0.4));
         slide.setNode(slider);
         slide.setToX(0);
         slide.play();

         slider.setTranslateX(-658);

         slide.setOnFinished(event1 -> {
            show.setVisible(false);
            hide.setVisible(true);
         });
      });
      hide.setOnAction(event -> {
         TranslateTransition slide = new TranslateTransition();
         slide.setDuration(Duration.seconds(0.4));
         slide.setNode(slider);
         slide.setToX(-658);
         slide.play();
         slider.setTranslateX(0);

         slide.setOnFinished(event1 -> {
            show.setVisible(true);
            hide.setVisible(false);
         });
      });
   }
   private void showProfilePic(){
      System.out.println(profilePicture);
      if (!profilePicture.isEmpty()) {
         profileImage.setImage(new Image(profilePicture));
      } else {
         profileImage.setImage(new Image("icon.png"));
      }
   }
   private void getUserInfo(){
      Connection con = null;
      PreparedStatement stmt = null;
      try {con = Database.getDatabase();
         con.setAutoCommit(false);
         System.out.println("Database Connected.");
         System.out.println(userId);
         String sql = ("SELECT username, weight, height, profile_picture FROM \"User\" WHERE user_id = ?");
         stmt = con.prepareStatement(sql);
         stmt.setInt(1, userId);

         ResultSet result = stmt.executeQuery();
         if (result.next()){
            username = result.getString("username");
            height = result.getDouble("height");
            weight = result.getDouble("weight");
            profilePicture = result.getString("profile_picture");
         }

         stmt.close();
         con.commit();
         con.close();

      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
   }
}
