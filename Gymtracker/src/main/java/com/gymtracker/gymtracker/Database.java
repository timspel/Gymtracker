package com.gymtracker.gymtracker;

import java.sql.*;

public class Database {

    public static Connection getDatabase(){
        Connection con = null;
        Statement stmt = null;
        try {

            Class.forName("org.postgresql.Driver");
            con  = DriverManager
                    .getConnection("jdbc:postgresql://pgserver.mau.se:5432/gymtracker", "an8345", "7zazym0c");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return con;
    }

}
