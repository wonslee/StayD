package org.example.stayd.domain.user.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.stayd.common.DatabaseConnection;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
