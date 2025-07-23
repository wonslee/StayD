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

public class ReservationStatusDayController {

    @FXML
    private BarChart<String, Number> dayReservationBarChart;
    @FXML
    private CategoryAxis dayOfWeekXAxis;

    @FXML
    private ComboBox<String> dayOfWeekComboBox;

    private ReservationService reservationService;

    public ReservationStatusDayController() {
        this.reservationService = new ReservationService();
    }

    // 화면 초기화 시 ComboBox에 요일 추가
    @FXML
    private void initialize() {
        dayOfWeekComboBox.getItems().addAll("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT");
    }

    // 요일별 예약 현황 조회
    @FXML
    private void loadDayOfWeekReservationStatus(ActionEvent event) {
        String selectedDay = dayOfWeekComboBox.getValue();  // ComboBox에서 선택된 요일

        if (selectedDay != null) {
            try {
                System.out.println("Selected Day: " + selectedDay);
                List<ReservationDTO> reservationList = reservationService.getReservationStatusByDay(selectedDay);
                System.out.println("Number of Reservations: " + reservationList.size());
                if (reservationList.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "알림", "예약 데이터가 없습니다.");
                } else {
                    updateDayOfWeekChart(reservationList);
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "오류", "예약 데이터를 가져오는 중 오류가 발생했습니다.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "요일 선택", "요일을 선택해주세요.");
        }
    }

    private void updateDayOfWeekChart(List<ReservationDTO> reservationList) {
        // 24시간 동안 예약 건수를 저장할 배열 (각 시간대별로 카운트)
        int[] reservationCounts = new int[24]; // 0:00~1:00, 1:00~2:00, ..., 23:00~00:00

        // 예약 데이터를 순회하며 예약 건수 카운트
        for (ReservationDTO reservation : reservationList) {
            // usage_started_at과 usage_ended_at을 이용하여 시간대별로 예약 건수 카운트
            int startHour = reservation.getUsageStartedAt();
            int endHour = reservation.getUsageEndedAt();

            // 시작 시간부터 끝 시간까지 예약 건수 카운트
            if (startHour <= endHour) {
                for (int i = startHour; i <= endHour; i++) {
                    reservationCounts[i]++;
                }
            } else {
                // 23시 이후로 넘어가는 예약 처리 (예: 23:30 - 01:30)
                for (int i = startHour; i < 24; i++) {
                    reservationCounts[i]++;
                }
                for (int i = 0; i <= endHour; i++) {
                    reservationCounts[i]++;
                }
            }
        }

        // 디버깅 로그: 예약 건수 출력
        for (int i = 0; i < 24; i++) {
            System.out.println("Hour: " + i + ":00 - " + (i + 1) + ":00, Reservations: " + reservationCounts[i]);
        }

        // X축에 24시간을 나열 (0:00~1:00, 1:00~2:00, ..., 23:00~00:00)
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

    // 예약 건수에 따른 색상 반환
    private Color getColorBasedOnCount(int count) {
        if (count > 5) {
            return Color.web("#4CAF4F"); // 예약 건수 5건 이상 빨간색
        } else {
            return Color.web("#A8D6AA"); // 평소엔 stayD 대표색
        }
    }

    // 색상을 hex 값으로 변환
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));
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