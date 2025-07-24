package org.example.stayd.domain.user.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.example.stayd.common.FXUtils;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.dto.PasswordResetDTO;
import org.example.stayd.domain.user.service.UserService;

import static org.example.stayd.common.FXUtils.showAlert;

public class ResetPwController {

    @FXML
    private PasswordField newPasswordField;  // 새로운 비밀번호 입력 필드
    @FXML
    private PasswordField confirmPasswordField;  // 비밀번호 확인 입력 필드
    @FXML
    private Label newPasswordValidationLabel;  // 새로운 비밀번호 유효성 검사 라벨
    @FXML
    private Label confirmPasswordValidationLabel;  // 비밀번호 확인 일치 검사 라벨
    @FXML
    private Button resetPasswordButton;  // 비밀번호 재설정 버튼

    private PasswordResetDTO resetDto;  // 비밀번호 재설정 DTO

    private final UserService userService = new UserService();  // 사용자 서비스 객체

    /**
     * 초기화 메서드
     * - 비밀번호 유효성 검사 및 비밀번호 확인 일치 검사 기능 초기화
     */
    public void initData(PasswordResetDTO resetDto) {
        this.resetDto = resetDto;  // 비밀번호 재설정 DTO 초기화
    }

    @FXML
    public void initialize() {
        // 비밀번호 유효성 검사
        newPasswordValidationLabel.setText("");  // 초기화
        newPasswordField.textProperty().addListener((obs, oldV, newV) -> {
            validatePassword();  // 비밀번호 유효성 검사
            updateResetButtonEnabled();  // 버튼 활성화 여부 업데이트
        });

        // 비밀번호 확인 일치 검사
        confirmPasswordValidationLabel.setText("");  // 초기화
        confirmPasswordField.textProperty().addListener((obs, oldV, newV) -> {
            validatePasswordMatch();  // 비밀번호 확인 일치 검사
            updateResetButtonEnabled();  // 버튼 활성화 여부 업데이트
        });
    }

    /**
     * 새로운 비밀번호의 유효성 검사
     * - 최소 8자 이상, 영문·숫자·특수문자가 포함되어야 함
     */
    private void validatePassword() {
        String password = newPasswordField.getText().trim();
        if (password.length() < 8) {
            newPasswordValidationLabel.setText("비밀번호는 최소 8자 이상이어야 합니다.");
        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*\\W).+$")) {
            newPasswordValidationLabel.setText("영문·숫자·특수문자 포함 필요.");
        } else {
            newPasswordValidationLabel.setText("");  // 유효성 검사 통과
        }
    }

    /**
     * 비밀번호 확인란에 입력된 값이 새로운 비밀번호와 일치하는지 검사
     */
    private void validatePasswordMatch() {
        String password = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        if (confirmPassword.isEmpty()) {
            confirmPasswordValidationLabel.setText("");
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordValidationLabel.setText("비밀번호가 일치하지 않습니다.");
        } else {
            confirmPasswordValidationLabel.setText("");  // 일치
        }
    }

    /**
     * 버튼 활성화 여부 제어
     * - 비밀번호 유효성 및 확인 일치 검사 통과 시 버튼을 활성화
     */
    private void updateResetButtonEnabled() {
        boolean pwOk = newPasswordValidationLabel.getText().isEmpty();
        boolean matchOk = confirmPasswordValidationLabel.getText().isEmpty();
        resetPasswordButton.setDisable(!(pwOk && matchOk));  // 두 조건 모두 만족 시 버튼 활성화
    }

    /**
     * 비밀번호 재설정 버튼 클릭 시 호출되는 메서드
     * - 비밀번호 입력값 유효성 체크 후, 비밀번호 재설정 처리
     */
    @FXML
    private void onResetPassword() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // 비밀번호 확인 검사
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(null, Alert.AlertType.ERROR, "비밀번호 오류", "비밀번호와 확인란을 모두 입력해주세요.");
            return;
        }

        // 비밀번호가 일치하는지 확인
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordValidationLabel.setText("비밀번호가 일치하지 않습니다.");
            return;
        }

        // 비밀번호 유효성 검사
        if (newPassword.length() < 8) {
            newPasswordValidationLabel.setText("비밀번호는 최소 8자 이상이어야 합니다.");
            return;
        }

        // 비밀번호 변경
        try {
            userService.updatePassword(resetDto.getLoginId(), newPassword);  // 비밀번호 변경
            showAlert(null, Alert.AlertType.INFORMATION, "비밀번호 변경 완료", "비밀번호가 성공적으로 변경되었습니다.");
            goToLogin();  // 로그인 화면으로 이동
        } catch (UserService.ValidationException ex) {
            showAlert(null, Alert.AlertType.ERROR, "변경 실패", ex.getMessage());  // 비밀번호 변경 실패
        }
    }

    /**
     * 비밀번호 변경 후 로그인 화면으로 이동
     */
    private void goToLogin() {
        try {
            Stage stage = (Stage) resetPasswordButton.getScene().getWindow();  // 현재 창(Stage) 가져오기
            FXUtils.switchScene(stage, SceneConfig.LOGIN_FXML);  // 로그인 화면으로 전환
        } catch (Exception e) {
            showAlert(null, Alert.AlertType.ERROR, "화면 전환 오류", "로그인 화면으로 이동하는 중 오류가 발생했습니다.");
        }
    }
}
