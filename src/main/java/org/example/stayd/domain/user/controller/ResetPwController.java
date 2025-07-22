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

import java.io.IOException;

public class ResetPwController {

    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label newPasswordValidationLabel;
    @FXML
    private Label confirmPasswordValidationLabel;
    @FXML
    private Button resetPasswordButton;

    private PasswordResetDTO resetDto;

    private final UserService userService = new UserService();

    public void initData(PasswordResetDTO resetDto) {
        this.resetDto = resetDto;
    }

    @FXML
    public void initialize() {
        // 비밀번호 유효성 검사
        newPasswordValidationLabel.setText("");
        newPasswordField.textProperty().addListener((obs, oldV, newV) -> {
            validatePassword();
            updateResetButtonEnabled();
        });

        // 비밀번호 확인 일치 검사
        confirmPasswordValidationLabel.setText("");
        confirmPasswordField.textProperty().addListener((obs, oldV, newV) -> {
            validatePasswordMatch();
            updateResetButtonEnabled();
        });
    }

    // 비밀번호 유효성 검사
    private void validatePassword() {
        String password = newPasswordField.getText().trim();
        if (password.length() < 8) {
            newPasswordValidationLabel.setText("비밀번호는 최소 8자 이상이어야 합니다.");
        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*\\W).+$")) {
            newPasswordValidationLabel.setText("영문·숫자·특수문자 포함 필요.");
        } else {
            newPasswordValidationLabel.setText(""); // 유효성 검사 통과
        }
    }

    // 비밀번호 확인 일치 검사
    private void validatePasswordMatch() {
        String password = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        if (confirmPassword.isEmpty()) {
            confirmPasswordValidationLabel.setText("");
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordValidationLabel.setText("비밀번호가 일치하지 않습니다.");
        } else {
            confirmPasswordValidationLabel.setText(""); // 일치
        }
    }

    // 버튼 활성화/비활성화 제어
    private void updateResetButtonEnabled() {
        boolean pwOk = newPasswordValidationLabel.getText().isEmpty();
        boolean matchOk = confirmPasswordValidationLabel.getText().isEmpty();
        resetPasswordButton.setDisable(!(pwOk && matchOk));
    }

    @FXML
    private void onResetPassword() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // 비밀번호 확인 검사
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "비밀번호 오류", "비밀번호와 확인란을 모두 입력해주세요.");
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
            userService.updatePassword(resetDto.getLoginId(), newPassword);
            showAlert(Alert.AlertType.INFORMATION, "비밀번호 변경 완료", "비밀번호가 성공적으로 변경되었습니다.");
            goToLogin();
        } catch (UserService.ValidationException ex) {
            showAlert(Alert.AlertType.ERROR, "변경 실패", ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void goToLogin() {
        try {
            Stage stage = (Stage) resetPasswordButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.LOGIN_FXML);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "화면 전환 오류", "로그인 화면으로 이동하는 중 오류가 발생했습니다.");
        }
    }
}

