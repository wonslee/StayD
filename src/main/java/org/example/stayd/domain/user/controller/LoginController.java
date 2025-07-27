// 작성자 : 방대혁
package org.example.stayd.domain.user.controller;

import static org.example.stayd.common.FXUtils.showAlert;

import java.io.IOException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.stayd.common.FXUtils;
import org.example.stayd.common.SessionManager;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.dto.UserDTO;
import org.example.stayd.domain.user.service.UserService;

public class LoginController {

    @FXML
    private TextField loginIdField;  // 아이디 입력 필드
    @FXML
    private PasswordField passwordField;  // 비밀번호 입력 필드
    @FXML
    private Button loginButton;  // 로그인 버튼
    @FXML
    private Label messageLabel;  // 로그인 메시지 라벨
    @FXML
    private Label findIdLabel;  // 아이디 찾기 라벨
    @FXML
    private Label findPwLabel;  // 비밀번호 찾기 라벨
    @FXML
    private Label signUpLabel;  // 회원가입 라벨

    private final UserService userService = new UserService();  // 사용자 서비스 객체

    /**
     * 화면 초기화 시 실행되는 메서드 입력란에 텍스트가 입력되면 유효성 검사 후 버튼 활성화 여부를 설정
     */
    @FXML
    public void initialize() {
        loginIdField.setText("");  // 초기 라벨 텍스트 비우기
        passwordField.setText("");  // 초기 라벨 텍스트 비우기
        loginButton.setDisable(true);  // 버튼 비활성화

        // 입력란에 텍스트가 변경되면 유효성 검사 및 버튼 활성화 여부 설정
        loginIdField.textProperty().addListener((obs, oldV, newV) -> {
            messageLabel.setText("");  // 유효성 검사 라벨 초기화
            loginButton.setDisable(loginIdField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty());
        });

        passwordField.textProperty().addListener((obs, oldV, newV) -> {
            messageLabel.setText("");  // 유효성 검사 라벨 초기화
            loginButton.setDisable(loginIdField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty());
        });
    }

    /**
     * 링크에 마우스 오버 시 스타일 변경
     */
    @FXML
    private void onLinkHover(MouseEvent event) {
        Label lbl = (Label) event.getSource();
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-underline: true;");
    }

    /**
     * 링크에 마우스 아웃 시 스타일 변경
     */
    @FXML
    private void onLinkExit(MouseEvent event) {
        Label lbl = (Label) event.getSource();
        lbl.setStyle("-fx-font-weight: normal; -fx-text-fill: black; -fx-underline: true;");
    }

    /**
     * 로그인 버튼 클릭 시 호출되는 메서드 1. 아이디와 비밀번호를 입력받고 유효성 검사 2. 사용자 인증을 비동기로 진행하여 UI를 차단하지 않음
     */
    @FXML
    private void onLogin() {
        String loginId = loginIdField.getText().trim();  // 아이디
        String rawPw = passwordField.getText();  // 비밀번호

        // 입력 빈값 검증
        if (loginId.isEmpty() || rawPw.isEmpty()) {
            messageLabel.setText("아이디와 비밀번호를 모두 입력하세요.");
            return;
        }

        // 버튼 비활성화, 메시지 초기화
        loginButton.setDisable(true);
        messageLabel.setText("로그인 중…");

        // 로그인 작업을 비동기적으로 처리
        Task<UserDTO> loginTask = new Task<>() {
            @Override
            protected UserDTO call() throws Exception {
                return userService.authenticate(loginId, rawPw);  // 사용자 인증
            }
        };

        // 로그인 성공 시 사용자 정보를 세션에 저장하고 홈 화면으로 이동
        loginTask.setOnSucceeded(evt -> {
            UserDTO user = loginTask.getValue();  // 로그인 성공한 사용자 객체
            SessionManager.getInstance().setLoggedInUser(user);  // 로그인 정보 세션에 저장
            goToHome(user);  // 홈 화면으로 이동
        });

        // 로그인 실패 시 실패 메시지 표시
        loginTask.setOnFailed(evt -> {
            Throwable ex = loginTask.getException();
            messageLabel.setText(ex.getMessage());  // 오류 메시지 표시
            loginButton.setDisable(false);  // 로그인 버튼 재활성화
        });

        // 새로운 스레드에서 로그인 작업을 시작
        new Thread(loginTask).start();
    }

    /**
     * 사용자 역할에 맞는 홈 화면으로 이동
     *
     * @param user 로그인한 사용자 객체
     */
    private void goToHome(UserDTO user) {
        Stage stage = (Stage) loginButton.getScene().getWindow();  // 로그인 버튼이 속한 Stage를 가져옴

        // 사용자의 역할에 따라 화면을 전환
        if ("CAFE_OWNER".equals(user.getRole())) {
            try {
                // CAFE_OWNER일 경우 스터디 카페 관리 화면으로 이동
                FXUtils.switchScene(stage, SceneConfig.RESERVATION_STATUS_FXML);
            } catch (IOException e) {
                showAlert(null, Alert.AlertType.ERROR, "화면 전환 오류", "스터디 카페 관리 화면을 불러오는 중 오류가 발생했습니다.");
            }
        } else {
            try {
                // 기본 사용자일 경우 메인 화면으로 이동
                FXUtils.switchScene(stage, SceneConfig.HOME_FXML);
            } catch (IOException e) {
                showAlert(null, Alert.AlertType.ERROR, "화면 전환 오류", "메인 화면을 불러오는 중 오류가 발생했습니다.");
            }
        }
    }

    /**
     * 회원가입 페이지로 이동
     */
    @FXML
    private void goToSignup() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.SIGNUP_FXML);
        } catch (IOException e) {
            showAlert(null, Alert.AlertType.ERROR, "화면 전환 오류", "홈 화면으로 이동하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 아이디 찾기 페이지로 이동
     */
    @FXML
    private void onFindId(MouseEvent event) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.FIND_ID_FXML);
        } catch (IOException e) {
            showAlert(null, Alert.AlertType.ERROR, "화면 전환 오류", "아이디 찾기 화면을 불러오는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 비밀번호 찾기 페이지로 이동
     */
    @FXML
    private void onFindPw(MouseEvent event) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.FIND_PW_FXML);
        } catch (IOException e) {
            showAlert(null, Alert.AlertType.ERROR, "화면 전환 오류", "비밀번호 찾기 화면을 불러오는 중 오류가 발생했습니다.");
        }
    }
}
