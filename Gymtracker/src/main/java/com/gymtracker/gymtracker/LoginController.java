package com.gymtracker.gymtracker;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
   private Stage stage;
   private Scene scene;
   @FXML
   private Button loginButton;
   @FXML
   private Button registerButton;
   @FXML
   private Button regUserButton;
   @FXML
   private Button cancelButton;
   @FXML
   private AnchorPane registerPane;
   @FXML
   private TextField registerUsernameTextField;
   @FXML
   private TextField registerPasswordField1;
   @FXML
   private TextField registerPasswordField2;


   @FXML
   private TextField loginUsernameTextField;
   @FXML
   private TextField loginPasswordField;

   private int userId;

   public void login(ActionEvent e) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource("MainFrame.fxml"));
      stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene (scene);
      stage.setX(0);
      stage.setY(0);
      stage.show();
   }

   public void buttonClicked(ActionEvent event) throws IOException {
      if(event.getSource() == loginButton){

        if (loginAttempt()){

           login(event);
        }
      }
      if(event.getSource() == registerButton){
         registerPane.setVisible(true);
         registerPane.setTranslateX(-204);
         TranslateTransition slide = new TranslateTransition();
         slide.setDuration(Duration.seconds(0.4));
         slide.setNode(registerPane);
         slide.setToX(0);
         slide.play();

         registerPane.setTranslateX(-204);
      }
      if(event.getSource() == regUserButton){
         TranslateTransition slide = new TranslateTransition();
         slide.setDuration(Duration.seconds(0.4));
         slide.setNode(registerPane);
         slide.setToX(-204);
         slide.play();
         registerPane.setTranslateX(0);
         //registerPane.setVisible(false);
         addUser();
      }
      if(event.getSource() == cancelButton){
         TranslateTransition slide = new TranslateTransition();
         slide.setDuration(Duration.seconds(0.4));
         slide.setNode(registerPane);
         slide.setToX(-204);
         slide.play();
         registerPane.setTranslateX(0);
      }
   }

   public boolean loginAttempt(){

      if(loginCustomer(loginUsernameTextField.getText(), loginPasswordField.getText())){
         UserIdSingleton.getInstance().setUserId(userId);
         return true;
      }
      System.out.println("failed to login");
      return false;
   }

   public boolean loginCustomer(String username, String password) {

      Connection con = null;
      PreparedStatement stmt = null;

      try {con = Database.getDatabase();
         con.setAutoCommit(false);
         System.out.println("Opened database successfully");

         String sql = ("SELECT user_id FROM \"User\" WHERE username = ? AND password = ?");
         stmt = con.prepareStatement(sql);
         stmt.setString(1,username);
         stmt.setString(2,password);

         ResultSet result = stmt.executeQuery();
         if (result.next()){
            userId = result.getInt("user_id");
            System.out.println("You have logged in");
            System.out.printf("Username: %s", username);
            System.out.println("");
         }
         else {
            System.out.println("Wrong username or password");
            return false;

         }
         stmt.close();
         con.commit();
         con.close();

      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
         return false;
      }
      return true;
   }


   public void addUser(){
      boolean registered = false;

      if(registerPasswordField1.getText().equals(registerPasswordField2.getText())){
         
          registered = newUserRegistered(registerUsernameTextField.getText(),registerPasswordField1.getText(),0, 0, "");
      }else{
         System.out.println("password does not match");
      }

      if(!registered){
         System.out.println("couldnt register");

      }


   }

   public static boolean newUserRegistered(String username, String password, double weight, double height, String profilePicture) {
      try (Connection con = Database.getDatabase()) {
         // Check if the username already exists
         try (PreparedStatement checkStmt = con.prepareStatement("SELECT username FROM \"User\" WHERE username = ?")) {
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
               if (rs.next()) {
                  // If a row is returned, the username already exists
                  System.out.println("Username already exists in the database");
                  return false;
               }
            }
         }

         // If the username doesn't exist, insert the new user
         try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO \"User\" (username, password, weight, height, profile_picture) VALUES (?, ?, ?, ?, ?)")) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setDouble(3, weight);
            insertStmt.setDouble(4, height);
            insertStmt.setString(5, profilePicture);

            insertStmt.executeUpdate();
            System.out.println("User added successfully");
         }
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
         return false;
      }
      return true;
   }






   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      registerPane.setVisible(false);
   }
}
