// 작성자 : 이원석, 방대혁
package org.example.stayd.domain.reservation.controller;

import static org.example.stayd.common.FXUtils.showAlert;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.example.stayd.common.FXUtils;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.service.ReservationService;

public class ReservationStatusController {

    @FXML
    private BarChart<String, Number> reservationBarChart;  // 예약 현황을 보여줄 바 차트
    @FXML
    private CategoryAxis xAxis;  // x축: 시간대
    @FXML
    private DatePicker datePicker;  // 날짜 선택을 위한 DatePicker

    private final ReservationService reservationService;

    /**
     * 생성자: ReservationService 객체 초기화
     */
    public ReservationStatusController() {
        this.reservationService = new ReservationService();
    }

    /**
     * 날짜 선택 시 호출되는 메서드 사용자가 선택한 날짜에 해당하는 예약 현황을 조회하고, 차트를 업데이트
     */
    public void loadReservationStatus() {
        LocalDate localDate = datePicker.getValue();  // DatePicker에서 선택된 날짜

        // 날짜가 선택되었는지 확인
        if (localDate != null) {
            // LocalDate를 java.sql.Date로 변환
            Date selectedDate = Date.valueOf(localDate);

            try {
                // 선택된 날짜에 해당하는 예약 현황 데이터 가져오기
                List<ReservationDTO> reservationList = reservationService.getReservationStatusByLoggedInUser(
                        selectedDate);

                // 예약 리스트가 비어 있는지 확인
                if (reservationList.isEmpty()) {
                    showAlert(null, Alert.AlertType.WARNING, "알림", "예약 데이터가 없습니다.");
                } else {
                    // 예약 현황을 BarChart로 업데이트
                    updateChart(reservationList);
                }
            } catch (SQLException e) {
                // 데이터베이스 오류 시 알림 표시
                showAlert(null, Alert.AlertType.ERROR, "오류", "예약 데이터를 가져오는 중 오류가 발생했습니다.");
            }
        } else {
            // 날짜를 선택하지 않았을 경우 경고 알림
            showAlert(null, Alert.AlertType.WARNING, "날짜 선택", "날짜를 선택해주세요.");
        }
    }

    /**
     * 예약 현황 차트를 업데이트하는 메서드 예약 데이터를 바탕으로 24시간 동안의 예약 건수를 시각화
     */
    private void updateChart(List<ReservationDTO> reservationList) {
        int[] reservationCounts = new int[24];  // 24시간 동안 예약 건수를 저장할 배열

        // 각 예약 항목에 대해 시간대별로 예약 건수 증가
        for (ReservationDTO reservation : reservationList) {
            int startHour = reservation.getUsageStartedAt();
            int endHour = reservation.getUsageEndedAt();

            // 예약 시작 시간부터 종료 시간까지 예약 건수 증가
            if (startHour <= endHour) {
                for (int i = startHour; i <= endHour; i++) {
                    reservationCounts[i]++;
                }
            } else {
                // 예약 시간이 자정을 넘는 경우
                for (int i = startHour; i < 24; i++) {
                    reservationCounts[i]++;
                }
                for (int i = 0; i <= endHour; i++) {
                    reservationCounts[i]++;
                }
            }
        }

        // x축에 24시간을 나열 (시간대)
        xAxis.setCategories(javafx.collections.FXCollections.observableArrayList(
                "00:00~01:00", "01:00~02:00", "02:00~03:00", "03:00~04:00", "04:00~05:00", "05:00~06:00",
                "06:00~07:00", "07:00~08:00", "08:00~09:00", "09:00~10:00", "10:00~11:00", "11:00~12:00",
                "12:00~13:00", "13:00~14:00", "14:00~15:00", "15:00~16:00", "16:00~17:00", "17:00~18:00",
                "18:00~19:00", "19:00~20:00", "20:00~21:00", "21:00~22:00", "22:00~23:00", "23:00~00:00"
        ));

        // 차트 데이터 설정
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // 각 시간대별 예약 건수 추가
        for (int i = 0; i < 24; i++) {
            String timeRange = xAxis.getCategories().get(i);  // "00:00~01:00", "01:00~02:00" 등
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
        reservationBarChart.getData().clear();  // 기존 데이터 삭제
        reservationBarChart.getData().add(series);  // 새 데이터 추가

        reservationBarChart.setLegendVisible(false); // 범례 숨김
    }

    /**
     * 예약 건수에 따른 색상 반환 예약 건수에 따라 색상을 다르게 표시 (예: 예약 건수가 많으면 빨간색, 적으면 대표 색)
     */
    private Color getColorBasedOnCount(int count) {
        if (count > 10) {
            return Color.web("#4CAF4F"); // 예약 건수 5건 이상 빨간색
        } else {
            return Color.web("#A8D6AA"); // 평소엔 stayD 대표색
        }
    }

    /**
     * 색상을 hex 값으로 변환
     *
     * @param color Color 객체
     * @return hex 색상 코드
     */
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * 마우스가 버튼 위에 올라갔을 때 색상 변경
     *
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseEnter(MouseEvent event) {
        FXUtils.handleMouseEnter(event); // FXUtils에서 처리
    }

    /**
     * 마우스가 버튼을 벗어났을 때 원래 색상으로 복원
     *
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseExit(MouseEvent event) {
        FXUtils.handleMouseExit(event); // FXUtils에서 처리
    }
}