package org.example.stayd.domain.reservation.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import org.example.stayd.domain.cafe.dto.CafeDTO;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.model.Seat;
import org.example.stayd.domain.reservation.service.ReservationService;
import org.example.stayd.domain.user.dto.UserDTO;

/**
 *
 */
public class ReservationCreateController {
    private CafeDTO cafeDto;
    private UserDTO user;


    /* ───────────── FXML 바인딩 │ 기본 정보 영역 ───────────── */
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> startHourCombo;
    @FXML
    private ComboBox<String> endHourCombo;
    @FXML
    private Label totalPriceLabel;

    /* ───────────── FXML 바인딩 │ 좌석 영역 ───────────── */
    @FXML
    private GridPane seatGrid;
    @FXML
    private Button reserveBtn;
    @FXML
    private Label statusLabel;

    /* ───────────── 런타임 필드 ───────────── */
    private final ReservationService service = new ReservationService();
    private Seat selectedSeat;
    //    TODO: service로부터 DB 쳐서 가져오기.
    private long cafeId = 1L;   // 실제 페이지 진입 시 파라미터로 주입한다고 가정
    private long currentUserId = 1L;  // 로그인 세션 가정
    private int pricePerHour = 10000;    // 로드 시 Cafe 정보에서 가져옴

    /* ───────────── 초기화 ───────────── */
    @FXML
    private void initialize() {
        initHourCombos();
        loadSelectionSeats();
        fetchCafePrice();

        /* 가격 계산용 리스너 */
        ChangeListener<Object> calc = (obs, o, n) -> calcTotalPrice();
        datePicker.valueProperty().addListener(calc);
        startHourCombo.valueProperty().addListener(calc);
        endHourCombo.valueProperty().addListener(calc);

//        TODO: ReservationDTO를 만들어서 createReservation() 호출
//        reserveBtn.setOnAction(e -> createReservation());
    }

    /* 0~23시 콤보 채우기 */
    private void initHourCombos() {
        ObservableList<String> hours = FXCollections.observableArrayList(
                IntStream.range(0, 24)
                        .mapToObj(i -> String.format("%02d:00", i))
                        .toList());
        startHourCombo.setItems(hours);
        endHourCombo.setItems(hours);

        startHourCombo.getSelectionModel().select("09:00");
        endHourCombo.getSelectionModel().select("18:00");
    }

    /* DB에서 좌석 목록 로드 & 버튼 렌더링 */
    private void loadSelectionSeats() {
        try {
            List<Seat> seats = service.getSeats(cafeId);
            seatGrid.getChildren().clear();
            selectedSeat = null;

            final int cols = 6;
            for (int i = 0; i < seats.size(); i++) {
                Seat s = seats.get(i);
                ToggleButton btn = new ToggleButton(s.getSeatNumber());
                btn.setPrefSize(80, 40);
                btn.setDisable(!s.isAvailable());
                btn.setAlignment(Pos.CENTER);

                btn.selectedProperty().addListener((o, was, is) -> {
                    if (is) {           // 새로 선택
                        selectedSeat = s;
                        seatGrid.getChildren().forEach(n -> {
                            if (n instanceof ToggleButton tb && tb != btn) {
                                tb.setSelected(false);
                            }
                        });
                    } else if (selectedSeat == s) {  // 선택 해제
                        selectedSeat = null;
                    }
                });
                seatGrid.add(btn, i % cols, i / cols);
            }
        } catch (SQLException ex) {
            statusLabel.setText("좌석 로딩 실패: " + ex.getMessage());
        }
    }

    /* 카페 가격 단가 로드 (간단히 1회 호출) */
    private void fetchCafePrice() {
        try {
            // TODO: service 에서 가져오기
            pricePerHour = 10000;   // 필요 시 ReservationService에 메서드 추가
            calcTotalPrice();
            if (false) {
                throw new SQLException("lala");
            }
        } catch (SQLException ignore) { /* 가격 없으면 0 처리 */ }
    }

    /* 총 금액 계산 = 단가 × (종료 - 시작) */
    private void calcTotalPrice() {
        try {
            int start = parseHour(startHourCombo.getValue());
            int end = parseHour(endHourCombo.getValue());
            if (end <= start) {
                totalPriceLabel.setText("0");
                return;
            }
            int hours = end - start;
            int total = hours * pricePerHour;
            totalPriceLabel.setText(String.format("%,d", total));
        } catch (Exception ignore) {
            totalPriceLabel.setText("0");
        }
    }

    private int parseHour(String comboVal) {
        return comboVal == null ? 0 : Integer.parseInt(comboVal.substring(0, 2));
    }

    /* 예약 생성 호출 */
    private void createReservation(ReservationDTO reservationDTO) {
        if (selectedSeat == null) {
            statusLabel.setText("좌석을 선택하세요.");
            return;
        }
        LocalDate date = datePicker.getValue();
        if (date == null) {
            statusLabel.setText("날짜를 선택하세요.");
            return;
        }
        int startH = parseHour(startHourCombo.getValue());
        int endH = parseHour(endHourCombo.getValue());
        if (endH <= startH) {
            statusLabel.setText("종료 시간이 시작 시간보다 커야 합니다.");
            return;
        }

        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(startH, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(endH, 0));
        int total = (endH - startH) * pricePerHour;

        try {
            service.createReservation(
                    cafeId,
                    selectedSeat.getSeatId(),
                    currentUserId,
                    reservationDTO
            );
            statusLabel.setText("예약 완료!");
            loadSelectionSeats();                // UI 업데이트
        } catch (Exception ex) {
            statusLabel.setText("예약 실패: " + ex.getMessage());
        }
    }
}
