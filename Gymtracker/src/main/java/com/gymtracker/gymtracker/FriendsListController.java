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

/**
 * This class controls the friendslisttab, it has the function to search, add and preview other peoples accounts and
 * add them as friends.
 *  @author Villie Brandt, Tim Do
 */
public class FriendsListController implements Initializable {


    @FXML
    private Text alert;
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

    /**
     *  This initializes all the needed code for when the tab is activated, it starts all the pending/friends lists.
     * @param url represents the location of the FXML file.
     * @param resourceBundle used to localize and accessing resources.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        friendsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        friendsArrayList = new ArrayList<>();
        populateFriendsList(Singleton.getInstance().getUserId());

        pendingColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // Set cell value factory for the pendingColumn
        pendingArrayList = new ArrayList<>();
        populatePendingFriendRequest(Singleton.getInstance().getUserId());


        friendsList.setItems(FXCollections.observableArrayList(friendsArrayList));
        pendingList.setItems(FXCollections.observableArrayList(pendingArrayList)); // Set the items for pendingList


        pendingList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Friend selectedFriend = pendingList.getSelectionModel().getSelectedItem();
                setFriendInformation(selectedFriend.getName(), selectedFriend.getWeight(), selectedFriend.getHeight(), selectedFriend.getImage());
            }
        });

        friendsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Friend selectedFriend = friendsList.getSelectionModel().getSelectedItem();
                setFriendInformation(selectedFriend.getName(), selectedFriend.getWeight(), selectedFriend.getHeight(), selectedFriend.getImage());
            }
        });
    }

    /**
     * This is the method for the search button, it contains the events for when you press different buttons
     * in the panel. You can search, addfriend, removefriend also remove a friendrequest and accept a friendrequest.
     * @param event The actionlistner what happens.
     * @throws IOException
     */
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
                removeFriendshipRequest(friendshipId, Singleton.getInstance().getUserId());
            }

        }


        if (event.getSource() == acceptFriend){
            Friend selectedFriend = pendingList.getSelectionModel().getSelectedItem();
            if (selectedFriend != null) {
                int friendshipId = selectedFriend.getId();
                acceptFriendship(friendshipId, Singleton.getInstance().getUserId());
            }

        }
    }

    /**
     * The method for searching for a friend that makes a connection to the database and searches for the username that was
     * inputted in the searchbar and it gives back user_id, weight, height and a profile picture.
     * @param search The searched name.
     */
    public void searchFriends(String search){

        Connection con = null;
        try {con = Database.getDatabase();
            //con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement("SELECT user_id, username, weight, height, profile_picture FROM \"User\" WHERE username = ?"); {
                stmt.setString(1, search); // Set the first parameter of the prepared statement to the search string + a wildcard
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    friendUsername = rs.getString("username");
                    double weight = rs.getDouble("weight");
                    double height = rs.getDouble("height");
                    System.out.println(friendUsername);
                    userIdFriend = rs.getInt("user_id");
                    System.out.println(userIdFriend);
                    userFriendPicture =  rs.getString("profile_picture");
                    setFriendInformation(friendUsername, weight, height, userFriendPicture);

                } else {
                    setAlert("No user by that username was found");
                    clearFriendInformation();
                }
                stmt.close();
                //con.commit();
                con.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *  Method to set the username, weight, height, profilpicture for a user and display it.
     * @param friendUsername The username for a user.
     * @param weight The chosen weight for a user.
     * @param height The chosen height for a user.
     * @param image Either a chosen image or a default image.
     */
    public void setFriendInformation(String friendUsername, double weight, double height ,String image){
        selectedUsername.setText(friendUsername);
        userWeight.setText(String.valueOf(weight)+" kg");
        userHeight.setText(String.valueOf(height)+" cm");
        if (image.equals("")){
            profilePicture.setImage(new Image("icon.png"));
        }else
            profilePicture.setImage(new Image(image));
    }

    /**
     * This method is used for clearing all the information from the display labels and reset them.
     */
    public void clearFriendInformation(){
        selectedUsername.setText("");
        userWeight.setText("");
        userHeight.setText("");
        profilePicture.setImage(new Image("icon.png"));
    }

    /**
     * This method is for adding a friend and making a friendship and insert it into the pendingfriendshiplist.
     * @param user1Id The user id.
     * @param user2Id The friends id.
     */
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
                    setAlert("Users are already friends");
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

                        setAlert("Friend request sent successfully");
                        Friend friend = new Friend(friendUsername, user2Id, weight, height, image);
                        pendingArrayList.add(friend);
                        populatePendingFriendRequest(Singleton.getInstance().getUserId());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Used to populate a users friendslist it happens instantly in initialize and it searches for all the users friends
     * and populates the friendsarraylist.
     * @param userId The user that gets it's friendslist populated.
     */
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

    /**
     * This method is made for removing a friendshiprequest and if the friendshiprequest is in pending it allow the user
     * to delete it.
     * @param friendshipId the id off the friendship.
     * @param userId the user.
     */
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
                    setAlert("Friend request is not in pending status");
                    return;
                } else if (user1Id != userId && user2Id != userId) {
                    setAlert("You are not authorized to remove this friend request");
                    return;
                }
            } else {
                setAlert("You are not friends with this person");
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
                setAlert("Friendship request removed successfully");

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
                setAlert("You are not friends with this person");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * This method is similar to the method above except this method removes a friends instead of a friendshiprequest.
     * @param friendshipId The id of the friendship.
     */
    public void removeFriendship(int friendshipId) {
        try (Connection con = Database.getDatabase();
             PreparedStatement selectStmt = con.prepareStatement("SELECT status FROM Friendship WHERE friendship_id = ?")) {
            selectStmt.setInt(1, friendshipId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (!status.equals("accepted")) {
                    setAlert("Friendship is not in accepted status");
                    return;
                }
            } else {
                setAlert("You are not friends with this person");
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
                setAlert("Friendship removed successfully");

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
                setAlert("You are not friends with this person");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * This method is for accepting a friendship, it makes checks for the status of the two users friendship and it does
     * several checks for example if they are already friends, if it's the right person that accepts it.
     * @param friendshipId the id for the friendship.
     * @param userId the user.
     */
    public void acceptFriendship(int friendshipId, int userId) {
        try (Connection con = Database.getDatabase();
             PreparedStatement selectStmt = con.prepareStatement("SELECT user2_id, status FROM Friendship WHERE friendship_id = ?")) {
            selectStmt.setInt(1, friendshipId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int user2Id = rs.getInt("user2_id");
                String status = rs.getString("status");
                if (status.equals("accepted")) {
                    setAlert("You are already friends");
                    return;
                } else if (user2Id != userId) {
                    setAlert("You are not authorized to accept this friend request");
                    return;
                }
            } else {
                setAlert("Error: friendship request not found");
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
                setAlert("Friendship request accepted successfully");

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
                setAlert("Error: friendship request not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * This method is similar to the populatefriends but this method populates the list with pending friends requests.
     * @param userId the user.
     */
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

    /**
     * This method is used to send messages for example if there is a error how the user is trying to interact with the
     * friendlist.
     * @param message the message that gets sent.
     */
    public void setAlert(String message){
        alert.setText(message);
    }
    
}