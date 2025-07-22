package org.example.stayd.domain.reservation.controller;

import static org.example.stayd.common.BusinessLogicConstants.DEFAULT_SEAT_COLS;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import lombok.NoArgsConstructor;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.dto.CafeDto.DetailResponse;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.model.DayOfWeek;
import org.example.stayd.domain.reservation.model.Reservation;
import org.example.stayd.domain.reservation.model.Seat;
import org.example.stayd.domain.reservation.service.ReservationService;

@NoArgsConstructor
public class ReservationCreateController {

    /* FXML */
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Integer> startCombo, endCombo;
    @FXML
    private Label totalPriceLabel, statusLabel;
    @FXML
    private GridPane seatGrid;
    @FXML
    private Button reserveBtn;

    /* 주입될 DTO */
    private CafeDto.DetailResponse cafe;

    /* 서비스 & 상태 */
    private final ReservationService service = new ReservationService();
    private Seat selectedSeat;

    public ReservationCreateController(DetailResponse cafe) {
        this.cafe = cafe;
    }

    /* 초기화는 FXML 로드 직후 호출 */
    @FXML
    public void initialize() {
        initTimeCombos();
        datePicker.setOnAction(e -> recalc());
        startCombo.valueProperty().addListener((obs, o, n) -> recalc());
        endCombo.valueProperty().addListener((obs, o, n) -> recalc());
        reserveBtn.setOnAction(e -> makeReservation());

//        TODO: cafe 존재하지 않을 경우 예외 처리

        if (cafe != null) {
            loadSeats();
        }
    }


    /* ───────── 시간 콤보 채우기 ───────── */
    private void initTimeCombos() {
        int open = 0, close = 24; // 운영시간 컬럼이 있으면 교체
        startCombo.getItems().setAll(IntStream.range(open, close).boxed().toList());
        endCombo.getItems().setAll(IntStream.rangeClosed(open + 1, close).boxed().toList());
    }

    //    TODO: CafeService로 이동
    /* ───────── 좌석 로딩 ───────── */
    private void loadSeats() {
        try {
            selectedSeat = null;
            List<Seat> seats = service.getSeats(cafe.getCafeId());

            for (int i = 0; i < seats.size(); i++) {
                Seat s = seats.get(i);
                ToggleButton btn = new ToggleButton(String.valueOf(s.getSeatNumber()));
                btn.setPrefSize(45, 32);
                btn.setDisable(!s.isAvailable());
                btn.setAlignment(Pos.CENTER);
                btn.setStyle("");

                if (!s.isAvailable()) {
                    btn.setStyle("-fx-background-color: #e71919;");
                } else {
                    btn.setStyle("-fx-background-color: #B3B3B3FF;");
                }

                seatGrid.getChildren().forEach(n -> {
                    if (n instanceof ToggleButton tb && tb != btn) {
                        tb.setSelected(false);
                    }
                });

                btn.selectedProperty().addListener((o, was, sel) -> {
                    if (sel) {
                        btn.setStyle("-fx-background-color: #4CAF50;");
                        selectedSeat = s;

                    } else if (selectedSeat == s) {
                        selectedSeat = null;
                    } else {
                        btn.setStyle("-fx-background-color: #BDBDBD;");
                    }
                });
                seatGrid.add(btn, i % DEFAULT_SEAT_COLS, i / DEFAULT_SEAT_COLS);
            }
        } catch (SQLException ex) {
            statusLabel.setText("좌석 로드 실패: " + ex.getMessage());
        }
    }

    /* ───────── 금액 계산 ───────── */
    private void recalc() {
        if (cafe == null) {
            return;
        }
        Integer sh = startCombo.getValue(), eh = endCombo.getValue();
        if (sh == null || eh == null || eh <= sh) {
            totalPriceLabel.setText("0");
            return;
        }
        int total = (eh - sh) * cafe.getPricePerHour();
        totalPriceLabel.setText(String.format("%,d", total));
    }

    /* ───────── 예약 실행 ───────── */
//    TODO: 예약 직후 예약 상세로 리다이렉션
    private void makeReservation() {
        if (cafe == null) {
            return;
        }

        LocalDate date = datePicker.getValue();
        Integer sh = startCombo.getValue(), eh = endCombo.getValue();

        if (date == null || sh == null || eh == null || eh <= sh) {
            statusLabel.setText("날짜/시간을 확인하세요.");
            return;
        }
        if (selectedSeat == null) {
            statusLabel.setText("좌석을 선택하세요.");
            return;
        }

        ReservationDTO dto = ReservationDTO.builder()
                .reservationDate(date)
                .usageStartedAt(sh)
                .usageEndedAt(eh)
                .dayOfWeek(DayOfWeek.valueOf(date.getDayOfWeek().name().substring(0, 3)))
                .originalPrice((eh - sh) * cafe.getPricePerHour())
                .discountPrice((eh - sh) * cafe.getPricePerHour()) // 할인 미적용
                .build();

        long userId = SessionManager.getInstance().getLoggedInUser().getUser_id();

        try {
            Reservation newReservation = service.createReservation(
                    cafe.getCafeId(),
                    selectedSeat.getSeatId(),
                    userId,
                    dto
            );
            statusLabel.setStyle("-fx-text-fill:#4CAF50;");
            statusLabel.setText("예약 완료!");
            loadSeats();                    // 상태 갱신
        } catch (Exception ex) {
            statusLabel.setStyle("-fx-text-fill:#e91e63;");
            statusLabel.setText("예약 실패: " + ex.getMessage());
        }
    }
}
