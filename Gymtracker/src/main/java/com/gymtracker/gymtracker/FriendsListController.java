package com.gymtracker.gymtracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import model.Exercise;
import model.Friend;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendsListController{


    @FXML
    private ListView<Friend> friendsList = new ListView<>();

    private ObservableList<Friend> friends = FXCollections.observableArrayList();
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button removeFriend;
    @FXML
    private Button addFriend;
    @FXML
    private ImageView profilePicture;
    @FXML
    private ImageView searchPicture;
    private String search;

    public void searchButtonClicked(ActionEvent event) throws IOException{
        if (event.getSource() == searchButton){
            search = searchField.getText();
            searchFriends(search);
        }
        if(event.getSource() == addFriend){

        }
        if (event.getSource() == removeFriend){

        }
    }
    public void searchFriends(String search){

        Connection con = null;
        try {con = Database.getDatabase();

            PreparedStatement stmt = con.prepareStatement("SELECT username FROM \"User\" WHERE username = ?"); {
                stmt.setString(1, search + "%"); // Set the first parameter of the prepared statement to the search string + a wildcard
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String username = rs.getString("username");
                    System.out.println(username);
                }
                stmt.close();
                con.commit();
                con.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean addFriendship(int user1Id, int user2Id) {
        try (Connection con = Database.getDatabase();
             PreparedStatement pstmt = con.prepareStatement("INSERT INTO Friendship (user1_id, user2_id) VALUES (?, ?)")) {
            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);

            pstmt.executeUpdate();
            System.out.println("Friendship added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }
}