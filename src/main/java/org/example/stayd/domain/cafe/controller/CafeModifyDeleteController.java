package org.example.stayd.domain.cafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CafeModifyDeleteController implements Initializable {

    // FXML 필드들
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
    @FXML private TextField cafeIdField; // 카페 ID 저장용 숨겨진 필드

    // 요일 토글 버튼들
    @FXML private ToggleButton mondayButton;
    @FXML private ToggleButton tuesdayButton;
    @FXML private ToggleButton wednesdayButton;
    @FXML private ToggleButton thursdayButton;
    @FXML private ToggleButton fridayButton;
    @FXML private ToggleButton saturdayButton;
    @FXML private ToggleButton sundayButton;

    // 시간 조정 버튼들
    @FXML private Button startTimeUpButton;
    @FXML private Button startTimeDownButton;
    @FXML private Button endTimeUpButton;
    @FXML private Button endTimeDownButton;

    // 가격 조정 버튼들
    @FXML private Button priceUpButton;
    @FXML private Button priceDownButton;

    // 수정/삭제 버튼들
    @FXML private Button modifyButton;
    @FXML private Button deleteButton;
    @FXML private Font modifyButtonFont;
    @FXML private Font deleteButtonFont;

    // 내부 변수들
    private final Set<String> selectedDays = new HashSet<>();
    private final Map<ToggleButton, String> dayButtonMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDayButtons();
        initializeCharacterCount();

        // 테스트용 샘플 데이터 로드 (실제 구현 시 삭제)
        loadCafeData(1L);

        // 기본값 설정
        startTimeField.setText("09:00");
        endTimeField.setText("18:00");
        priceField.setText("1000");
        charCountLabel.setText("0/200");
    }

    /**
     * 카페 데이터를 로드하여 필드에 설정
     * @param cafeId 카페 ID
     */
    public void loadCafeData(Long cafeId) {
        // TODO: 실제 구현 시 Service를 통해 카페 데이터를 가져와야 함
        // 현재는 예시 데이터로 설정

        cafeIdField.setText(String.valueOf(cafeId));

        // 예시 데이터 (실제로는 Service에서 가져올 데이터)
        cafeNameField.setText("스터디 카페 예시");
        locationField.setText("서울시 강남구");
        phoneField.setText("010-1234-5678");
        imageUrlField.setText("https://example.com/image.jpg");
        descriptionArea.setText("편안하고 조용한 스터디 카페입니다.");
        startTimeField.setText("08:00");
        endTimeField.setText("22:00");
        priceField.setText("2000");

        // 영업일 설정 (예: 월,화,수,목,금)
        Set<String> businessDays = Set.of("월", "화", "수", "목", "금");
        for (Map.Entry<ToggleButton, String> entry : dayButtonMap.entrySet()) {
            if (businessDays.contains(entry.getValue())) {
                entry.getKey().setSelected(true);
                entry.getKey().setOpacity(1.0);
                selectedDays.add(entry.getValue());
            }
        }
        updateSelectedDaysField();
        updateCharacterCount();

        /*
        // 실제 구현 시 사용할 코드 예시:
        CafeService cafeService = new CafeService();
        Cafe cafe = cafeService.findById(cafeId);

        if (cafe != null) {
            cafeNameField.setText(cafe.getName());
            locationField.setText(cafe.getLocation());
            phoneField.setText(cafe.getPhone());
            imageUrlField.setText(cafe.getImageUrl());
            descriptionArea.setText(cafe.getDescription());
            startTimeField.setText(cafe.getStartTime());
            endTimeField.setText(cafe.getEndTime());
            priceField.setText(String.valueOf(cafe.getPrice()));

            // 영업일 설정
            Set<String> businessDays = cafe.getBusinessDays();
            // ... 영업일 토글 버튼 설정 로직
        }
        */
    }

    private void initializeDayButtons() {
        dayButtonMap.put(mondayButton, "월");
        dayButtonMap.put(tuesdayButton, "화");
        dayButtonMap.put(wednesdayButton, "수");
        dayButtonMap.put(thursdayButton, "목");
        dayButtonMap.put(fridayButton, "금");
        dayButtonMap.put(saturdayButton, "토");
        dayButtonMap.put(sundayButton, "일");
    }

    private void initializeCharacterCount() {
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateCharacterCount();
        });
    }

    // 요일 토글 이벤트
    @FXML
    private void toggleDay() {
        selectedDays.clear();
        for (Map.Entry<ToggleButton, String> entry : dayButtonMap.entrySet()) {
            ToggleButton button = entry.getKey();
            String day = entry.getValue();

            if (button.isSelected()) {
                selectedDays.add(day);
                button.setOpacity(1.0);
            } else {
                button.setOpacity(0.5);
            }
        }
        updateSelectedDaysField();
    }

    private void updateSelectedDaysField() {
        selectedDaysField.setText(String.join(",", selectedDays));
    }

    // 시간 조정 메서드들
    @FXML
    private void increaseStartTime() {
        adjustTime(startTimeField, 1);
    }

    @FXML
    private void decreaseStartTime() {
        adjustTime(startTimeField, -1);
    }

    @FXML
    private void increaseEndTime() {
        adjustTime(endTimeField, 1);
    }

    @FXML
    private void decreaseEndTime() {
        adjustTime(endTimeField, -1);
    }

    private void adjustTime(TextField timeField, int hours) {
        try {
            LocalTime currentTime = LocalTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime newTime = currentTime.plusHours(hours);
            timeField.setText(newTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } catch (Exception e) {
            System.err.println("시간 조정 중 오류 발생: " + e.getMessage());
        }
    }

    // 가격 조정 메서드들
    @FXML
    private void increasePrice() {
        adjustPrice(500);
    }

    @FXML
    private void decreasePrice() {
        adjustPrice(-500);
    }

    private void adjustPrice(int amount) {
        try {
            int currentPrice = Integer.parseInt(priceField.getText());
            int newPrice = Math.max(0, currentPrice + amount);
            priceField.setText(String.valueOf(newPrice));
        } catch (NumberFormatException e) {
            System.err.println("가격 조정 중 오류 발생: " + e.getMessage());
        }
    }

    // 전화번호 포맷팅
    @FXML
    private void formatPhoneNumber(KeyEvent event) {
        String text = phoneField.getText();
        text = text.replaceAll("[^0-9]", "");

        if (text.length() > 11) {
            text = text.substring(0, 11);
        }

        if (text.length() >= 7) {
            text = text.substring(0, 3) + "-" + text.substring(3, 7) + "-" + text.substring(7);
        } else if (text.length() >= 3) {
            text = text.substring(0, 3) + "-" + text.substring(3);
        }

        phoneField.setText(text);
        phoneField.positionCaret(text.length());
    }

    // 설명 글자수 제한 및 카운트
    @FXML
    private void limitDescription(KeyEvent event) {
        String text = descriptionArea.getText();
        if (text.length() > 200) {
            descriptionArea.setText(text.substring(0, 200));
            descriptionArea.positionCaret(200);
        }
        updateCharacterCount();
    }

    private void updateCharacterCount() {
        int length = descriptionArea.getText().length();
        charCountLabel.setText(length + "/200");

        if (length > 180) {
            charCountLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 10px;");
        } else {
            charCountLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px;");
        }
    }

    // 수정 버튼 이벤트
    @FXML
    private void modifyCafe() {
        if (!validateInput()) {
            return;
        }

        try {
            // TODO: 실제 구현 시 Service를 통해 카페 정보 수정
            Long cafeId = Long.parseLong(cafeIdField.getText());

            /*
            // 실제 구현 예시:
            CafeModifyRequest request = CafeModifyRequest.builder()
                .id(cafeId)
                .name(cafeNameField.getText())
                .location(locationField.getText())
                .phone(phoneField.getText())
                .imageUrl(imageUrlField.getText())
                .description(descriptionArea.getText())
                .startTime(startTimeField.getText())
                .endTime(endTimeField.getText())
                .price(Integer.parseInt(priceField.getText()))
                .businessDays(selectedDays)
                .build();

            CafeService cafeService = new CafeService();
            cafeService.modifyCafe(request);
            */

            showAlert(Alert.AlertType.INFORMATION, "수정 완료", "스터디 카페 정보가 성공적으로 수정되었습니다.");
            closeWindow();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "수정 실패", "카페 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 삭제 버튼 이벤트
    @FXML
    private void deleteCafe() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("카페 삭제 확인");
        confirmAlert.setHeaderText("정말로 이 스터디 카페를 삭제하시겠습니까?");
        confirmAlert.setContentText("삭제된 카페 정보는 복구할 수 없습니다.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // TODO: 실제 구현 시 Service를 통해 카페 삭제
                Long cafeId = Long.parseLong(cafeIdField.getText());

                /*
                // 실제 구현 예시:
                CafeService cafeService = new CafeService();
                cafeService.deleteCafe(cafeId);
                */

                showAlert(Alert.AlertType.INFORMATION, "삭제 완료", "스터디 카페가 성공적으로 삭제되었습니다.");
                closeWindow();

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "삭제 실패", "카페 삭제 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
    }

    // 입력값 검증
    private boolean validateInput() {
        List<String> errors = new ArrayList<>();

        if (cafeNameField.getText().trim().isEmpty()) {
            errors.add("카페 이름을 입력해주세요.");
        }

        if (locationField.getText().trim().isEmpty()) {
            errors.add("위치를 입력해주세요.");
        }

        if (selectedDays.isEmpty()) {
            errors.add("영업일을 하나 이상 선택해주세요.");
        }

        if (phoneField.getText().trim().isEmpty()) {
            errors.add("전화번호를 입력해주세요.");
        }

        try {
            int price = Integer.parseInt(priceField.getText());
            if (price <= 0) {
                errors.add("가격은 0보다 큰 값이어야 합니다.");
            }
        } catch (NumberFormatException e) {
            errors.add("올바른 가격을 입력해주세요.");
        }

        if (!errors.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "입력 오류", String.join("\n", errors));
            return false;
        }

        return true;
    }

    // 버튼 호버 효과들
    @FXML
    private void onModifyButtonEnter(MouseEvent event) {
        modifyButton.setStyle("-fx-background-color: #4caf4f; -fx-font-weight: bold; -fx-background-radius: 10;");
        modifyButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onModifyButtonExit(MouseEvent event) {
        modifyButton.setStyle("-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        modifyButton.setTextFill(javafx.scene.paint.Color.web("#4caf4f"));
    }

    @FXML
    private void onDeleteButtonEnter(MouseEvent event) {
        deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-font-weight: bold; -fx-background-radius: 10; ");
        deleteButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onDeleteButtonExit(MouseEvent event) {
        deleteButton.setStyle("-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        deleteButton.setTextFill(javafx.scene.paint.Color.web("#ff6b6b"));
    }

    // 유틸리티 메서드들
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) modifyButton.getScene().getWindow();
        stage.close();
    }


}