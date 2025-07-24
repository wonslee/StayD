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
    private TextField emailTextField;  // 이메일 입력 필드
    @FXML
    private Label emailValidationLabel;  // 이메일 유효성 검사 결과를 표시할 라벨
    @FXML
    private Button findIdButton;  // 아이디 찾기 버튼
    @FXML
    private Label goLoginLabel;  // 로그인 화면으로 가는 링크 라벨

    private final UserService userService = new UserService();  // UserService 객체
    private final String propertiesFile = "src/main/resources/application.properties";  // 이메일 설정 파일 경로

    /**
     * 화면 초기화 시 실행되는 메서드
     * 이메일 입력란에 텍스트가 입력되면 유효성 검사 후 버튼 활성화 여부를 설정
     */
    @FXML
    public void initialize() {
        emailValidationLabel.setText("");  // 초기 라벨 텍스트 비우기
        findIdButton.setDisable(true);  // 버튼 비활성화

        // 이메일 입력란에 텍스트가 변경되면 유효성 검사 및 버튼 활성화 여부 설정
        emailTextField.textProperty().addListener((obs, oldV, newV) -> {
            emailValidationLabel.setText("");  // 유효성 검사 라벨 초기화
            findIdButton.setDisable(newV.trim().isEmpty());  // 이메일이 비어있으면 버튼 비활성화
        });
    }

    /**
     * 아이디 찾기 버튼 클릭 시 호출되는 메서드
     * 이메일을 통해 아이디를 찾고, 이메일로 아이디를 전송하는 기능을 수행
     */
    @FXML
    private void onFindId() {
        String email = emailTextField.getText().trim();  // 입력된 이메일 가져오기

        // 이메일 형식이 유효한지 체크
        if (!email.matches("^.+@.+\\..+$")) {
            emailValidationLabel.setText("유효한 이메일을 입력하세요.");  // 유효하지 않으면 메시지 표시
            return;
        }

        String foundId;
        try {
            foundId = userService.findLoginIdByEmail(email);  // 이메일을 통해 아이디 찾기
        } catch (UserService.ValidationException ex) {
            // 유효성 예외 발생 시 알림 표시
            showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.ERROR, "조회 실패", ex.getMessage());
            return;
        }

        // 아이디가 없을 경우
        if (foundId == null) {
            showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.INFORMATION, "아이디 찾기", "등록된 아이디가 없습니다.");
            return;
        }

        // 이메일 발송을 위한 설정 값 로드
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            props.load(fis);  // 설정 파일에서 메일 설정 로드
        } catch (IOException e) {
            showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.ERROR, "설정 오류", "메일 설정을 불러오지 못했습니다.");
            return;
        }

        // 메일 발송에 필요한 계정 정보 로드
        String mailUser = props.getProperty("mail.username");
        String mailPass = props.getProperty("mail.password");
        EmailService emailService = new EmailService(mailUser, mailPass);

        // 새로운 스레드에서 이메일 발송 작업 수행
        new Thread(() -> {
            try {
                // 이메일 내용 설정
                String subject = "[MyStay] 아이디 안내";
                String body = """
                        안녕하세요.
                        요청하신 회원님의 아이디는 다음과 같습니다:
                        
                        %s
                        
                        감사합니다.
                        """.formatted(foundId);

                // 이메일 발송
                emailService.sendEmail(email, subject, body);

                // UI 스레드에서 알림창을 띄우고, 확인 누르면 로그인 화면으로 이동
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
                // 이메일 발송 실패 시 UI 스레드에서 알림
                Platform.runLater(() ->
                        showAlert(findIdButton.getScene().getWindow(), Alert.AlertType.ERROR, "전송 실패", "메일 발송에 실패했습니다.")
                );
            }
        }).start();
    }
}