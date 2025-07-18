package org.example.stayd.domain.user.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.example.stayd.common.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Label findIdLabel;

    @FXML
    private Label findPwLabel;

    @FXML
    private Label signUpLabel;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField password;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private StackPane headerContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Header.fxml을 로드하여 StackPane에 추가
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/common/header.fxml"));
            Parent header = loader.load();
            headerContainer.getChildren().add(header);
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 발생 시 처리 코드 추가 (예: 사용자에게 에러 메시지 표시)
        }

        setupHoverEffect(findIdLabel);
        setupHoverEffect(findPwLabel);
        setupHoverEffect(signUpLabel);

        loginButton.setOnMouseClicked(mouseEvent -> {
            validateLogin();
        });

        findIdLabel.setOnMouseClicked(event -> {
            // 아이디 찾기
        });

        findPwLabel.setOnMouseClicked(event -> {
            // 비밀번호 찾기
        });

        signUpLabel.setOnMouseClicked(event -> {
            // 회원가입
        });
    }

    private void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM UserAccounts WHERE username = '" + usernameTextField.getText() + "' AND password = '" + password.getText() +"'";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while(queryResult.next()){
                if (queryResult.getInt(1)==1){
                    loginMessageLabel.setText("성공");
                } else{
                    loginMessageLabel.setText("실패");
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setupHoverEffect(Label label) {
        label.setCursor(Cursor.HAND);

        label.setOnMouseEntered(e -> label.setStyle("-fx-text-fill: blue; -fx-underline: true;"));
        label.setOnMouseExited(e -> label.setStyle("-fx-text-fill: black; -fx-underline: false;"));
    }
}
