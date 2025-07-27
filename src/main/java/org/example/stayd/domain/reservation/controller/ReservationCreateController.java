package org.example.stayd.domain.reservation.controller;

import static org.example.stayd.common.BusinessLogicConstants.DEFAULT_SEAT_COLS;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import org.example.stayd.common.FXUtils;
import org.example.stayd.common.SessionManager;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.dto.CafeDto.DetailResponse;
import org.example.stayd.domain.cafe.model.DiscountHours;
import org.example.stayd.domain.cafe.service.CafeService;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.cafe.model.OperationHours;
import org.example.stayd.domain.cafe.model.DayOfWeek;
import org.example.stayd.domain.reservation.model.Reservation;
import org.example.stayd.domain.reservation.model.Seat;
import org.example.stayd.domain.reservation.service.ReservationService;
import org.example.stayd.domain.user.service.UserService;
import javafx.scene.control.DateCell;
import javafx.util.Callback;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;

@NoArgsConstructor
public class ReservationCreateController {
    private UserService userService = new UserService();

    /* 주입될 DTO */
    private CafeDto.DetailResponse cafe;

    /* 서비스 & 상태 */
    private final ReservationService service = new ReservationService();
    private Seat selectedSeat;
    private CafeService cafeService = new CafeService();

    public ReservationCreateController(DetailResponse cafe) {
        this.cafe = cafe;
    }

    /* FXML */
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Integer> startCombo, endCombo;
    @FXML
    private Label totalPriceLabel, statusLabel, discountHoursLabel;
    @FXML
    private GridPane seatGrid;
    @FXML
    private Button reserveBtn;


    /* 초기화는 FXML 로드 직후 호출 */
    @FXML
    public void initialize() {
        initTimeCombos();
        if (cafe != null) {
            loadSeats();
            recalc();
        }
    }


    private void updateTimeCombosForSelectedDay() {
        System.out.println("updateTimeCombosForSelectedDay()");
        if (cafe == null || datePicker.getValue() == null) return;
        String dayOfWeekStr = datePicker.getValue().getDayOfWeek().name().substring(0, 3).toUpperCase();
        DayOfWeek selectedDay = DayOfWeek.valueOf(dayOfWeekStr);
        try {
            java.util.List<OperationHours> opHours = cafeService.getOperationHours(cafe.getCafeId());
            OperationHours op = opHours.stream()
                .filter(o -> o.dayOfWeek() == selectedDay)
                .findFirst().orElse(null);
            if (op != null) {
                int open = op.operationStart();
                int close = op.operationEnd();
                int nowHour = (datePicker.getValue().isEqual(LocalDate.now())) ? java.time.LocalTime.now().getHour() : open;
                java.util.List<Integer> startHours = java.util.stream.IntStream.range(open, close)
                    .filter(h -> datePicker.getValue().isAfter(LocalDate.now()) || h > nowHour)
                    .boxed().toList();
                java.util.List<Integer> endHours = java.util.stream.IntStream.rangeClosed(open + 1, close)
                    .filter(h -> datePicker.getValue().isAfter(LocalDate.now()) || h > nowHour)
                    .boxed().toList();
                startCombo.getItems().setAll(startHours);
                endCombo.getItems().setAll(endHours);
                startCombo.setValue(null);
                endCombo.setValue(null);
                // Enable and reset style
                startCombo.setDisable(false);
                endCombo.setDisable(false);
                reserveBtn.setDisable(false);
                startCombo.setStyle("");
                endCombo.setStyle("");
                reserveBtn.setText("예약");
                reserveBtn.setStyle("");
            } else {
                startCombo.getItems().clear();
                endCombo.getItems().clear();
                startCombo.setDisable(true);
                endCombo.setDisable(true);
                reserveBtn.setDisable(true);
                startCombo.setStyle("-fx-border-color: red; -fx-text-fill: red;");
                endCombo.setStyle("-fx-border-color: red; -fx-text-fill: red;");
                reserveBtn.setText("unavailable");
                reserveBtn.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
            }
        } catch (Exception e) {
            startCombo.getItems().clear();
            endCombo.getItems().clear();
            startCombo.setDisable(true);
            endCombo.setDisable(true);
            reserveBtn.setDisable(true);
            startCombo.setStyle("-fx-border-color: red; -fx-text-fill: red;");
            endCombo.setStyle("-fx-border-color: red; -fx-text-fill: red;");
            reserveBtn.setText("unavailable");
            reserveBtn.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
        }
    }

    @FXML
    public void handleDateChange(ActionEvent event) {
        updateTimeCombosForSelectedDay();
        // Update discountHoursLabel only when date is picked
        if (cafe != null && datePicker.getValue() != null) {
            String dayOfWeek = datePicker.getValue().getDayOfWeek().name().substring(0, 3).toUpperCase();
            java.util.List<org.example.stayd.domain.cafe.model.DiscountHours> discounts = cafeService.getDiscountHours(cafe.getCafeId());
            org.example.stayd.domain.cafe.model.DiscountHours dh = discounts.stream()
                .filter(d -> d.dayOfWeek().name().equalsIgnoreCase(dayOfWeek))
                .findFirst().orElse(null);
            if (dh != null) {
                String period = String.format("%02d:00 ~ %02d:00", dh.discountStart(), dh.discountEnd());
                discountHoursLabel.setText(period);
            } else {
                discountHoursLabel.setText("없음");
            }
        } else {
            if (discountHoursLabel != null) discountHoursLabel.setText("");
        }
        recalc();
    }

    @FXML
    public void handleStartComboChange(ActionEvent event) {
        recalc();
    }

    @FXML
    public void handleEndComboChange(ActionEvent event) {
        recalc();
    }

    @FXML
    public void handleReserve(ActionEvent event) {
        makeReservation();
        checkIfUserLoggedIn(event);
    }

    public void setCafe(CafeDto.DetailResponse cafe) {
        this.cafe = cafe;
        // If FXML fields are injected, update UI
        if (seatGrid != null) {
            loadSeats();

            if (datePicker.getValue() != null) {
                updateTimeCombosForSelectedDay();
            }
            recalc();
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

    private void recalc() {
        if (cafe == null) {
            return;
        }
        Integer sh = startCombo.getValue(), eh = endCombo.getValue();
        if (sh == null || eh == null || eh <= sh) {
            totalPriceLabel.setText("0");
            return;
        }
        String dayOfWeek = datePicker.getValue() != null
            ? datePicker.getValue().getDayOfWeek().name().substring(0, 3).toUpperCase()
            : "MON"; // default/fallback

        int total = cafeService.calculateCafePrice(cafe.getCafeId(), dayOfWeek, sh, eh);
        int undiscounted = (eh - sh) * cafe.getPricePerHour();
        int discount = undiscounted - total;

        // Show result price and discounted price in parentheses if discount exists
        if (discount > 0) {
            totalPriceLabel.setText(String.format("%,d원 (할인 %d원)", total, discount));
        } else {
            totalPriceLabel.setText(String.format("%,d원", total));
        }
    }

    /* ───────── 예약 실행 ───────── */
    private void makeReservation() {
        if (cafe == null) {
            return;
        }

        // 유저 로그인 여부 확인 - 안 되어있을 경우
        Stage stage = (Stage) reserveBtn.getScene().getWindow();
        if (!userService.isUserLoggedIn()) {
            FXUtils.navigateToPage(
                    stage,
                    SceneConfig.LOGIN_FXML,
                    "유저가 로그인하지 않았습니다."
            );
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
            if (newReservation != null) {
                // 예약 성공 시 상세 페이지로 이동 (데이터 전달)
                navigateToReservationDetail(newReservation);
            }
            statusLabel.setStyle("-fx-text-fill:#4CAF50;");
            statusLabel.setText("예약 완료!");
            loadSeats();                    // 상태 갱신
        } catch (Exception ex) {
            statusLabel.setStyle("-fx-text-fill:#e91e63;");
            statusLabel.setText("예약 실패: " + ex.getMessage());
        }
    }

    // 상세 페이지로 이동하는 메서드
    private void navigateToReservationDetail(Reservation createdReservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/reservation/reservation-detail.fxml"));
            Node detailView = loader.load();
            // 컨트롤러 가져오기
            org.example.stayd.domain.reservation.controller.ReservationDetailController detailController = loader.getController();
            // 데이터 전달
            detailController.setCafe(this.cafe);
            detailController.setReservation(org.example.stayd.domain.reservation.dto.ReservationDTO.of(createdReservation));
            detailController.updateUI();
            // 현재 Stage에서 전환
            Stage stage = (Stage) reserveBtn.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene((javafx.scene.Parent) detailView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIfUserLoggedIn(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 로그인 상태 확인
        if (userService.isUserLoggedIn()) {
            // TODO: 정상 케이스 - 유저가 로그인되어있으면 마이페이지 - 예약 상세 페이지로 이동
            System.out.println("navigating to my page");
            FXUtils.navigateToPage(stage, "/org/example/stayd/reservation/reservation-detail.fxml", "마이페이지로 이동하는 중 오류가 발생했습니다.");
        } else {
            // 비전상 케이스 - 유저가 로그인 되어있지 않으면 로그인 페이지로 이동
            FXUtils.navigateToPage(stage, SceneConfig.LOGIN_FXML, "로그인 화면으로 이동하는 중 오류가 발생했습니다.");
        }
    }

    // 버튼 호버 효과들
    @FXML
    private void onModifyButtonEnter(MouseEvent event) {
        reserveBtn.setStyle(
                "-fx-background-color: #4caf4f; -fx-font-weight: bold; -fx-background-radius: 10;");
        reserveBtn.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onModifyButtonExit(MouseEvent event) {
        reserveBtn.setStyle(
                "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        reserveBtn.setTextFill(javafx.scene.paint.Color.web("#4caf4f"));
    }
}