package org.example.stayd.domain.cafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CafeCreateController implements Initializable {

    @FXML private TextField cafeNameField;
    @FXML private TextField locationField;
    @FXML private TextField selectedDaysField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private TextField priceField;
    @FXML private TextField phoneField;
    @FXML private TextField imageUrlField;
    @FXML private TextArea descriptionArea;
    @FXML private Label charCountLabel;
    @FXML private Button registerButton;
    @FXML private Font registerButtonFont;

    // 요일 버튼들
    @FXML private ToggleButton mondayButton;
    @FXML private ToggleButton tuesdayButton;
    @FXML private ToggleButton wednesdayButton;
    @FXML private ToggleButton thursdayButton;
    @FXML private ToggleButton fridayButton;
    @FXML private ToggleButton saturdayButton;
    @FXML private ToggleButton sundayButton;

    private int currentPrice = 1000;
    private int startHour = 9;
    private int endHour = 18;
    private List<String> selectedDays = new ArrayList<>();

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
        registerButton.setStyle("-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-font-size: 16px;");
    }

    // 등록 버튼 마우스 나가기
    @FXML
    private void onRegisterButtonExit(MouseEvent event) {
        registerButton.setStyle("-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-font-size: 14px;");
    }

    // 등록 버튼 클릭
    @FXML
    private void registerCafe(ActionEvent event) {
        // 입력 값 검증
        if (cafeNameField.getText().trim().isEmpty()) {
            showAlert("스터디 카페 이름을 입력해주세요.");
            return;
        }

        if (locationField.getText().trim().isEmpty()) {
            showAlert("위치를 입력해주세요.");
            return;
        }

        if (selectedDays.isEmpty()) {
            showAlert("영업일을 선택해주세요.");
            return;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showAlert("전화번호를 입력해주세요.");
            return;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            showAlert("설명을 입력해주세요.");
            return;
        }

        // 여기서 데이터베이스에 저장하는 로직 구현
        saveCafeData();
    }

    private void saveCafeData() {
        // 데이터베이스 저장 로직
        String cafeName = cafeNameField.getText();
        String location = locationField.getText();
        String businessDays = String.join(",", selectedDays);
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();
        int price = currentPrice;
        String phone = phoneField.getText();
        String imageUrl = imageUrlField.getText();
        String description = descriptionArea.getText();

        System.out.println("cafeName: " + cafeName);
        System.out.println("location: " + location);
        System.out.println("businessDays: " + businessDays);
        System.out.println("businessTime: " + startTime + " ~ " + endTime);
        System.out.println("price: " + price);
        System.out.println("phone: " + phone);
        System.out.println("imageUrl: " + imageUrl);
        System.out.println("description: " + description);

        showAlert("스터디 카페가 성공적으로 등록되었습니다!");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}