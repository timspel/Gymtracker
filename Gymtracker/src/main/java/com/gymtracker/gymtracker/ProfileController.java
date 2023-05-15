package com.gymtracker.gymtracker;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
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

   public void buttonHandler(ActionEvent event){ //Method that handles all the button presses.
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
      if(event.getSource() == editInfoButton){ //When user presses the edit button next to weight and height.
         if(editInfoButton.getText() != "Save") { //If the button says "Edit"
            editInfoButton.setText("Save");
            heightField.setEditable(true);
            weightField.setEditable(true);
            weightField.setStyle("-fx-background-color: WHITE;");
            heightField.setStyle("-fx-background-color: WHITE;");
         }
         else { //IF the button says "Save"
            editInfoButton.setText("Edit");
            heightField.setEditable(false);
            weightField.setEditable(false);
            weightField.setStyle("-fx-background-color: TRANSPARENT;");
            heightField.setStyle("-fx-background-color: TRANSPARENT;");
            if(Double.parseDouble(weightField.getText()) != weight && Double.parseDouble(heightField.getText()) != height) { //Checks if both fields are empty
               setWeightHeight(); //Calls method that updates the users weight and height.
            }
         }
      }
      if(event.getSource() == changeButton){ //Handles the events when user presses Change button.
         if(changeButton.getText() != "Save") { //Enables the user to edit the image url.
            pictureUrlField.setVisible(true);
            changeButton.setText("Save");
            lblUrlTip.setVisible(true);
         }
         else { //When the user wants to save the changes.
            if(!pictureUrlField.getText().isEmpty()) { //IF the URL field isn't empty.
               pictureUrlField.setVisible(false);
               changeButton.setText("Change");
               setProfileImage(); //Calls the method that updates the profile picture in the Database.
            }
            else { //IF textfield is empty nothing is updated or sent to the database.
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
   } //Returns the loaded FXML Parent

   private void loadFXML() throws IOException{ //Loads FXML file and assigns it to parent variable.
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfilePane.fxml"));
      loader.setController(this); //Sets the current insance as the controller class for the FXML interface.
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
      //Sets the animation boundaries and effect.
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
      }); //End of animation settings
   }
   private void showProfilePic(){
      System.out.println(profilePicture);
      if (!profilePicture.isEmpty()) {
         profileImage.setImage(new Image(profilePicture));
      } else {
         profileImage.setImage(new Image("icon.png"));
      }
   }
   private void setProfileImage(){ //Handles requests to update the users profile picture
      PreparedStatement stmt = null;
      try (Connection con = Database.getDatabase()) {
         con.setAutoCommit(false);
         System.out.println("Database Connected.");

         String sql = ("UPDATE \"User\" SET profile_picture = ? WHERE user_id = ?");
         String path = pictureUrlField.getText(); //Gets the url from the textfield
         stmt = con.prepareStatement(sql);
         stmt.setString(1, path);
         stmt.setInt(2, userId);

         stmt.executeUpdate(); //Pushing the update
         stmt.close(); //Closing the statement
         con.commit(); //Committing the UPDATE.
         con.close(); //Closing the connection.
         lblUrlTip.setVisible(false); //Hides the label showing the tip.
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
      profileImage.setImage(new Image(pictureUrlField.getText()));
   }

   private void setWeightHeight() {
      PreparedStatement stmt = null;
      try (Connection con = Database.getDatabase()) {
         con.setAutoCommit(false);
         System.out.println("Database Connected.");

         String sql = ("UPDATE \"User\" SET height = ?, weight = ? WHERE user_id = ?");
         stmt = con.prepareStatement(sql);
         stmt.setDouble(1, Double.parseDouble(heightField.getText()));
         stmt.setDouble(2, Double.parseDouble(weightField.getText()));
         stmt.setInt(3, userId);

         stmt.executeUpdate();
         stmt.close();
         con.commit();
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
   }
   private void getUserInfo(){ //Method that fetches the logged in users information.
      PreparedStatement stmt = null;
      try (Connection con = Database.getDatabase()) {
         con.setAutoCommit(false);
         System.out.println("Database Connected.");
         String sql = ("SELECT username, weight, height, profile_picture, personal_goal FROM \"User\" WHERE user_id = ?");
         stmt = con.prepareStatement(sql);
         stmt.setInt(1, userId);

         ResultSet result = stmt.executeQuery(); //Executing the SQL query
         if (result.next()){ //Fetching results
            username = result.getString("username");
            height = result.getDouble("height");
            weight = result.getDouble("weight");
            profilePicture = result.getString("profile_picture");
            goalsText.setText(result.getString("personal_goal"));
         }

         stmt.close();
         con.commit();
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
   }
}
