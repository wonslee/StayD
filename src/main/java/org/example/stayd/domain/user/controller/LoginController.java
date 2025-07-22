package org.example.stayd.domain.user.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.stayd.common.FXUtils;
import org.example.stayd.common.SessionManager;
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
            // 로그인 성공 시, 사용자 정보를 SessionManager에 저장
            UserDTO user = loginTask.getValue();
            SessionManager.getInstance().setLoggedInUser(user);  // 로그인 정보 저장
            goToHome(user);  // 홈 화면으로 이동
        });

        loginTask.setOnFailed(evt -> {
            Throwable ex = loginTask.getException();
            messageLabel.setText(ex.getMessage());
            loginButton.setDisable(false);
        });

        new Thread(loginTask).start();
    }

    private void goToHome(UserDTO user) {
        Stage stage = (Stage) loginButton.getScene().getWindow(); // 로그인 버튼이 속한 Stage를 가져옴

        // 사용자의 역할에 맞게 화면을 전환
        if ("CAFE_OWNER".equals(user.getRole())) {
            try {
                // CAFE_OWNER일 경우 스터디 카페 관리 화면으로 이동
                FXUtils.switchScene(stage, SceneConfig.RESERVATION_STATUS_FXML);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "화면 전환 오류", "스터디 카페 관리 화면을 불러오는 중 오류가 발생했습니다.");
            }
        } else {
            try {
                // 기본 사용자일 경우 메인 화면으로 이동
                FXUtils.switchScene(stage, SceneConfig.HOME_FXML);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "화면 전환 오류", "메인 화면을 불러오는 중 오류가 발생했습니다.");
            }
        }
    }

    @FXML
    private void goToSignup() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.SIGNUP_FXML);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "화면 전환 오류", "홈 화면으로 이동하는 중 오류가 발생했습니다.");
        }
    }

    @FXML
    private void onFindId(MouseEvent event) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.FIND_ID_FXML);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "화면 전환 오류", "아이디 찾기 화면을 불러오는 중 오류가 발생했습니다.");
        }
    }

    @FXML
    private void onFindPw(MouseEvent event) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.FIND_PW_FXML);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "화면 전환 오류", "비밀번호 찾기 화면을 불러오는 중 오류가 발생했습니다.");
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
