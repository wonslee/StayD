package org.example.stayd.domain.user.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.stayd.common.FXUtils;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.dto.UserDTO;
import org.example.stayd.domain.user.service.UserService;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField loginIdField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label messageLabel;
    @FXML
    private Label findIdLabel;
    @FXML
    private Label findPwLabel;
    @FXML
    private Label signUpLabel;
    private final UserService userService = new UserService();

    @FXML
    private void onLinkHover(MouseEvent event) {
        Label lbl = (Label) event.getSource();
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-underline: true;");
    }

    @FXML
    private void onLinkExit(MouseEvent event) {
        Label lbl = (Label) event.getSource();
        lbl.setStyle("-fx-font-weight: normal; -fx-text-fill: black; -fx-underline: true;");
    }

    @FXML
    private void onLogin() {
        String loginId = loginIdField.getText().trim();
        String rawPw = passwordField.getText();

        // 입력 빈값 검증
        if (loginId.isEmpty() || rawPw.isEmpty()) {
            messageLabel.setText("아이디와 비밀번호를 모두 입력하세요.");
            return;
        }

        // 버튼 비활성화, 메시지 초기화
        loginButton.setDisable(true);
        messageLabel.setText("로그인 중…");

        Task<UserDTO> loginTask = new Task<>() {
            @Override
            protected UserDTO call() throws Exception {
                return userService.authenticate(loginId, rawPw);
            }
        };

        loginTask.setOnSucceeded(evt -> {
            goToSignup();
        });

        loginTask.setOnFailed(evt -> {
            Throwable ex = loginTask.getException();
            messageLabel.setText(ex.getMessage());
            loginButton.setDisable(false);
        });

        new Thread(loginTask).start();
    }

    @FXML
    private void onFindId() {
    }

    @FXML
    private void onFindPw() {
    }

    @FXML
    private void goToSignup() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.SIGNUP_FXML);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "화면 전환 오류", "회원가입 화면을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
