package org.example.stayd.domain.user.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.stayd.common.EmailService;
import org.example.stayd.common.FXUtils;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.dto.PasswordResetDTO;
import org.example.stayd.domain.user.service.UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;

import static javafx.application.Platform.runLater;
import static org.example.stayd.common.FXUtils.showAlert;

public class FindPwController {

    @FXML
    private TextField loginIdField;
    @FXML
    private TextField emailField;
    @FXML
    private Label validationLabel;
    @FXML
    private Button sendCodeButton;

    private final UserService userService = new UserService();
    private final String propertiesFile = "src/main/resources/application.properties";

    private String pendingToken;
    private Instant tokenExpiresAt;
    private PasswordResetDTO resetDto;

    @FXML
    public void initialize() {
        validationLabel.setText("");
        sendCodeButton.setDisable(true);
        loginIdField.textProperty().addListener((o, ov, nv) -> toggleButton());
        emailField.textProperty().addListener((o, ov, nv) -> toggleButton());
    }

    private void toggleButton() {
        sendCodeButton.setDisable(
                loginIdField.getText().trim().isEmpty() ||
                        emailField.getText().trim().isEmpty()
        );
        validationLabel.setText("");
    }

    @FXML
    private void onSendCode() {
        String loginId = loginIdField.getText().trim();
        String email = emailField.getText().trim();

        // 이메일 형식 검사
        if (!email.matches("^.+@.+\\..+$")) {
            validationLabel.setText("유효한 이메일을 입력하세요.");
            return;
        }

        // 아이디+이메일 일치 여부 확인
        try {
            if (!userService.checkUserEmail(loginId, email)) {
                showAlert(
                        null, Alert.AlertType.ERROR,
                        "조회 실패", "입력하신 정보와 일치하는 계정이 없습니다."
                );
                return;
            }
        } catch (UserService.ValidationException ex) {
            showAlert(
                    null, Alert.AlertType.ERROR,
                    "조회 오류", ex.getMessage()
            );
            return;
        }

        // 인증 코드 생성·만료 설정
        pendingToken = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        tokenExpiresAt = Instant.now().plus(Duration.ofMinutes(10));
        resetDto = new PasswordResetDTO(loginId, email);
        sendCodeButton.setDisable(true);

        // 메일 설정 로드
        Properties props = new Properties();
        try (var fis = new FileInputStream(propertiesFile)) {
            props.load(fis);
        } catch (IOException e) {
            showAlert(
                    null, Alert.AlertType.ERROR,
                    "설정 오류", "메일 설정을 불러오지 못했습니다."
            );
            sendCodeButton.setDisable(false);
            return;
        }

        // 이메일 발송
        String mailUser = props.getProperty("mail.username");
        String mailPass = props.getProperty("mail.password");
        EmailService emailService = new EmailService(mailUser, mailPass);

        new Thread(() -> {
            try {
                emailService.sendVerificationCode(email, pendingToken);
                runLater(this::showCodeDialog);
            } catch (Exception e) {
                runLater(() ->
                        showAlert(
                                null, Alert.AlertType.ERROR,
                                "전송 실패", "인증 코드 전송에 실패했습니다."
                        )
                );
            }
        }).start();
    }

    private void showCodeDialog() {
        var dlg = new TextInputDialog();
        dlg.setTitle("비밀번호 찾기");
        dlg.setHeaderText("메일로 전송된 코드를 입력하세요.");
        dlg.setContentText("코드:");
        dlg.initOwner(sendCodeButton.getScene().getWindow());

        dlg.showAndWait().ifPresent(input -> {
            if (Instant.now().isAfter(tokenExpiresAt)) {
                showAlert(
                        null, Alert.AlertType.WARNING,
                        "만료", "인증 코드가 만료되었습니다."
                );
                sendCodeButton.setDisable(false);
            } else if (!input.trim().equals(pendingToken)) {
                showAlert(
                        null, Alert.AlertType.ERROR,
                        "불일치", "인증 코드가 일치하지 않습니다."
                );
                sendCodeButton.setDisable(false);
            } else {
                // 인증 성공 → 새 비밀번호 설정 화면으로 이동
                runLater(() -> {
                    showAlert(
                            null, Alert.AlertType.INFORMATION,
                            "성공", "인증 코드가 일치합니다. 비밀번호 재설정을 진행합니다."
                    );
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource(SceneConfig.RESET_PW_FXML)
                        );
                        Parent root = loader.load();
                        // 컨트롤러에 DTO 주입
                        ResetPwController ctrl = loader.getController();
                        ctrl.initData(resetDto);
                        // 씬 교체
                        Stage stage = (Stage) sendCodeButton.getScene().getWindow();
                        stage.setScene(new Scene(root));
                    } catch (IOException ex) {
                        showAlert(null, Alert.AlertType.ERROR,
                                "화면 오류", "비밀번호 재설정 화면을 열 수 없습니다.");
                    }
                });
            }
        });
    }
}