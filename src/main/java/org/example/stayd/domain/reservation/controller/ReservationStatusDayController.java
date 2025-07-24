package org.example.stayd.domain.reservation.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.service.ReservationService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.example.stayd.common.FXUtils.showAlert;

public class ReservationStatusDayController {

    @FXML
    private BarChart<String, Number> dayReservationBarChart;  // 요일별 예약 현황을 표시할 차트
    @FXML
    private CategoryAxis dayOfWeekXAxis;  // x축: 요일 (0~23시간)
    @FXML
    private ComboBox<String> dayOfWeekComboBox;  // 요일 선택을 위한 ComboBox

    private ReservationService reservationService;

    /**
     * 생성자: ReservationService 객체 초기화
     */
    public ReservationStatusDayController() {
        this.reservationService = new ReservationService();
    }

    /**
     * 화면 초기화 시 ComboBox에 요일 목록 추가
     */
    @FXML
    private void initialize() {
        dayOfWeekComboBox.getItems().addAll("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT");  // 요일 목록 추가
    }

    /**
     * 요일별 예약 현황을 조회하는 메서드
     * ComboBox에서 선택된 요일에 맞는 예약 데이터를 조회하여 차트에 반영
     */
    @FXML
    private void loadDayOfWeekReservationStatus(ActionEvent event) {
        String selectedDay = dayOfWeekComboBox.getValue();  // ComboBox에서 선택된 요일

        if (selectedDay != null) {
            try {
                // 선택된 요일에 해당하는 예약 데이터 조회
                List<ReservationDTO> reservationList = reservationService.getReservationStatusByDay(selectedDay);
                System.out.println("Number of Reservations: " + reservationList.size());

                if (reservationList.isEmpty()) {
                    // 예약 데이터가 없는 경우 경고창 표시
                    showAlert(null, Alert.AlertType.WARNING, "알림", "예약 데이터가 없습니다.");
                } else {
                    // 예약 데이터가 있으면 차트를 업데이트
                    updateDayOfWeekChart(reservationList);
                }
            } catch (Exception e) {
                // 예약 데이터를 가져오는 중 오류가 발생한 경우
                showAlert(null, Alert.AlertType.ERROR, "오류", "예약 데이터를 가져오는 중 오류가 발생했습니다.");
            }
        } else {
            // 요일을 선택하지 않은 경우 경고창 표시
            showAlert(null, Alert.AlertType.WARNING, "요일 선택", "요일을 선택해주세요.");
        }
    }

    /**
     * 요일별 예약 현황 차트를 업데이트하는 메서드
     * 예약 데이터를 바탕으로 24시간 동안 예약 건수를 시각화
     */
    private void updateDayOfWeekChart(List<ReservationDTO> reservationList) {
        int[] reservationCounts = new int[24];  // 24시간 동안 예약 건수를 저장할 배열

        // 각 예약 항목에 대해 시간대별로 예약 건수 증가
        for (ReservationDTO reservation : reservationList) {
            int startHour = reservation.getUsageStartedAt();
            int endHour = reservation.getUsageEndedAt();

            // 예약 시간이 시작 시간부터 끝 시간까지 포함하는 경우
            if (startHour <= endHour) {
                for (int i = startHour; i <= endHour; i++) {
                    reservationCounts[i]++;
                }
            } else {
                // 예약 시간이 자정을 넘는 경우 (예: 23:30 - 01:30)
                for (int i = startHour; i < 24; i++) {
                    reservationCounts[i]++;
                }
                for (int i = 0; i <= endHour; i++) {
                    reservationCounts[i]++;
                }
            }
        }

        // x축에 24시간을 나열 (시간대: 00:00~01:00, 01:00~02:00 등)
        dayOfWeekXAxis.setCategories(javafx.collections.FXCollections.observableArrayList(
                "00:00~01:00", "01:00~02:00", "02:00~03:00", "03:00~04:00", "04:00~05:00", "05:00~06:00",
                "06:00~07:00", "07:00~08:00", "08:00~09:00", "09:00~10:00", "10:00~11:00", "11:00~12:00",
                "12:00~13:00", "13:00~14:00", "14:00~15:00", "15:00~16:00", "16:00~17:00", "17:00~18:00",
                "18:00~19:00", "19:00~20:00", "20:00~21:00", "21:00~22:00", "22:00~23:00", "23:00~00:00"
        ));

        // 차트 데이터 설정
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // 각 시간대별 예약 건수 추가
        for (int i = 0; i < 24; i++) {
            String timeRange = dayOfWeekXAxis.getCategories().get(i);  // "00:00~01:00", "01:00~02:00" 등
            XYChart.Data<String, Number> data = new XYChart.Data<>(timeRange, reservationCounts[i]);

            // 예약 건수에 따른 색상 설정
            Color barColor = getColorBasedOnCount(reservationCounts[i]);

            // 텍스트를 막대 위에 배치
            Text text = new Text(Integer.toString(reservationCounts[i]));  // 예약 건수를 텍스트로 표시
            text.setStyle("-fx-font-size: 14; -fx-fill: black; -fx-stroke: white;");  // 텍스트 스타일 설정

            // 막대 색상 적용
            StackPane stack = new StackPane();
            stack.getChildren().addAll(text);  // 텍스트만 스택에 추가
            data.setNode(stack);  // 막대 노드에 텍스트 배치

            // 스타일을 적용하는 코드: 노드를 설정한 후 스타일 적용
            data.getNode().setStyle("-fx-bar-fill: " + colorToHex(barColor));  // 색상 적용

            // 데이터 추가
            series.getData().add(data);
        }

        // 차트에 데이터 추가
        dayReservationBarChart.getData().clear();  // 기존 데이터 삭제
        dayReservationBarChart.getData().add(series);  // 새 데이터 추가

        dayReservationBarChart.setLegendVisible(false); // 범례 숨김
    }

    /**
     * 예약 건수에 따른 색상 반환
     * 예약 건수에 따라 색상을 다르게 표시 (예: 예약 건수가 많으면 빨간색, 적으면 대표 색)
     */
    private Color getColorBasedOnCount(int count) {
        if (count > 5) {
            return Color.web("#4CAF4F"); // 예약 건수 5건 이상 빨간색
        } else {
            return Color.web("#A8D6AA"); // 평소엔 stayD 대표색
        }
    }

    /**
     * 색상을 hex 값으로 변환
     * @param color Color 객체
     * @return hex 색상 코드
     */
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }
}