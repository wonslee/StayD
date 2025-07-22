package org.example.stayd.domain.user.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.stayd.common.EmailService;
import org.example.stayd.common.FXUtils;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.service.UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.example.stayd.common.FXUtils.showAlert;

public class FindIdController {

    @FXML
    private TextField emailTextField;
    @FXML
    private Label emailValidationLabel;
    @FXML
    private Button findIdButton;
    @FXML
    private Label goLoginLabel;

    private final UserService userService = new UserService();
    private final String propertiesFile = "src/main/resources/application.properties";

    @FXML
    public void initialize() {
        emailValidationLabel.setText("");
        findIdButton.setDisable(true);

        emailTextField.textProperty().addListener((obs, oldV, newV) -> {
            emailValidationLabel.setText("");
            findIdButton.setDisable(newV.trim().isEmpty());
        });
    }

    @FXML
    private void onFindId() {
        String email = emailTextField.getText().trim();
        if (!email.matches("^.+@.+\\..+$")) {
            emailValidationLabel.setText("유효한 이메일을 입력하세요.");
            return;
        }

        String foundId;
        try {
            foundId = userService.findLoginIdByEmail(email);
        } catch (UserService.ValidationException ex) {
            showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.ERROR, "조회 실패", ex.getMessage());
            return;
        }

        if (foundId == null) {
            showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.INFORMATION, "아이디 찾기", "등록된 아이디가 없습니다.");
            return;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            props.load(fis);
        } catch (IOException e) {
            showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.ERROR, "설정 오류", "메일 설정을 불러오지 못했습니다.");
            return;
        }

        String mailUser = props.getProperty("mail.username");
        String mailPass = props.getProperty("mail.password");
        EmailService emailService = new EmailService(mailUser, mailPass);

        new Thread(() -> {
            try {
                String subject = "[MyStay] 아이디 안내";
                String body = """
                        안녕하세요.
                        요청하신 회원님의 아이디는 다음과 같습니다:
                        
                        %s
                        
                        감사합니다.
                        """.formatted(foundId);

                emailService.sendEmail(email, subject, body);

                // UI 스레드에서 Alert 띄우고, 확인 누르면 로그인 화면으로 이동
                Platform.runLater(() -> {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("전송 완료");
                    a.setHeaderText(null);
                    a.setContentText("입력하신 이메일로 아이디를 전송했습니다.");
                    a.initOwner(findIdButton.getScene().getWindow());
                    a.showAndWait();

                    // 로그인 화면으로 전환
                    try {
                        FXUtils.switchScene(
                                (Stage) findIdButton.getScene().getWindow(),
                                SceneConfig.LOGIN_FXML
                        );
                    } catch (IOException ex) {
                        showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "로그인 화면으로 이동하는 중 오류가 발생했습니다.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.ERROR, "전송 실패", "메일 발송에 실패했습니다.")
                );
            }
        }).start();
    }
}