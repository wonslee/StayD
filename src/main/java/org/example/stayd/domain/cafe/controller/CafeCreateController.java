package org.example.stayd.domain.cafe.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.stayd.common.FXUtils;
import org.example.stayd.common.SessionManager;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.service.CafeService;

public class CafeCreateController implements Initializable {

    @FXML
    private TextField cafeNameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField selectedDaysField;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField imageUrlField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label charCountLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Font registerButtonFont;

    // 요일 버튼들
    @FXML
    private ToggleButton mondayButton;
    @FXML
    private ToggleButton tuesdayButton;
    @FXML
    private ToggleButton wednesdayButton;
    @FXML
    private ToggleButton thursdayButton;
    @FXML
    private ToggleButton fridayButton;
    @FXML
    private ToggleButton saturdayButton;
    @FXML
    private ToggleButton sundayButton;

    private int currentPrice = 1000;
    private int startHour = 9;
    private int endHour = 18;
    private List<String> selectedDays = new ArrayList<>();

    // 서비스 계층
    private final CafeService cafeService;

    public CafeCreateController() {
        this.cafeService = new CafeService();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 초기 설정
        priceField.setText(String.valueOf(currentPrice));
        startTimeField.setText(String.format("%02d:00", startHour));
        endTimeField.setText(String.format("%02d:00", endHour));
        charCountLabel.setText("0/200");
    }

    // 요일 선택 토글
    @FXML
    private void toggleDay(ActionEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        String day = button.getText();

        if (button.isSelected()) {
            button.setOpacity(1.0);
            if (!selectedDays.contains(day)) {
                selectedDays.add(day);
            }
        } else {
            button.setOpacity(0.5);
            selectedDays.remove(day);
        }

        // 선택된 요일들을 숨겨진 필드에 저장
        selectedDaysField.setText(String.join(",", selectedDays));
    }

    // 시작 시간 증가
    @FXML
    private void increaseStartTime(ActionEvent event) {
        if (startHour < 23) {
            startHour++;
            startTimeField.setText(String.format("%02d:00", startHour));

            // 종료 시간이 시작 시간보다 작거나 같으면 조정
            if (endHour <= startHour) {
                endHour = startHour + 1;
                endTimeField.setText(String.format("%02d:00", endHour));
            }
        }
    }

    // 시작 시간 감소
    @FXML
    private void decreaseStartTime(ActionEvent event) {
        if (startHour > 0) {
            startHour--;
            startTimeField.setText(String.format("%02d:00", startHour));
        }
    }

    // 종료 시간 증가
    @FXML
    private void increaseEndTime(ActionEvent event) {
        if (endHour < 23) {
            endHour++;
            endTimeField.setText(String.format("%02d:00", endHour));
        }
    }

    // 종료 시간 감소
    @FXML
    private void decreaseEndTime(ActionEvent event) {
        if (endHour > startHour + 1) {
            endHour--;
            endTimeField.setText(String.format("%02d:00", endHour));
        }
    }

    // 가격 증가
    @FXML
    private void increasePrice(ActionEvent event) {
        currentPrice += 1000;
        priceField.setText(String.valueOf(currentPrice));
    }

    // 가격 감소
    @FXML
    private void decreasePrice(ActionEvent event) {
        if (currentPrice > 1000) {
            currentPrice -= 1000;
            priceField.setText(String.valueOf(currentPrice));
        }
    }

    // 전화번호 포맷팅
    @FXML
    private void formatPhoneNumber(KeyEvent event) {
        String text = phoneField.getText().replaceAll("[^0-9]", "");

        if (text.length() > 11) {
            text = text.substring(0, 11);
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (i == 3 || i == 7) {
                formatted.append("-");
            }
            formatted.append(text.charAt(i));
        }

        phoneField.setText(formatted.toString());
        phoneField.positionCaret(formatted.length());
    }

    // 설명 글자 수 제한
    @FXML
    private void limitDescription(KeyEvent event) {
        String text = descriptionArea.getText();

        if (text.length() > 200) {
            text = text.substring(0, 200);
            descriptionArea.setText(text);
            descriptionArea.positionCaret(200);
        }

        charCountLabel.setText(text.length() + "/200");
    }

    // 등록 버튼 마우스 진입
    @FXML
    private void onRegisterButtonEnter(MouseEvent event) {
        registerButton.setStyle(
            "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-font-size: 16px;");
    }

    // 등록 버튼 마우스 나가기
    @FXML
    private void onRegisterButtonExit(MouseEvent event) {
        registerButton.setStyle(
            "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-font-size: 14px;");
    }

    // 카페 생성 버튼 클릭 (백엔드 연동)
    @FXML
    private void registerCafe(ActionEvent event) {
        try {
            // DTO 생성
            CafeDto.CreateRequest request = createCafeRequest();

            // 더미 오너 ID 가져오기 (실제로는 현재 로그인한 사용자 ID)
//            Long ownerId = cafeService.getDummyOwnerId();
            Long ownerId = (long) SessionManager.getInstance().getLoggedInUser().getUser_id();

            // 카페 생성 서비스 호출
            CafeDto.CreateResponse response = cafeService.createCafe(request, ownerId);

            if (response.isSuccess()) {
                // 성공 시
                showAlert(Alert.AlertType.INFORMATION, "생성 성공",
                    "카페 ID: " + response.getCafeId() + "\n" + response.getMessage());
                clearForm();

            } else {
                // 실패 시
                showAlert(Alert.AlertType.ERROR, "생성 실패", response.getMessage());
            }
            // FXUtils로 페이지 이동
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXUtils.navigateToPage(stage, SceneConfig.RESERVATION_STATUS_FXML, "페이지 이동");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류 발생", "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 입력 데이터로부터 카페 생성 요청 DTO 생성
     *
     * @return 카페 생성 요청 DTO
     */
    private CafeDto.CreateRequest createCafeRequest() {
        CafeDto.CreateRequest request = new CafeDto.CreateRequest();

        request.setName(cafeNameField.getText());
        request.setAddress(locationField.getText());
        request.setPricePerHour(currentPrice);
        request.setDescription(descriptionArea.getText());
        request.setPhoneNumber(phoneField.getText());
        request.setImageUrl(imageUrlField.getText());
        request.setOperatingDays(new ArrayList<>(selectedDays));
        request.setOperatingStartHour(startHour);
        request.setOperatingEndHour(endHour);

        return request;
    }

    /**
     * 폼 초기화
     */
    private void clearForm() {
        cafeNameField.clear();
        locationField.clear();
        phoneField.clear();
        imageUrlField.clear();
        descriptionArea.clear();

        // 요일 버튼 초기화
        resetDayButtons();

        // 시간 및 가격 초기화
        currentPrice = 1000;
        startHour = 9;
        endHour = 18;
        priceField.setText(String.valueOf(currentPrice));
        startTimeField.setText(String.format("%02d:00", startHour));
        endTimeField.setText(String.format("%02d:00", endHour));

        // 글자 수 카운터 초기화
        charCountLabel.setText("0/200");

        // 선택된 요일 초기화
        selectedDays.clear();
        selectedDaysField.clear();
    }

    /**
     * 요일 버튼들 초기화
     */
    private void resetDayButtons() {
        ToggleButton[] dayButtons = {
            mondayButton, tuesdayButton, wednesdayButton, thursdayButton,
            fridayButton, saturdayButton, sundayButton
        };

        for (ToggleButton button : dayButtons) {
            button.setSelected(false);
            button.setOpacity(0.5);
        }
    }

    /**
     * 알림 다이얼로그 표시
     *
     * @param alertType 알림 타입
     * @param title     제목
     * @param message   메시지
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}