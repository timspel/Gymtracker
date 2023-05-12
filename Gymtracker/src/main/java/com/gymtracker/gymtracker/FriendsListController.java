package com.gymtracker.gymtracker;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Friend;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FriendsListController implements Initializable {


    @FXML
    private TableView<Friend> friendsList = new TableView<>();
    @FXML
    private TableColumn<Friend,String> friendsColumn;
    private List<Friend> friendsArrayList = new ArrayList<>();
    @FXML
    private TableView<Friend> pendingList = new TableView<>();
    @FXML
    private TableColumn<Friend,String> pendingColumn;
    private List<Friend> pendingArrayList;
    @FXML
    private Label profileNameAdd;
    @FXML
    private ImageView searchPicture;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button removeFriend;
    @FXML
    private Button acceptFriend;
    @FXML
    private Button addFriend;
    @FXML
    private Button removeFriendRequest;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Text selectedUsername;
    @FXML
    private Text userWeight;
    @FXML
    private Text userHeight;
    private String search;
    private int userId;
    private int userIdFriend;
    private String  userFriendPicture;
    private  String friendUsername;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        friendsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        friendsArrayList = new ArrayList<>();
        populateFriendsList(UserIdSingleton.getInstance().getUserId());

        pendingColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Set cell value factory for the pendingColumn
        pendingArrayList = new ArrayList<>();
        populatePendingFriendRequest(UserIdSingleton.getInstance().getUserId());


        friendsList.setItems(FXCollections.observableArrayList(friendsArrayList));
        pendingList.setItems(FXCollections.observableArrayList(pendingArrayList)); // Set the items for pendingList


        pendingList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Friend selectedFriend = pendingList.getSelectionModel().getSelectedItem();
                setSelectedFriendInformation(selectedFriend.getName(), selectedFriend.getWeight(), selectedFriend.getHeight(), selectedFriend.getImage());
            }
        });

        friendsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Friend selectedFriend = friendsList.getSelectionModel().getSelectedItem();
                setSelectedFriendInformation(selectedFriend.getName(), selectedFriend.getWeight(), selectedFriend.getHeight(), selectedFriend.getImage());
            }
        });
    }

    public void searchButtonClicked(ActionEvent event) throws IOException{
        if (event.getSource() == searchButton){
            search = searchField.getText();
            searchFriends(search);
        }
        if(event.getSource() == addFriend){
            userId = UserIdSingleton.getInstance().getUserId();
            System.out.println(userId);
            System.out.println(userIdFriend);
            addFriendship(userId,userIdFriend);
        }
        if (event.getSource() == removeFriend){
            Friend selectedFriend = friendsList.getSelectionModel().getSelectedItem();
            if (selectedFriend != null) {
                int friendshipId = selectedFriend.getId();
                removeFriendship(friendshipId);
            }


        }
        if (event.getSource() == removeFriendRequest){
            Friend selectedFriend = pendingList.getSelectionModel().getSelectedItem();
            if (selectedFriend != null) {
                int friendshipId = selectedFriend.getId();
                removeFriendshipRequest(friendshipId, UserIdSingleton.getInstance().getUserId());
            }

        }


        if (event.getSource() == acceptFriend){
            Friend selectedFriend = pendingList.getSelectionModel().getSelectedItem();
            if (selectedFriend != null) {
                int friendshipId = selectedFriend.getId();
                acceptFriendship(friendshipId, UserIdSingleton.getInstance().getUserId());
            }

        }
    }
    public void searchFriends(String search){

        Connection con = null;
        try {con = Database.getDatabase();
            //con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement("SELECT user_id, username, profile_picture FROM \"User\" WHERE username = ?"); {
                stmt.setString(1, search); // Set the first parameter of the prepared statement to the search string + a wildcard
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    friendUsername = rs.getString("username");
                    System.out.println(friendUsername);
                    userIdFriend = rs.getInt("user_id");
                    System.out.println(userIdFriend);
                    userFriendPicture =  rs.getString("profile_picture");
                    setFriendInformation(friendUsername, userFriendPicture);

                }
                stmt.close();
                //con.commit();
                con.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setFriendInformation(String searchedUserName, String userFriendPicture  ){
        profileNameAdd.setText(searchedUserName);
        searchPicture.setImage(new Image(userFriendPicture));
    }
    public void setSelectedFriendInformation(String friendUsername, double weight, double height ,String image){
        selectedUsername.setText(friendUsername);
        userWeight.setText(String.valueOf(weight));
        userHeight.setText(String.valueOf(height));
        profilePicture.setImage(new Image(image));
    }

    public void addFriendship(int user1Id, int user2Id) {
        String friendCheckSQL = "SELECT COUNT(*) FROM Friendship WHERE (user1_id = ? AND user2_id = ? AND status != 'pending') OR (user1_id = ? AND user2_id = ? AND status != 'pending')";
        try (Connection con = Database.getDatabase();
             PreparedStatement pstmt = con.prepareStatement(friendCheckSQL);
             PreparedStatement stmt = con.prepareStatement("SELECT username, weight, height, profile_picture FROM \"User\" WHERE user_id = ?")) {
            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);
            pstmt.setInt(3, user2Id);
            pstmt.setInt(4, user1Id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    System.out.println("Users are already friends");
                    return;
                }
            }

            try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO Friendship (user1_id, user2_id, status) VALUES (?, ?, 'pending') ON CONFLICT (user1_id, user2_id) DO NOTHING")) {
                insertStmt.setInt(1, user1Id);
                insertStmt.setInt(2, user2Id);
                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    stmt.setInt(1, user2Id);
                    ResultSet userRs = stmt.executeQuery();
                    if (userRs.next()) {
                        String friendUsername = userRs.getString("username");
                        double weight = userRs.getDouble("weight");
                        double height = userRs.getDouble("height");
                        String image = userRs.getString("profile_picture");

                        System.out.println("Friend request sent successfully");
                        Friend friend = new Friend(friendUsername, user2Id, weight, height, image);
                        pendingArrayList.add(friend);
                        populatePendingFriendRequest(UserIdSingleton.getInstance().getUserId());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    public void populateFriendsList(int userId){
        friendsArrayList.clear(); // Clear the pendingArrayList before populating it

        try (Connection con = Database.getDatabase();
             PreparedStatement pstmt = con.prepareStatement("SELECT f.friendship_id, u.username, u.weight, u.height, u.profile_picture " +
                     "FROM Friendship f " +
                     "INNER JOIN \"User\" u ON ((f.user1_id = u.user_id OR f.user2_id = u.user_id) AND f.status = 'accepted') " +
                     "WHERE (f.user1_id = ? OR f.user2_id = ?) AND u.user_id <> ?")) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int friendshipId = rs.getInt("friendship_id");
                String username = rs.getString("username");
                double weight = rs.getDouble("weight");
                double height = rs.getDouble("height");
                String image = rs.getString("profile_picture");
                friendsArrayList.add(new Friend(username, friendshipId, weight, height, image)); // Add friend request to pendingArrayList
            }
            pendingList.setItems(FXCollections.observableArrayList(friendsArrayList)); // Set the items for pendingList
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeFriendshipRequest(int friendshipId, int userId) {
        try (Connection con = Database.getDatabase();
             PreparedStatement selectStmt = con.prepareStatement("SELECT user1_id, user2_id, status FROM Friendship WHERE friendship_id = ?")) {
            selectStmt.setInt(1, friendshipId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int user1Id = rs.getInt("user1_id");
                int user2Id = rs.getInt("user2_id");
                String status = rs.getString("status");
                if (!status.equals("pending")) {
                    System.out.println("Friend request is not in pending status");
                    return;
                } else if (user1Id != userId && user2Id != userId) {
                    System.out.println("You are not authorized to remove this friend request");
                    return;
                }
            } else {
                System.out.println("Error: friendship request not found");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        try (Connection con = Database.getDatabase();
             PreparedStatement deleteStmt = con.prepareStatement("DELETE FROM Friendship WHERE friendship_id = ?")) {
            deleteStmt.setInt(1, friendshipId);
            int rowsDeleted = deleteStmt.executeUpdate();
            if (rowsDeleted == 1) {
                System.out.println("Friendship request removed successfully");

                // Remove friend request from pending list
                Friend removedFriend = null;
                for (Friend friend : pendingArrayList) {
                    if (friend.getId() == friendshipId) {
                        removedFriend = friend;
                        break;
                    }
                }

                if (removedFriend != null) {
                    pendingArrayList.remove(removedFriend);
                    pendingList.setItems(FXCollections.observableArrayList(pendingArrayList));
                }
            } else {
                System.out.println("Error: friendship request not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void removeFriendship(int friendshipId) {
        try (Connection con = Database.getDatabase();
             PreparedStatement selectStmt = con.prepareStatement("SELECT status FROM Friendship WHERE friendship_id = ?")) {
            selectStmt.setInt(1, friendshipId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (!status.equals("accepted")) {
                    System.out.println("Friendship is not in accepted status");
                    return;
                }
            } else {
                System.out.println("Error: friendship not found");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        try (Connection con = Database.getDatabase();
             PreparedStatement deleteStmt = con.prepareStatement("DELETE FROM Friendship WHERE friendship_id = ?")) {
            deleteStmt.setInt(1, friendshipId);
            int rowsDeleted = deleteStmt.executeUpdate();
            if (rowsDeleted == 1) {
                System.out.println("Friendship removed successfully");

                // Remove friend from friends list
                Friend removedFriend = null;
                for (Friend friend : friendsArrayList) {
                    if (friend.getId() == friendshipId) {
                        removedFriend = friend;
                        break;
                    }
                }

                if (removedFriend != null) {
                    friendsArrayList.remove(removedFriend);
                    friendsList.setItems(FXCollections.observableArrayList(friendsArrayList));
                }
            } else {
                System.out.println("Error: friendship not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }




    public void acceptFriendship(int friendshipId, int userId) {
        try (Connection con = Database.getDatabase();
             PreparedStatement selectStmt = con.prepareStatement("SELECT user2_id, status FROM Friendship WHERE friendship_id = ?")) {
            selectStmt.setInt(1, friendshipId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int user2Id = rs.getInt("user2_id");
                String status = rs.getString("status");
                if (status.equals("accepted")) {
                    System.out.println("You are already friends");
                    return;
                } else if (user2Id != userId) {
                    System.out.println("You are not authorized to accept this friend request");
                    return;
                }
            } else {
                System.out.println("Error: friendship request not found");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        try (Connection con = Database.getDatabase();
             PreparedStatement updateStmt = con.prepareStatement("UPDATE Friendship SET status = 'accepted' WHERE friendship_id = ?")) {
            updateStmt.setInt(1, friendshipId);
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated == 1) {
                System.out.println("Friendship request accepted successfully");

                // Move friend request from pending list to friends list
                Friend acceptedFriend = null;
                for (Friend friend : pendingArrayList) {
                    if (friend.getId() == friendshipId) {
                        acceptedFriend = friend;
                        break;
                    }
                }

                if (acceptedFriend != null) {
                    pendingArrayList.remove(acceptedFriend);
                    friendsArrayList.add(acceptedFriend);
                    pendingList.setItems(FXCollections.observableArrayList(pendingArrayList));
                    friendsList.setItems(FXCollections.observableArrayList(friendsArrayList));
                }
            } else {
                System.out.println("Error: friendship request not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    public void populatePendingFriendRequest(int userId) {
        pendingArrayList.clear(); // Clear the pendingArrayList before populating it

        try (Connection con = Database.getDatabase();
             PreparedStatement pstmt = con.prepareStatement("SELECT f.friendship_id, u.username, u.weight, u.height, u.profile_picture " +
                     "FROM Friendship f " +
                     "INNER JOIN \"User\" u ON ((f.user1_id = u.user_id OR f.user2_id = u.user_id) AND f.status = 'pending') " +
                     "WHERE (f.user1_id = ? OR f.user2_id = ?) AND u.user_id <> ?")) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int friendshipId = rs.getInt("friendship_id");
                String username = rs.getString("username");
                double weight = rs.getDouble("weight");
                double height = rs.getDouble("height");
                String image = rs.getString("profile_picture");
                pendingArrayList.add(new Friend(username, friendshipId, weight, height, image)); // Add friend request to pendingArrayList
            }
            pendingList.setItems(FXCollections.observableArrayList(pendingArrayList)); // Set the items for pendingList
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setProfilePicture(String Image){

    }
    
}