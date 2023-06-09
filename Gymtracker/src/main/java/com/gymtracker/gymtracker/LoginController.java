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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
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
   @FXML
   private Label lblStatus;

   private int userId;
   private Stage stage;
   private Scene scene;
   private String username;
   private static HashMap<String, String> allUsers = new HashMap<>();
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
      if(event.getSource() == loginButton){ //IF user pressed the Login button.
         lblStatus.setText("");
         handleLogin(event);
      }
      if(event.getSource() == registerButton){ //IF user pressed the Register button.
         lblStatus.setText("");
         registerPane.setVisible(true);
         registerPane.setTranslateX(-204);
         TranslateTransition slide = new TranslateTransition();
         slide.setDuration(Duration.seconds(0.2));
         slide.setNode(registerPane);
         slide.setToX(0);
         slide.play();

         registerPane.setTranslateX(-204);
      }
      if(event.getSource() == regUserButton){ //IF user pressed the Register button on the register new user panel.
         addUser();
         TranslateTransition slide = new TranslateTransition();
         slide.setDuration(Duration.seconds(0.2));
         slide.setNode(registerPane);
         slide.setToX(-204);
         slide.play();
         registerPane.setTranslateX(0);
      }
      if(event.getSource() == cancelButton){ //IF user pressed the Cancel button.
         TranslateTransition slide = new TranslateTransition();
         slide.setDuration(Duration.seconds(0.2));
         slide.setNode(registerPane);
         slide.setToX(-204);
         slide.play();
         registerPane.setTranslateX(0);
      }
   }

   private void handleLogin(ActionEvent event){
      if (loginAttempt()){
         PreparedStatement stmt = null;
         try (Connection con = Database.getDatabase()) {
            String sql = ("SELECT user_id FROM \"User\" WHERE username = ? AND password = ?");

            stmt = con.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2, allUsers.get(username));

            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
               userId = resultSet.getInt("user_id");
            }
            Singleton.getInstance().setUserId(userId);
            login(event);
         }
         catch (Exception e){e.printStackTrace();}
      }
   }
   public boolean loginAttempt(){
      boolean loginSuccess = false;
      if(loginCustomer(loginUsernameTextField.getText(), loginPasswordField.getText())){
         loginSuccess = true;
      }
      else {
         lblStatus.setText("Wrong Username or Password!");
      }
      return loginSuccess;
   }

   public boolean loginCustomer(String username, String password) {
      boolean success = false;
      if(allUsers.containsKey(username)){
         if(password.equals(allUsers.get(username))){
            this.username = username;
            success = true;
            lblStatus.setText("Login Successful!");
         }
      }
      return success;
   }

   public void addUser(){
      boolean registered = false;

      if(registerPasswordField1.getText().equals(registerPasswordField2.getText())){
         
          registered = newUserRegistered(registerUsernameTextField.getText(),registerPasswordField1.getText(),0, 0, "https://t3.ftcdn.net/jpg/03/46/83/96/360_F_346839683_6nAPzbhpSkIpb8pmAwufkC7c5eD7wYws.jpg");
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
            getAllUsers();
            System.out.println("User added successfully");
         }
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
         return false;
      }
      return true;
   }

   private static void getAllUsers(){
      PreparedStatement stmt = null;
      try (Connection conn = Database.getDatabase()){
         conn.setAutoCommit(false);
         String sql = ("SELECT username, password FROM \"User\"");
         stmt = conn.prepareStatement(sql);
         ResultSet result = stmt.executeQuery();

         while (result.next()){
            allUsers.put(result.getString("username"),result.getString("password"));
         }
         System.out.println("All users have been fetched.");
         stmt.close();
         conn.commit();

      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
   }

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      registerPane.setVisible(false);
      getAllUsers();
      loginPasswordField.setOnKeyPressed( event -> {
         if( event.getCode() == KeyCode.ENTER ) {
            loginButton.fire();
         }
      } );
      loginButton.setOnKeyPressed(event -> {
         if(event.getCode() == KeyCode.ENTER){
            loginButton.fire();
         }
      });
   }
}
