// 작성자 : 방대혁
package org.example.stayd.domain.user.controller;

import static org.example.stayd.common.FXUtils.showAlert;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.stayd.common.EmailService;
import org.example.stayd.common.FXUtils;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.dto.UserDTO;
import org.example.stayd.domain.user.service.UserService;

public class SignupController {

    @FXML
    private TextField loginIdTextField;  // 사용자 아이디 입력 필드
    @FXML
    private Label idValidationLabel;    // 아이디 유효성 검사 라벨
    @FXML
    private TextField emailTextField;   // 이메일 입력 필드
    @FXML
    private Label emailValidationLabel; // 이메일 유효성 검사 라벨
    @FXML
    private PasswordField passwordTextField;  // 비밀번호 입력 필드
    @FXML
    private Label passwordValidationLabel;    // 비밀번호 유효성 검사 라벨
    @FXML
    private PasswordField passwordMatchTextField; // 비밀번호 확인 필드
    @FXML
    private Label passwordMatchValidationLabel;   // 비밀번호 확인 일치 검사 라벨
    @FXML
    private Button signUpButton;  // 회원가입 버튼

    private static final Pattern MIXED_PATTERN =  // 비밀번호 유효성 검사 정규식
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*\\W).+$");

    private final UserService userService = new UserService();  // 사용자 서비스 객체

    private final String propertiesFile = "src/main/resources/application.properties";  // 이메일 설정 파일 경로

    private String pendingToken;  // 인증 토큰
    private Instant tokenExpiresAt;  // 인증 토큰 만료 시간
    private UserDTO pendingDto;  // 임시 사용자 DTO

    /**
     * 초기화 메서드 - 아이디, 이메일, 비밀번호, 비밀번호 확인의 유효성 검사 및 버튼 활성화 제어
     */
    @FXML
    public void initialize() {
        // ID 중복 검사
        idValidationLabel.setText("");
        loginIdTextField.focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                String login_id = loginIdTextField.getText().trim();
                if (login_id.isEmpty()) {
                    idValidationLabel.setText("아이디를 입력해주세요.");
                } else {
                    try {
                        boolean ok = userService.isIdAvailable(login_id);
                        setLabel(idValidationLabel,
                                ok ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.",
                                ok);
                    } catch (UserService.ValidationException ex) {
                        idValidationLabel.setText("검사 중 오류 발생");
                    }
                }
                updateSignUpEnabled();
            }
        });

        // Email 중복 검사
        emailValidationLabel.setText("");
        emailTextField.focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                String email = emailTextField.getText().trim();
                if (email.isEmpty()) {
                    emailValidationLabel.setText("이메일을 입력해주세요.");
                } else if (!email.matches("^.+@.+\\..+$")) {
                    emailValidationLabel.setText("유효한 이메일 형식이 아닙니다.");
                } else {
                    try {
                        boolean ok = userService.isEmailAvailable(email);
                        setLabel(emailValidationLabel,
                                ok ? "사용 가능한 이메일입니다." : "이미 가입된 이메일입니다.",
                                ok);
                    } catch (UserService.ValidationException ex) {
                        emailValidationLabel.setText("검사 중 오류 발생");
                    }
                }
                updateSignUpEnabled();
            }
        });

        // Password 검사
        passwordValidationLabel.setText("");
        passwordTextField.textProperty().addListener((obs, oldV, newV) -> {
            boolean lenOk = newV.length() >= 8;
            boolean mixOk = MIXED_PATTERN.matcher(newV).matches();
            if (!lenOk) {
                passwordValidationLabel.setText("8자 이상이어야 합니다.");
            } else if (!mixOk) {
                passwordValidationLabel.setText("영문·숫자·특수문자 포함 필요.");
            } else {
                passwordValidationLabel.setText("");
            }
            validatePasswordMatch();
            updateSignUpEnabled();
        });

        // PasswordMatch 검사
        passwordMatchValidationLabel.setText("");
        passwordMatchTextField.textProperty().addListener((obs, oldV, newV) -> {
            validatePasswordMatch();
            updateSignUpEnabled();
        });
    }

    /**
     * 비밀번호 확인 일치 로직 - 비밀번호와 비밀번호 확인이 일치하는지 확인
     */
    private void validatePasswordMatch() {
        String pw = passwordTextField.getText();
        String chk = passwordMatchTextField.getText();
        if (chk.isEmpty()) {
            passwordMatchValidationLabel.setText("");
        } else if (!pw.equals(chk)) {
            passwordMatchValidationLabel.setText("비밀번호가 일치하지 않습니다.");
        } else {
            passwordMatchValidationLabel.setText("");
        }
    }

    /**
     * 버튼 활성화/비활성화 제어 - 아이디, 이메일, 비밀번호 및 비밀번호 확인이 모두 유효한 경우에만 가입 버튼을 활성화
     */
    private void updateSignUpEnabled() {
        boolean idOk = "사용 가능한 아이디입니다.".equals(idValidationLabel.getText());
        boolean emOk = "사용 가능한 이메일입니다.".equals(emailValidationLabel.getText());
        String pw = passwordTextField.getText();
        boolean pwOk = pw.length() >= 8 && MIXED_PATTERN.matcher(pw).matches();
        boolean match = pw.equals(passwordMatchTextField.getText()) && !passwordMatchTextField.getText().isEmpty();
        signUpButton.setDisable(!(idOk && emOk && pwOk && match));
    }

    /**
     * 회원가입 버튼 클릭 시 호출되는 메서드 - 아이디, 이메일, 비밀번호를 검증하고, 인증 코드를 발송하며, 비밀번호를 재설정하기 위한 화면으로 전환
     */
    @FXML
    private void onSignUp() {

        pendingDto = new UserDTO(
                loginIdTextField.getText().trim(),
                emailTextField.getText().trim(),
                passwordTextField.getText(),
                passwordMatchTextField.getText(),
                "USER"
        );

        try {
            userService.validateForPending(pendingDto);
        } catch (UserService.ValidationException ex) {
            showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.WARNING, "회원가입 실패", ex.getMessage());
            return;
        }

        // 인증 토큰 생성·만료 설정
        pendingToken = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        tokenExpiresAt = Instant.now().plus(Duration.ofMinutes(10));
        signUpButton.setDisable(true);

        // 이메일 설정 파일 읽기
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            props.load(fis);
        } catch (IOException e) {
            showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.ERROR, "설정 오류", "메일 설정을 불러오지 못했습니다.");
            signUpButton.setDisable(false);
            return;
        }

        // 이메일 발송
        String mailUser = props.getProperty("mail.username");
        String mailPass = props.getProperty("mail.password");
        EmailService emailService = new EmailService(mailUser, mailPass);

        new Thread(() -> {
            try {
                emailService.sendVerificationCode(pendingDto.getEmail(), pendingToken);
                // UI 확인 창
                Platform.runLater(() -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("이메일 인증");
                    confirm.setHeaderText("인증 코드를 이메일로 전송했습니다.");
                    confirm.setContentText("지금 인증 코드를 입력하시겠습니까?");
                    confirm.initOwner(signUpButton.getScene().getWindow());

                    Optional<ButtonType> choice = confirm.showAndWait();
                    if (choice.isPresent() && choice.get() == ButtonType.OK) {
                        promptForVerification();  // 코드 입력 팝업 실행
                    } else {
                        signUpButton.setDisable(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.ERROR, "발송 실패",
                            "이메일 발송에 실패했습니다. 다시 시도하세요.");
                    signUpButton.setDisable(false);
                });
            }
        }).start();
    }

    /**
     * 인증 코드 입력 및 검증 후 회원가입 완료 - 사용자에게 입력을 받아 인증하고, 회원가입을 완료
     */
    private void promptForVerification() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("이메일 인증");
        dlg.setHeaderText("전송된 8자리 코드를 입력하세요.");
        dlg.setContentText("코드:");
        Window owner = signUpButton.getScene().getWindow();
        dlg.initOwner(owner);

        Optional<String> result = dlg.showAndWait();
        if (result.isEmpty()) {
            showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.INFORMATION, "인증 취소", "인증이 취소되었습니다.");
            signUpButton.setDisable(false);
            return;
        }
        String input = result.get().trim().toUpperCase();
        if (Instant.now().isAfter(tokenExpiresAt)) {
            showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.WARNING, "인증 실패", "인증 코드가 만료되었습니다.");
            signUpButton.setDisable(false);
            return;
        }
        if (!input.equals(pendingToken)) {
            showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.WARNING, "인증 실패", "인증 코드가 일치하지 않습니다.");
            signUpButton.setDisable(false);
            return;
        }
        try {
            userService.register(pendingDto);
            showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.INFORMATION, "회원가입 성공", "이메일 인증을 완료했습니다.");
            goToLogin();
        } catch (UserService.ValidationException ex) {
            showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.WARNING, "회원가입 실패", ex.getMessage());
            signUpButton.setDisable(false);
        }
    }

    /**
     * 라벨 색 변환 - 라벨 텍스트와 색상 설정
     */
    private void setLabel(Label label, String text, boolean positive) {
        label.setText(text);
        label.setTextFill(positive ? Color.GREEN : Color.RED);
    }

    /**
     * 로그인 페이지 이동 - 회원가입 후 로그인 페이지로 이동
     */
    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.LOGIN_FXML);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(signUpButton.getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류",
                    "로그인 화면을 불러오는 중 오류가 발생했습니다.");
        }
    }
}
