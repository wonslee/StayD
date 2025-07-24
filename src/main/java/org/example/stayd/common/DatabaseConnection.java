package org.example.stayd.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * 데이터베이스 연결을 관리하는 클래스.
 * {@code application.properties} 파일에서 데이터베이스 연결 정보를 로드하여 연결을 생성.
 */
public class DatabaseConnection {

    /** 데이터베이스 연결 객체 */
    public Connection databaseLink;

    /**
     * {@code application.properties} 파일에서 연결 정보를 읽어 데이터베이스에 연결.
     *
     * @return {@code Connection} - 데이터베이스 연결 객체
     * @throws IOException 파일 읽기 오류
     * @throws ClassNotFoundException 드라이버 클래스 로드 오류
     * @throws java.sql.SQLException 연결 오류
     */
    public Connection getConnection(){
        String propertiesFile = "src/main/resources/application.properties";  // 프로퍼티 파일 경로
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            properties.load(fis);

            // 데이터베이스 연결 정보 읽기
            String url = properties.getProperty("database.url");
            String username = properties.getProperty("database.username");
            String password = properties.getProperty("database.password");
            String driverClassName = properties.getProperty("database.driver-class-name");

            // JDBC 드라이버 로드 및 연결
            Class.forName(driverClassName);
            databaseLink = DriverManager.getConnection(url, username, password);

        } catch (IOException | ClassNotFoundException | java.sql.SQLException e) {
            e.printStackTrace();  // 오류 출력
        }
        return databaseLink;  // 연결 객체 반환
    }
}
