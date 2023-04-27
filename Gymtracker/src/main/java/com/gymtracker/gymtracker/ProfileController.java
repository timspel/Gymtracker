package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileController {
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
   private Label lblName;
   @FXML
   private AnchorPane backgroundPane;

   private Parent profilePane;
   private MainController mainController;
   private int userId;
   private String username;
   private double height;
   private double weight;
   private boolean toggle;
   private String defaultStyle;
   public ProfileController(MainController mainController) throws IOException {
      loadFXML();
      userId = UserIdSingleton.getInstance().getUserId();

      getUserInfo();
      initComponents();
   }

   public Parent getParent(){
      return profilePane;
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
         }
      }
      if(event.getSource() == editInfoButton){
         if(editInfoButton.getText() != "Save") {
            editInfoButton.setText("Save");
            heightField.setEditable(true);
            weightField.setEditable(true);
         }
         else {
            editInfoButton.setText("Edit");
            heightField.setEditable(false);
            weightField.setEditable(false);
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
   private void loadFXML() throws IOException{ //Loads FXML file and assigns it to parent variable.
      this.mainController = mainController;
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfilePane.fxml"));
      loader.setController(this);
      profilePane = loader.load();
   }

   private void initComponents(){
      goalsText.setEditable(false);
      weightField.setEditable(false);
      heightField.setEditable(false);
      editButton.setOnAction(l -> buttonHandler(l));
      editInfoButton.setOnAction(l -> buttonHandler(l));
      changeButton.setOnAction(l -> buttonHandler(l));
      darkmodeButton.setOnAction(l -> buttonHandler(l));
      toggle = false;
      lblName.setText(username);
      heightField.setText("" + height);
      weightField.setText("" + weight);
      defaultStyle = backgroundPane.getStyle();
   }

   private void getUserInfo(){
      Connection con = null;
      PreparedStatement stmt = null;
      try {con = Database.getDatabase();
         con.setAutoCommit(false);
         System.out.println("Opened database successfully");
         System.out.println(userId);
         String sql = ("SELECT username FROM \"User\" WHERE user_id = ?");
         stmt = con.prepareStatement(sql);
         stmt.setInt(1, userId);

         ResultSet result = stmt.executeQuery();
         if (result.next()){
            username = result.getString("username");
         }
         String query = ("SELECT weight FROM \"User\" WHERE user_id = ?");
         stmt = con.prepareStatement(query);
         stmt.setInt(1, userId);
         result = stmt.executeQuery();
         if (result.next()){
            weight = result.getDouble("weight");
         }
         String query2 = ("SELECT height FROM \"User\" WHERE user_id = ?");
         stmt = con.prepareStatement(query2);
         stmt.setInt(1, userId);
         result = stmt.executeQuery();
         if (result.next()){
            height = result.getDouble("height");
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
