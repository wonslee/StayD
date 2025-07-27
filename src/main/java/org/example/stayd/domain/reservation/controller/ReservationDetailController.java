package org.example.stayd.domain.reservation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.service.ReservationService;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.example.stayd.common.FXUtils;
import org.example.stayd.common.SessionManager;

public class ReservationDetailController {

    // FXML 필드들
    @FXML private Label titleLabel;

    // 카페 정보
    @FXML private Label cafeNameLabel;
    @FXML private Label cafeAddressLabel;
    @FXML private Label cafePhoneLabel;
    @FXML private Label cafePriceLabel;

    // 예약 정보
    @FXML private Label reservationIdLabel;
    @FXML private Label userIdLabel;
    @FXML private Label reservationDateLabel;
    @FXML private Label usageTimeLabel;
    @FXML private Label dayOfWeekLabel;
    @FXML private Label createdAtLabel;

    // 결제 정보
    @FXML private Label originalPriceLabel;
    @FXML private Label discountPriceLabel;
    @FXML private Label finalPriceLabel;
    @FXML private Label canceledLabel;

    // 리뷰 정보
    @FXML private VBox reviewSection;
    @FXML private Label ratingLabel;
    @FXML private Label reviewCreatedAtLabel;
    @FXML private Label reviewContentLabel;

    // 버튼들
    @FXML private Button backButton;
    @FXML private Button cancelReservationButton;

    // 상태 메시지
    @FXML private Label statusLabel;

    // 데이터 필드들
    private CafeDto.DetailResponse cafe;
    private ReservationDTO reservation;
    private final ReservationService reservationService;
    private Consumer<Void> onReservationCanceledCallback;

    // 기본 생성자 (FXML 로더용)
    public ReservationDetailController() {
        this.reservationService = new ReservationService();
    }

    // 데이터 설정 메서드들
    public void setCafe(CafeDto.DetailResponse cafe) {
        this.cafe = cafe;
    }

    public void setReservation(ReservationDTO reservation) {
        this.reservation = reservation;
    }

    @FXML
    public void initialize() {
        // initialize는 FXML 로드 후 자동 호출되지만,
        // 데이터는 setter를 통해 나중에 설정됨
    }

    // 데이터 설정 후 UI 업데이트를 위한 메서드
    public void updateUI() {
        if (reservation != null && cafe != null) {
            populateCafeInfo();
            populateReservationInfo();
            populatePaymentInfo();
            populateReviewInfo();
            updateCancelButtonState();
        } else {
            showError("예약 정보를 불러올 수 없습니다.");
        }
    }

    private void populateCafeInfo() {
        cafeNameLabel.setText(cafe.getName());
        cafeAddressLabel.setText(cafe.getAddress());
        cafePhoneLabel.setText(cafe.getPhoneNumber());
        cafePriceLabel.setText(String.format("%,d원/시간", cafe.getPricePerHour()));
    }

    private void populateReservationInfo() {
        reservationIdLabel.setText(String.valueOf(reservation.getReservationId()));
        userIdLabel.setText(String.valueOf(reservation.getUserId()));

        // 날짜 포맷팅
        if (reservation.getReservationDate() != null) {
            reservationDateLabel.setText(reservation.getReservationDate()
                    .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        }

        // 이용 시간 포맷팅
        usageTimeLabel.setText(String.format("%02d:00 ~ %02d:00",
                reservation.getUsageStartedAt(), reservation.getUsageEndedAt()));

        // 요일 변환 (MON -> 월요일)
        dayOfWeekLabel.setText(convertDayOfWeek(reservation.getDayOfWeek().name()));

        // 생성일 포맷팅
        if (reservation.getCreatedAt() != null) {
            createdAtLabel.setText(reservation.getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    private void populatePaymentInfo() {
        originalPriceLabel.setText(String.format("%,d원", reservation.getOriginalPrice()));
        discountPriceLabel.setText(String.format("%,d원", reservation.getDiscountPrice()));

        // 최종 결제금액 계산
        int finalPrice = reservation.getOriginalPrice() - reservation.getDiscountPrice();
        finalPriceLabel.setText(String.format("%,d원", finalPrice));

        // 예약 취소 상태
        canceledLabel.setText(reservation.isCanceled() ? "취소됨" : "활성");
        canceledLabel.setStyle(reservation.isCanceled() ?
                "-fx-text-fill: #F44336; -fx-font-weight: bold;" :
                "-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
    }

    private void populateReviewInfo() {
        // 리뷰가 있는 경우에만 섹션 표시
        if (reservation.getRating() != null && reservation.getContent() != null) {
            reviewSection.setVisible(true);
            reviewSection.setManaged(true);

            // 별점 표시
            ratingLabel.setText("★".repeat(reservation.getRating()) + "☆".repeat(5 - reservation.getRating()));

            // 리뷰 작성일
            if (reservation.getReviewCreatedAt() != null) {
                reviewCreatedAtLabel.setText(reservation.getReviewCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

            // 리뷰 내용
            reviewContentLabel.setText(reservation.getContent());
        } else {
            reviewSection.setVisible(false);
            reviewSection.setManaged(false);
        }
    }

    private void updateCancelButtonState() {
        // 이미 취소된 예약이거나 과거 예약인 경우 취소 버튼 비활성화
        if (reservation.isCanceled()) {
            cancelReservationButton.setDisable(true);
            cancelReservationButton.setText("이미 취소됨");
        }
    }

    private String convertDayOfWeek(String dayOfWeek) {
        return switch (dayOfWeek) {
            case "MON" -> "월요일";
            case "TUE" -> "화요일";
            case "WED" -> "수요일";
            case "THU" -> "목요일";
            case "FRI" -> "금요일";
            case "SAT" -> "토요일";
            case "SUN" -> "일요일";
            default -> dayOfWeek;
        };
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // mypage.fxml로 이동
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXUtils.navigateToPage(stage, "/org/example/stayd/mypage/mypage.fxml", "마이페이지로 이동하는 중 오류가 발생했습니다.");
    }

    @FXML
    private void handleCancelReservation(ActionEvent event) {
        if (reservation.isCanceled()) {
            showAlert("이미 취소된 예약입니다.", AlertType.WARNING);
            return;
        }

        Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
        confirmDialog.setTitle("예약 취소 확인");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("정말로 이 예약을 취소하시겠습니까?\n취소된 예약은 복구할 수 없습니다.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    // 실제 예약 삭제 처리 (userId 전달)
                    long userId = SessionManager.getInstance().getLoggedInUser().getUser_id();
                    reservationService.deleteReservation(reservation.getReservationId(), userId);
                    showAlert("예약이 성공적으로 취소되었습니다.", AlertType.INFORMATION);
                    // mypage.fxml로 이동
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    FXUtils.navigateToPage(stage, "/org/example/stayd/mypage/mypage.fxml", "마이페이지로 이동하는 중 오류가 발생했습니다.");
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("예약 취소 중 오류가 발생했습니다.", AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
    }

    // 콜백 설정 메서드
    public void setOnReservationCanceledCallback(Consumer<Void> callback) {
        this.onReservationCanceledCallback = callback;
    }

    // 버튼 호버 효과들
    @FXML
    private void onModifyButtonEnter(MouseEvent event) {
        backButton.setStyle(
                "-fx-background-color: #4caf4f; -fx-font-weight: bold; -fx-background-radius: 10;");
        backButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onModifyButtonExit(MouseEvent event) {
        backButton.setStyle(
                "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        backButton.setTextFill(javafx.scene.paint.Color.web("#4caf4f"));
    }

    @FXML
    private void onDeleteButtonEnter(MouseEvent event) {
        cancelReservationButton.setStyle(
                "-fx-background-color: #ff6b6b; -fx-font-weight: bold; -fx-background-radius: 10; ");
        cancelReservationButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onDeleteButtonExit(MouseEvent event) {
        cancelReservationButton.setStyle(
                "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        cancelReservationButton.setTextFill(javafx.scene.paint.Color.web("#ff6b6b"));
    }
}