package com.gymtracker.gymtracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import model.Friend;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsListController{


    @FXML
    private TableView<String> friendsList = new TableView<>();

    private ObservableList<String> friends = FXCollections.observableArrayList();
    @FXML
    private TableView<Friend> pendingList = new TableView<>();
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
    private int userId;
    private int userIdFriend;

    public void searchButtonClicked(ActionEvent event) throws IOException{
        if (event.getSource() == searchButton){
            search = searchField.getText();
            searchFriends(search);
        }
        if(event.getSource() == addFriend){
            userId = Singleton.getInstance().getUserId();
            System.out.println(userId);
            System.out.println(userIdFriend);
            addFriendship(userId,userIdFriend);
        }
        if (event.getSource() == removeFriend){

        }
    }
    public void searchFriends(String search){

        Connection con = null;
        try {con = Database.getDatabase();
            //con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement("SELECT user_id, username FROM \"User\" WHERE username = ?"); {
                stmt.setString(1, search); // Set the first parameter of the prepared statement to the search string + a wildcard
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String username = rs.getString("username");
                    System.out.println(username);
                    //populateFriendsList(username);
                    userIdFriend = rs.getInt("user_id");
                    System.out.println(userIdFriend);

                }
                stmt.close();
                //con.commit();
                con.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean addFriendship(int user1Id, int user2Id) {
        try (Connection con = Database.getDatabase();
             PreparedStatement pstmt = con.prepareStatement("INSERT INTO Friendship (user1_id, user2_id, status) VALUES (?, ?, 'pending') ON CONFLICT (user1_id, user2_id) DO NOTHING")) {
            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Friend request sent successfully");
                return true;
            } else {
                System.out.println("Friend request already exists or an error occurred");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean acceptFriendRequest(int friendshipId) {
        try (Connection con = Database.getDatabase();
             PreparedStatement pstmt = con.prepareStatement("UPDATE Friendship SET status = 'accepted' WHERE friendship_id = ?")) {
            pstmt.setInt(1, friendshipId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 1) {
                System.out.println("Friendship request accepted successfully");
                return true;
            } else {
                System.out.println("Error: friendship request not found");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    public List<String> getPendingFriendRequests(int userId) {
        List<String> friendRequests = new ArrayList<>();
        try (Connection con = Database.getDatabase();
             PreparedStatement pstmt = con.prepareStatement("SELECT f.friendship_id, u.username " +
                     "FROM Friendship f " +
                     "INNER JOIN \"User\" u ON (f.user1_id = u.user_id AND f.user2_id = ? AND f.status = 'pending')")) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int friendshipId = rs.getInt("friendship_id");
                String username = rs.getString("username");
                friendRequests.add("Friendship ID: " + friendshipId + ", From user: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendRequests;
    }

}