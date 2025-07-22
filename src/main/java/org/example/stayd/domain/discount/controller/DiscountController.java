package org.example.stayd.domain.discount.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.example.stayd.domain.discount.service.DiscountService;

import java.sql.Date;
import java.time.LocalDate;

public class DiscountController {

    @FXML
    private DatePicker discountDate;
    @FXML
    private ComboBox<Integer>  discountStartTime;
    @FXML
    private ComboBox<Integer>  discountEndTime;
    @FXML
    private TextField discountRate;

    private DiscountService discountService;

    public DiscountController() {
        this.discountService = new DiscountService();
    }

    // 화면 초기화 시 ComboBox에 0~23 시간 범위를 추가
    @FXML
    private void initialize() {
        // 0 ~ 23 시간을 ComboBox에 추가
        for (int i = 0; i < 24; i++) {
            discountStartTime.getItems().add(i);
            discountEndTime.getItems().add(i);
        }
    }

    // 할인 설정 저장 버튼 클릭 시 호출되는 메서드
    @FXML
    private void saveDiscountSettings() {
        LocalDate selectedDate = discountDate.getValue();

        if (selectedDate != null) {
            // 입력된 값 가져오기
            Integer startTime = discountStartTime.getValue();
            Integer endTime = discountEndTime.getValue();
            double discountRate = Double.parseDouble(this.discountRate.getText());

            // 유효성 체크
            if (startTime == null || endTime == null || startTime < 0 || startTime > 23 || endTime < 0 || endTime > 23 || discountRate < 0 || discountRate > 100) {
                showAlert(Alert.AlertType.ERROR, "입력 오류", "유효하지 않은 입력값입니다. 시간을 0~23 사이로, 할인율을 0~100 사이로 입력해주세요.");
                return;
            }

            // DiscountService를 사용해 DB에 저장
            Date selectedDateSql = Date.valueOf(selectedDate);
            boolean success = discountService.saveDiscountSettings(selectedDateSql, startTime, endTime, discountRate);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "저장 성공", "할인 설정이 저장되었습니다.");
            } else {
                showAlert(Alert.AlertType.ERROR, "저장 오류", "할인 설정 저장에 실패했습니다.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "날짜 선택", "날짜를 선택해주세요.");
        }
    }

    // 알림창 표시 함수
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}