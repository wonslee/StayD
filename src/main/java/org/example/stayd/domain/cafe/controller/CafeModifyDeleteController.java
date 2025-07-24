package org.example.stayd.domain.cafe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.stayd.common.FXUtils;
import org.example.stayd.common.SessionManager;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.service.CafeService;

import javax.mail.Session;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CafeModifyDeleteController implements Initializable {

    // FXML 필드들
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
    private TextField cafeIdField; // 카페 ID 저장용 숨겨진 필드

    // 요일 토글 버튼들
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

    // 시간 조정 버튼들
    @FXML
    private Button startTimeUpButton;
    @FXML
    private Button startTimeDownButton;
    @FXML
    private Button endTimeUpButton;
    @FXML
    private Button endTimeDownButton;

    // 가격 조정 버튼들
    @FXML
    private Button priceUpButton;
    @FXML
    private Button priceDownButton;

    // 수정/삭제 버튼들
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Font modifyButtonFont;
    @FXML
    private Font deleteButtonFont;

    // 내부 변수들
    private final Set<String> selectedDays = new HashSet<>();
    private final Map<ToggleButton, String> dayButtonMap = new HashMap<>();
    private CafeService cafeService;
    private Long currentCafeId; // 현재 카페 ID
    //    private final Long DUMMY_OWNER_ID = 5L; // 더미 사용자 ID
    private final Long OWNER_ID = (long) SessionManager.getInstance().getLoggedInUser()
        .getUser_id();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cafeService = new CafeService();
        initializeDayButtons();
        initializeCharacterCount();

//        // 테스트용 카페 데이터 로드 (실제로는 외부에서 setCafeId 호출)
//        loadCafeData(28L); // DB에 있는 카페 ID로 테스트

        // 기본값 설정
        charCountLabel.setText("0/200");
    }

    /**
     * 외부에서 카페 ID를 설정하는 메서드 현재는 NavBar에서 이동시 사용중
     */
    public void setCafeId(Long cafeId) {
        this.currentCafeId = cafeId;
        loadCafeData(cafeId);
    }

    /**
     * 카페 데이터를 로드하여 필드에 설정
     *
     * @param cafeId 카페 ID
     */
    public void loadCafeData(Long cafeId) {
        System.out.println("=== loadCafeData 시작 ===");
        System.out.println("전달받은 cafeId: " + cafeId);
        try {
            currentCafeId = cafeId;
            cafeIdField.setText(String.valueOf(cafeId));
            System.out.println("cafeService.getCafeDetail 호출 전");
            // DB에서 카페 데이터 가져오기
            CafeDto.DetailResponse cafe = cafeService.getCafeDetail(cafeId);
            System.out.println(
                "cafeService.getCafeDetail 호출 후, 결과: " + (cafe != null ? "성공" : "null"));
            if (cafe == null) {
                showAlert(Alert.AlertType.ERROR, "오류", "해당 카페를 찾을 수 없습니다.");
                return;
            }

            // 필드에 데이터 설정
            cafeNameField.setText(cafe.getName());
            locationField.setText(cafe.getAddress());
            phoneField.setText(cafe.getPhoneNumber());
            imageUrlField.setText(cafe.getImageUrl());
            descriptionArea.setText(cafe.getDescription());
            priceField.setText(String.valueOf(cafe.getPricePerHour()));

            // 운영시간 설정
            startTimeField.setText(String.format("%02d:00", cafe.getOperatingStartHour()));
            endTimeField.setText(String.format("%02d:00", cafe.getOperatingEndHour()));

            // 영업일 설정
            selectedDays.clear();
            Set<String> businessDays = new HashSet<>(cafe.getOperatingDays());

            for (Map.Entry<ToggleButton, String> entry : dayButtonMap.entrySet()) {
                ToggleButton button = entry.getKey();
                String day = entry.getValue();

                if (businessDays.contains(day)) {
                    button.setSelected(true);
                    button.setOpacity(1.0);
                    selectedDays.add(day);
                } else {
                    button.setSelected(false);
                    button.setOpacity(0.5);
                }
            }

            updateSelectedDaysField();
            updateCharacterCount();

            System.out.println("카페 데이터 로드 완료: " + cafe.getName());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "카페 데이터 로드 중 오류가 발생했습니다: " + e.getMessage());
        }
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
        // TextArea의 입력값이 변경될 때마다 글자 수를 업데이트하기 위한 리스너 설정
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
            LocalTime currentTime = LocalTime.parse(timeField.getText(),
                DateTimeFormatter.ofPattern("HH:mm"));
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
            int newPrice = Math.max(1000, currentPrice + amount); // 최소 1000원
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

    // 수정 버튼 이벤트 (DB 연동)
    @FXML
    private void modifyCafe(ActionEvent event) {
        if (!validateInput()) { // 빈 입력값 없도록.
            return;
        }

        try {
            // 수정 요청 데이터 생성
            CafeDto.UpdateRequest request = new CafeDto.UpdateRequest();
            request.setCafeId(currentCafeId);
            request.setName(cafeNameField.getText().trim());
            request.setAddress(locationField.getText().trim());
            request.setPricePerHour(Integer.parseInt(priceField.getText()));
            request.setDescription(descriptionArea.getText().trim());
            request.setPhoneNumber(phoneField.getText().trim());
            request.setImageUrl(imageUrlField.getText().trim());

            // 운영일 리스트로 변환
            request.setOperatingDays(new ArrayList<>(selectedDays));

            // 운영시간 파싱
            LocalTime startTime = LocalTime.parse(startTimeField.getText(),
                DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endTimeField.getText(),
                DateTimeFormatter.ofPattern("HH:mm"));
            request.setOperatingStartHour(startTime.getHour());
            request.setOperatingEndHour(endTime.getHour());

            // Service를 통해 카페 정보 수정
            CafeDto.UpdateResponse response = cafeService.updateCafe(request, OWNER_ID);

            if (response.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "수정 완료", response.getMessage());
                // FXUtils로 페이지 이동
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXUtils.navigateToPage(stage, SceneConfig.RESERVATION_STATUS_FXML, "페이지 이동");

            } else {
                showAlert(Alert.AlertType.ERROR, "수정 실패", response.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "수정 실패", "카페 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 삭제 버튼 이벤트 (DB 연동)
    @FXML
    private void deleteCafe(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("카페 삭제 확인");
        confirmAlert.setHeaderText("정말로 이 스터디 카페를 삭제하시겠습니까?");
        confirmAlert.setContentText("삭제된 카페 정보는 복구할 수 없습니다.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Service를 통해 카페 삭제
                CafeDto.DeleteResponse response = cafeService.deleteCafe(currentCafeId, OWNER_ID);

                if (response.isSuccess()) {
                    showAlert(Alert.AlertType.INFORMATION, "삭제 완료", response.getMessage());
                    // FXUtils로 페이지 이동
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    FXUtils.navigateToPage(stage, SceneConfig.RESERVATION_STATUS_FXML, "페이지 이동");
                } else {
                    showAlert(Alert.AlertType.ERROR, "삭제 실패", response.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
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
            if (price < 1000) {
                errors.add("가격은 1000원 이상이어야 합니다.");
            }
        } catch (NumberFormatException e) {
            errors.add("올바른 가격을 입력해주세요.");
        }

        // 시간 검증
        try {
            LocalTime startTime = LocalTime.parse(startTimeField.getText(),
                DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endTimeField.getText(),
                DateTimeFormatter.ofPattern("HH:mm"));

            if (!startTime.isBefore(endTime)) {
                errors.add("종료 시간은 시작 시간보다 늦어야 합니다.");
            }
        } catch (Exception e) {
            errors.add("올바른 시간 형식을 입력해주세요. (예: 09:00)");
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
        modifyButton.setStyle(
            "-fx-background-color: #4caf4f; -fx-font-weight: bold; -fx-background-radius: 10;");
        modifyButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onModifyButtonExit(MouseEvent event) {
        modifyButton.setStyle(
            "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        modifyButton.setTextFill(javafx.scene.paint.Color.web("#4caf4f"));
    }

    @FXML
    private void onDeleteButtonEnter(MouseEvent event) {
        deleteButton.setStyle(
            "-fx-background-color: #ff6b6b; -fx-font-weight: bold; -fx-background-radius: 10; ");
        deleteButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onDeleteButtonExit(MouseEvent event) {
        deleteButton.setStyle(
            "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
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

    /**
     * 마우스가 버튼 위에 올라갔을 때 색상 변경
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseEnter(MouseEvent event) {
        FXUtils.handleMouseEnter(event); // FXUtils에서 처리
    }

    /**
     * 마우스가 버튼을 벗어났을 때 원래 색상으로 복원
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseExit(MouseEvent event) {
        FXUtils.handleMouseExit(event); // FXUtils에서 처리
    }
}