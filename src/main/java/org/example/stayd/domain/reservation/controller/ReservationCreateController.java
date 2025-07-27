// 작성자 : 이원석, 방대혁
package org.example.stayd.domain.reservation.controller;

import static org.example.stayd.common.BusinessLogicConstants.DEFAULT_SEAT_COLS;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import org.example.stayd.domain.cafe.model.DayOfWeek;
import org.example.stayd.domain.cafe.model.OperationHours;
import org.example.stayd.domain.cafe.service.CafeService;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.model.Reservation;
import org.example.stayd.domain.reservation.model.Seat;
import org.example.stayd.domain.reservation.service.ReservationService;
import org.example.stayd.domain.user.service.UserService;

/**
 * 예약 생성 화면의 JavaFX 컨트롤러 클래스입니다.
 * <p>
 * UI 이벤트 처리, 입력값 검증, 예약 생성 요청, 좌석 현황 갱신 등 View와 Service 계층을 연결하는 역할을 합니다.
 * </p>
 */
@NoArgsConstructor
public class ReservationCreateController {
    private final UserService userService = new UserService();

    /* 주입될 DTO */
    private CafeDto.DetailResponse cafe;

    /* 서비스 & 상태 */
    private final ReservationService service = new ReservationService();
    private Seat selectedSeat;
    private final CafeService cafeService = new CafeService();

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


    /**
     * FXML 로드 직후 자동 호출되는 초기화 메서드입니다.
     * <p>
     * 시간 콤보박스를 초기화하고, 카페 정보가 있으면 좌석 현황을 불러오고 가격을 계산합니다.
     * </p>
     */
    @FXML
    public void initialize() {
        initTimeCombos();
        if (cafe != null) {
            loadSeats();
            recalc();
        }
    }

    /**
     * 선택한 날짜와 카페의 운영시간에 따라 시작/종료 시간 콤보박스를 동적으로 갱신합니다.
     * <p>
     * 운영하지 않는 날이거나 예외 발생 시 콤보박스와 예약 버튼을 비활성화합니다.
     * </p>
     *
     * @throws Exception 카페 운영시간 조회 실패 등
     */
    private void updateTimeCombosForSelectedDay() {
        System.out.println("updateTimeCombosForSelectedDay()");
        if (cafe == null || datePicker.getValue() == null) {
            return;
        }
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
                int nowHour =
                        (datePicker.getValue().isEqual(LocalDate.now())) ? java.time.LocalTime.now().getHour() : open;
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

    /**
     * 날짜 선택 시 호출되는 이벤트 핸들러입니다.
     * <p>
     * 선택한 날짜에 따라 좌석 현황 및 할인 시간대 정보를 갱신하고, 가격을 재계산합니다.
     * </p>
     *
     * @param event 날짜 선택 이벤트
     */
    @FXML
    public void handleDateChange(ActionEvent event) {
        updateTimeCombosForSelectedDay();
        // Update discountHoursLabel only when date is picked
        if (cafe != null && datePicker.getValue() != null) {
            String dayOfWeek = datePicker.getValue().getDayOfWeek().name().substring(0, 3).toUpperCase();
            java.util.List<org.example.stayd.domain.cafe.model.DiscountHours> discounts = cafeService.getDiscountHours(
                    cafe.getCafeId());
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
            if (discountHoursLabel != null) {
                discountHoursLabel.setText("");
            }
        }
        recalc();
    }

    /**
     * 시작 시간 콤보박스 변경 시 호출되는 이벤트 핸들러입니다.
     * <p>
     * 종료 시간, 가격 등 관련 UI를 갱신합니다.
     * </p>
     *
     * @param event 콤보박스 변경 이벤트
     */
    @FXML
    public void handleStartComboChange(ActionEvent event) {
        recalc();
    }

    /**
     * 종료 시간 콤보박스 변경 시 호출되는 이벤트 핸들러입니다.
     * <p>
     * 가격 등 관련 UI를 갱신합니다.
     * </p>
     *
     * @param event 콤보박스 변경 이벤트
     */
    @FXML
    public void handleEndComboChange(ActionEvent event) {
        recalc();
    }

    /**
     * 예약 버튼 클릭 시 호출되는 이벤트 핸들러입니다.
     * <p>
     * 입력값 검증 후 예약 생성 요청을 보냅니다. 예약 성공 시 상세 페이지로 이동합니다.
     * </p>
     *
     * @param event 버튼 클릭 이벤트
     */
    @FXML
    public void handleReserve(ActionEvent event) {
        makeReservation();
        checkIfUserLoggedIn(event);
    }

    /**
     * 카페 정보를 설정하고, 좌석 현황과 시간 콤보박스를 초기화합니다.
     * <p>
     * FXML 필드가 주입된 이후에 호출되어야 정상 동작합니다.
     * </p>
     *
     * @param cafe 카페 상세 정보 DTO
     */
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


    /**
     * 시간 콤보박스를 0~24시로 초기화합니다.
     * <p>
     * 운영시간 컬럼이 있으면 실제 운영시간으로 대체할 수 있습니다.
     * </p>
     */
    private void initTimeCombos() {
        int open = 0, close = 24; // 운영시간 컬럼이 있으면 교체
        startCombo.getItems().setAll(IntStream.range(open, close).boxed().toList());
        endCombo.getItems().setAll(IntStream.rangeClosed(open + 1, close).boxed().toList());
    }

    /**
     * 카페의 좌석 정보를 불러와서 좌석 그리드에 버튼으로 표시합니다.
     * <p>
     * 좌석의 가용성에 따라 버튼의 스타일과 활성화 상태를 다르게 표시하며, 좌석 선택 시 선택된 좌석을 저장합니다.
     * </p>
     *
     * @throws SQLException 좌석 정보 조회 실패 시
     */
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

    /**
     * 현재 선택된 시간, 카페, 할인 정책에 따라 총 가격을 계산하여 UI에 표시합니다.
     * <p>
     * 할인 적용 시 할인 금액도 함께 표시합니다.
     * </p>
     */
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

    /**
     * 예약 생성 로직을 수행합니다.
     * <p>
     * 1. 로그인 여부 확인, 2. 입력값 검증, 3. 예약 DTO 생성, 4. 서비스 호출, 5. 성공 시 상세 페이지 이동
     * </p>
     *
     * @throws Exception 예약 생성 실패 시 예외 발생 (DB 오류, 검증 실패 등)
     */
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

        long userId = SessionManager.getInstance().getLoggedInUser().getUser_id();

        ReservationDTO dto = ReservationDTO.builder()
                .cafeId(cafe.getCafeId())
                .userId(userId)
                .reservationDate(date)
                .usageStartedAt(sh)
                .usageEndedAt(eh)
                .dayOfWeek(DayOfWeek.valueOf(date.getDayOfWeek().name().substring(0, 3)))
                .originalPrice((eh - sh) * cafe.getPricePerHour())
                .discountPrice((eh - sh) * cafe.getPricePerHour()) // 할인 미적용
                .build();

        try {
            Reservation newReservation = service.createReservation(
                    dto,
                    selectedSeat.getSeatId()
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

    /**
     * 예약 생성 성공 시 예약 상세 페이지로 이동합니다.
     * <p>
     * 생성된 예약 정보와 카페 정보를 상세 컨트롤러에 전달합니다.
     * </p>
     *
     * @param createdReservation 생성된 예약 객체
     */
    private void navigateToReservationDetail(Reservation createdReservation) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/stayd/reservation/reservation-detail.fxml"));
            Node detailView = loader.load();
            // 컨트롤러 가져오기
            org.example.stayd.domain.reservation.controller.ReservationDetailController detailController = loader.getController();
            // 데이터 전달
            detailController.setCafe(this.cafe);
            detailController.setReservation(
                    org.example.stayd.domain.reservation.dto.ReservationDTO.of(createdReservation));
            detailController.updateUI();
            // 현재 Stage에서 전환
            Stage stage = (Stage) reserveBtn.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene((javafx.scene.Parent) detailView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 예약 버튼 클릭 후 로그인 상태를 확인하여, 로그인되어 있지 않으면 로그인 페이지로 이동합니다.
     * <p>
     * 로그인 상태라면 마이페이지(예약 상세)로 이동합니다.
     * </p>
     *
     * @param event 예약 버튼 클릭 이벤트
     */
    private void checkIfUserLoggedIn(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 로그인 상태 확인
        if (userService.isUserLoggedIn()) {
            System.out.println("navigating to my page");
            FXUtils.navigateToPage(stage, "/org/example/stayd/reservation/reservation-detail.fxml",
                    "마이페이지로 이동하는 중 오류가 발생했습니다.");
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