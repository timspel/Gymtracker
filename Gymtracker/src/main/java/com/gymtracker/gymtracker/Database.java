package com.gymtracker.gymtracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {


    public static Connection getDatabase(){
        Connection con = null;
        Statement stmt = null;
        try {

            Class.forName("org.postgresql.Driver");
            con  = DriverManager
                    .getConnection("jdbc:postgresql://pgserver.mau.se:5432/gymtracker",
                            "an5527", "ss242937");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return con;
    }


}
