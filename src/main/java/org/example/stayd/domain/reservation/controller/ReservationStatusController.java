package org.example.stayd.domain.reservation.controller;

import java.sql.SQLException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.example.stayd.common.FXUtils;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.service.ReservationService;

public class ReservationStatusController {

    @FXML
    private BarChart<String, Number> reservationBarChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private ReservationService reservationService;

    public ReservationStatusController() {
        this.reservationService = new ReservationService();
    }

    // 조회 버튼 클릭 시 호출되는 메서드
    public void loadReservationStatus() {
        try {
            // 로그인한 유저의 cafe_id에 해당하는 예약 현황 데이터 가져오기
            List<ReservationDTO> reservationList = reservationService.getReservationStatusByLoggedInUser();

            // 예약 리스트가 비어 있는지 확인
            if (reservationList.isEmpty()) {
                FXUtils.showAlert(null, javafx.scene.control.Alert.AlertType.WARNING, "알림", "예약 데이터가 없습니다.");
            } else {
                // 예약 현황을 BarChart로 업데이트
                updateChart(reservationList);
            }
        } catch (SQLException e) {
            FXUtils.showAlert(null, javafx.scene.control.Alert.AlertType.ERROR, "오류", "예약 데이터를 가져오는 중 오류가 발생했습니다.");
        }
    }

    // BarChart를 업데이트하는 메서드
    private void updateChart(List<ReservationDTO> reservationList) {
        // 24시간 동안 예약 건수를 저장할 배열
        int[] reservationCounts = new int[24];

        // 각 예약 항목에 대해 시간대별로 예약 건수 증가
        for (ReservationDTO reservation : reservationList) {
            int startHour = reservation.getUsageStartedAt();
            int endHour = reservation.getUsageEndedAt();

            // 시작 시간에서 종료 시간까지 모든 시간대에 대해 예약 건수 증가
            if (startHour <= endHour) {
                // 종료 시간이 시작 시간과 같거나 나중인 경우 (한 날 내에서 예약)
                for (int i = startHour; i <= endHour; i++) {
                    reservationCounts[i]++;
                }
            } else {
                // 예약이 자정을 넘는 경우 (예: 22:00 ~ 02:00)
                for (int i = startHour; i < 24; i++) {
                    reservationCounts[i]++;
                }
                for (int i = 0; i <= endHour; i++) {
                    reservationCounts[i]++;
                }
            }
        }

        // x축에 24시간을 나열
        xAxis.setCategories(javafx.collections.FXCollections.observableArrayList(
                "00:00~01:00", "01:00~02:00", "02:00~03:00", "03:00~04:00", "04:00~05:00", "05:00~06:00",
                "06:00~07:00", "07:00~08:00", "08:00~09:00", "09:00~10:00", "10:00~11:00", "11:00~12:00",
                "12:00~13:00", "13:00~14:00", "14:00~15:00", "15:00~16:00", "16:00~17:00", "17:00~18:00",
                "18:00~19:00", "19:00~20:00", "20:00~21:00", "21:00~22:00", "22:00~23:00", "23:00~00:00"
        ));

        // 차트 데이터 설정
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("예약 건수");

        // 각 시간대별 예약 건수 추가
        for (int i = 0; i < 24; i++) {
            String timeRange = xAxis.getCategories().get(i);  // "00:00~01:00", "01:00~02:00" 등
            XYChart.Data<String, Number> data = new XYChart.Data<>(timeRange, reservationCounts[i]);

            // 기존 막대에 텍스트 추가
            Text text = new Text(Integer.toString(reservationCounts[i]));  // 예약 건수를 텍스트로 표시
            text.setStyle("-fx-font-size: 14; -fx-fill: black; -fx-stroke: white;");  // 텍스트 스타일 설정

            // 텍스트를 막대 위에 배치
            StackPane stack = new StackPane();
            stack.getChildren().add(text);  // 텍스트를 스택에 추가
            data.setNode(stack);  // 막대 노드에 텍스트를 배치

            // 데이터 추가
            series.getData().add(data);
        }

        // 차트에 데이터 추가
        reservationBarChart.getData().clear();  // 기존 데이터 삭제
        reservationBarChart.getData().add(series);  // 새 데이터 추가
    }
}