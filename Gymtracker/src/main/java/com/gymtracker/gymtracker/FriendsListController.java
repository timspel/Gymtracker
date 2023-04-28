package com.gymtracker.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendsListController{


    @FXML
    private ListView friendsList;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    private String search;

    public void searchButtonClicked(ActionEvent event) throws IOException{
        if (event.getSource() == searchButton){
            search = searchField.getText();
            searchFriends(search);
        }
    }
    public void searchFriends(String search){

        Connection con = null;
        try {con = Database.getDatabase();

            PreparedStatement stmt = con.prepareStatement("SELECT username FROM \"User\" WHERE username LIKE ?"); {
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
}