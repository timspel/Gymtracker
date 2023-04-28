package com.gymtracker.gymtracker;

import java.sql.*;

public class Database {


    public static void main(String[] args) {
        //addUser("Tim", "password123", 180.5, 6.2, "https://example.com/profile/john_doe.png");


    }
    /*public static void addUser(String username, String password, double weight, double height, String profilePicture) {
        try (Connection con = getDatabase();
             PreparedStatement stmt = con.prepareStatement("INSERT INTO \"User\" (username, password, weight, height, profile_picture) VALUES (?, ?, ?, ?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setDouble(3, weight);
            stmt.setDouble(4, height);
            stmt.setString(5, profilePicture);

            stmt.executeUpdate();

            System.out.println("User added successfully");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }*/

    public static Connection getDatabase(){
        Connection con = null;
        Statement stmt = null;
        try {

            Class.forName("org.postgresql.Driver");
            con  = DriverManager
                    .getConnection("jdbc:postgresql://pgserver.mau.se:5432/gymtracker", "an5527", "ss242937");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return con;
    }

}
