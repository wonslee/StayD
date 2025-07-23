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
    private TextField loginIdField;  // 아이디 입력 필드
    @FXML
    private TextField emailField;  // 이메일 입력 필드
    @FXML
    private Label validationLabel;  // 유효성 검사 결과를 표시하는 라벨
    @FXML
    private Button sendCodeButton;  // 인증 코드 발송 버튼

    private final UserService userService = new UserService();  // 사용자 서비스 객체
    private final String propertiesFile = "src/main/resources/application.properties";  // 이메일 설정 파일 경로

    private String pendingToken;  // 인증 코드
    private Instant tokenExpiresAt;  // 인증 코드 만료 시간
    private PasswordResetDTO resetDto;  // 비밀번호 재설정 DTO

    /**
     * 화면 초기화 시 호출되는 메서드.
     * 이메일과 아이디 입력 시, 유효성 검사를 통해 버튼을 활성화/비활성화함.
     */
    @FXML
    public void initialize() {
        validationLabel.setText("");  // 초기 라벨 텍스트 비우기
        sendCodeButton.setDisable(true);  // 버튼 비활성화 초기화

        // 아이디 또는 이메일 입력 시 버튼 활성화/비활성화 처리
        loginIdField.textProperty().addListener((o, ov, nv) -> toggleButton());
        emailField.textProperty().addListener((o, ov, nv) -> toggleButton());
    }

    /**
     * 이메일과 아이디 입력란에 값이 있을 때 버튼 활성화
     */
    private void toggleButton() {
        sendCodeButton.setDisable(
                loginIdField.getText().trim().isEmpty() ||
                        emailField.getText().trim().isEmpty()
        );
        validationLabel.setText("");  // 유효성 검사 라벨 초기화
    }

    /**
     * 인증 코드 요청 버튼 클릭 시 호출되는 메서드
     * 1. 이메일 형식 검증
     * 2. 아이디와 이메일 일치 여부 확인
     * 3. 인증 코드 생성 및 이메일로 발송
     */
    @FXML
    private void onSendCode() {
        String loginId = loginIdField.getText().trim();  // 아이디
        String email = emailField.getText().trim();  // 이메일

        // 이메일 형식 검증
        if (!email.matches("^.+@.+\\..+$")) {
            validationLabel.setText("유효한 이메일을 입력하세요.");
            return;
        }

        // 아이디와 이메일 일치 여부 확인
        try {
            if (!userService.checkUserEmail(loginId, email)) {
                showAlert(
                        sendCodeButton.getScene().getWindow(), Alert.AlertType.ERROR,
                        "조회 실패", "입력하신 정보와 일치하는 계정이 없습니다."
                );
                return;
            }
        } catch (UserService.ValidationException ex) {
            showAlert(
                    sendCodeButton.getScene().getWindow(), Alert.AlertType.ERROR,
                    "조회 오류", ex.getMessage()
            );
            return;
        }

        // 인증 코드 생성 및 만료 시간 설정
        pendingToken = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        tokenExpiresAt = Instant.now().plus(Duration.ofMinutes(10));  // 10분 동안 유효
        resetDto = new PasswordResetDTO(loginId, email);  // 비밀번호 재설정 DTO

        sendCodeButton.setDisable(true);  // 인증 코드 발송 후 버튼 비활성화

        // 메일 설정 로드
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            props.load(fis);  // 메일 설정 파일 로드
        } catch (IOException e) {
            showAlert(
                    sendCodeButton.getScene().getWindow(), Alert.AlertType.ERROR,
                    "설정 오류", "메일 설정을 불러오지 못했습니다."
            );
            sendCodeButton.setDisable(false);  // 버튼 활성화
            return;
        }

        // 메일 계정 정보 로드
        String mailUser = props.getProperty("mail.username");
        String mailPass = props.getProperty("mail.password");
        EmailService emailService = new EmailService(mailUser, mailPass);

        // 새로운 스레드에서 이메일 발송
        new Thread(() -> {
            try {
                // 이메일 발송 내용 설정
                String subject = "[MyStay] 아이디 안내";
                String body = """
                        안녕하세요.
                        요청하신 회원님의 아이디는 다음과 같습니다:
                        
                        %s
                        
                        감사합니다.
                        """.formatted(loginId);

                // 이메일 발송
                emailService.sendEmail(email, subject, body);

                // UI 스레드에서 Alert 띄우고, 확인 누르면 로그인 화면으로 이동
                runLater(this::showCodeDialog);
            } catch (Exception e) {
                runLater(() ->
                        showAlert(
                                sendCodeButton.getScene().getWindow(), Alert.AlertType.ERROR,
                                "전송 실패", "인증 코드 전송에 실패했습니다."
                        )
                );
            }
        }).start();
    }

    /**
     * 인증 코드 입력 받기 위한 대화상자 표시
     */
    private void showCodeDialog() {
        var dlg = new TextInputDialog();
        dlg.setTitle("비밀번호 찾기");
        dlg.setHeaderText("메일로 전송된 코드를 입력하세요.");
        dlg.setContentText("코드:");
        dlg.initOwner(sendCodeButton.getScene().getWindow());

        dlg.showAndWait().ifPresent(input -> {
            if (Instant.now().isAfter(tokenExpiresAt)) {
                showAlert(
                        sendCodeButton.getScene().getWindow(), Alert.AlertType.WARNING,
                        "만료", "인증 코드가 만료되었습니다."
                );
                sendCodeButton.setDisable(false);  // 버튼 활성화
            } else if (!input.trim().equals(pendingToken)) {
                showAlert(
                        sendCodeButton.getScene().getWindow(), Alert.AlertType.ERROR,
                        "불일치", "인증 코드가 일치하지 않습니다."
                );
                sendCodeButton.setDisable(false);  // 버튼 활성화
            } else {
                // 인증 성공 → 새 비밀번호 설정 화면으로 이동
                runLater(() -> {
                    showAlert(
                            sendCodeButton.getScene().getWindow(), Alert.AlertType.INFORMATION,
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
                        showAlert(sendCodeButton.getScene().getWindow(), Alert.AlertType.ERROR,
                                "화면 오류", "비밀번호 재설정 화면을 열 수 없습니다.");
                    }
                });
            }
        });
    }
}
