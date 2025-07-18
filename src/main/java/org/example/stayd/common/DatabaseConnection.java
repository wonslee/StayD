package org.example.stayd.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection(){
        // 프로퍼티 파일 경로
        String propertiesFile = "src/main/resources/application.properties";

        // 프로퍼티 객체 생성
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            // 프로퍼티 파일 로드
            properties.load(fis);

            // 프로퍼티 파일에서 데이터베이스 연결 정보 읽기
            String url = properties.getProperty("database.url");
            String username = properties.getProperty("database.username");
            String password = properties.getProperty("database.password");
            String driverClassName = properties.getProperty("database.driver-class-name");

            // Oracle JDBC 드라이버 로드
            Class.forName(driverClassName);

            // 데이터베이스 연결
            databaseLink = DriverManager.getConnection(url, username, password);

        } catch (IOException | ClassNotFoundException | java.sql.SQLException e) {
            e.printStackTrace();
        }

        return databaseLink;
    }
}