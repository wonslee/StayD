package org.example.stayd.common;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "xepdb1";
        String databaseUser = "ace";
        String databasePassword = "ace";
        String url = "jdbc:oracle:thin:@localhost:1521/" + databaseName;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaseLink;
    }
}
