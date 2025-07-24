package org.example.stayd.domain.discount.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.example.stayd.domain.discount.dto.DiscountDTO;
import org.example.stayd.domain.discount.service.DiscountService;

import java.sql.Date;
import java.time.LocalDate;

import static org.example.stayd.common.FXUtils.showAlert;

public class DiscountController {

    @FXML
    private DatePicker discountDate;  // 할인 설정 날짜
    @FXML
    private ComboBox<Integer> discountStartTime;  // 할인 시작 시간
    @FXML
    private ComboBox<Integer> discountEndTime;    // 할인 종료 시간
    @FXML
    private TextField discountRate;  // 할인율

    private DiscountService discountService;  // DiscountService 객체

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

    /**
     * 할인 설정 저장 버튼 클릭 시 호출되는 메서드
     * 할인 날짜, 시작 시간, 종료 시간, 할인율을 DB에 저장
     */
    // 할인 설정 저장 버튼 클릭 시 호출되는 메서드
    @FXML
    private void saveDiscountSettings() {
        // 입력된 값 가져오기
        Integer startTime = discountStartTime.getValue();
        Integer endTime = discountEndTime.getValue();
        double discountRateValue = Double.parseDouble(this.discountRate.getText());

        // 선택된 날짜가 null이 아닌지 확인
        LocalDate selectedDate = discountDate.getValue();  // 사용자로부터 선택된 날짜

        if (selectedDate != null) {
            // 요일 값은 LocalDate에서 가져온다
            String dayOfWeek = selectedDate.getDayOfWeek().name().substring(0, 3);  // MON, TUE, WED 등

            // 유효성 체크 (시간 범위와 할인율이 올바른지 확인)
            if (startTime == null || endTime == null || startTime < 0 || startTime > 23 || endTime < 0 || endTime > 23 || discountRateValue < 0 || discountRateValue > 100) {
                showAlert(null, Alert.AlertType.ERROR, "입력 오류", "유효하지 않은 입력값입니다. 시간을 0~23 사이로, 할인율을 0~100 사이로 입력해주세요.");
                return;
            }

            // DTO 객체 생성
            DiscountDTO discountDTO = new DiscountDTO(dayOfWeek, startTime, endTime, discountRateValue);

            // DiscountService를 사용해 DB에 저장
            boolean success = discountService.saveDiscountSettings(discountDTO);

            // 저장 성공 여부에 따라 알림 표시
            if (success) {
                showAlert(null, Alert.AlertType.INFORMATION, "저장 성공", "할인 설정이 저장되었습니다.");
            } else {
                showAlert(null, Alert.AlertType.ERROR, "저장 오류", "할인 설정 저장에 실패했습니다.");
            }
        } else {
            showAlert(null, Alert.AlertType.WARNING, "날짜 선택", "날짜를 선택해주세요.");
        }
    }
}