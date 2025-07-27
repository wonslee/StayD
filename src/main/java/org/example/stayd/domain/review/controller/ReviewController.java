package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.reservation.dto.ReservationWithCafeDTO;
import org.example.stayd.domain.review.dao.ReviewDAO;
import org.example.stayd.domain.user.dto.UserDTO;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 리뷰 작성 화면을 제어하는 컨트롤러 클래스.
 * - 예약 정보 바인딩
 * - 별점 선택
 * - 리뷰 작성 및 등록 기능 포함
 */
public class ReviewController {

    private final ReviewDAO reviewDAO = new ReviewDAO();
    @FXML private TextArea reviewTextArea;
    @FXML private Button submitButton, cancelButton;

    @FXML private ToggleButton star1;
    @FXML private ToggleButton star2;
    @FXML private ToggleButton star3;
    @FXML private ToggleButton star4;
    @FXML private ToggleButton star5;
    @FXML private HBox ratingBox;

    @FXML private Label cafeName;
    @FXML private Label useDate;
    @FXML private Label useTime;
    @FXML private Label branchName;

    private List<ToggleButton> stars;

    private ReservationWithCafeDTO selectedReservation;
    private final ReservationWDAO reservationWDAO = new ReservationWDAO();
    private Consumer<Void> onReviewSubmittedCallback;

    /**
     * 예약 정보를 화면에 표시
     * @param dto 예약 정보 DTO
     */
    public void setReservation(ReservationWithCafeDTO dto) {
        this.selectedReservation = dto;

        if (dto != null) {
            System.out.println("\uD83D\uDCCC 예약 정보 세팅: " + dto);

            cafeName.setText(dto.getCafeName());
            useDate.setText(dto.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
            useTime.setText(dto.getUsageStartedAt() + ":00 - " + dto.getUsageEndedAt() + ":00");
            if (branchName != null) branchName.setText(dto.getCafeName() + " 지점");
        }
    }
    /**
     * 리뷰 등록 후 수행할 콜백을 설정
     * @param callback 등록 후 콜백 함수
     */
    public void setOnReviewSubmittedCallback(Consumer<Void> callback) {
        this.onReviewSubmittedCallback = callback;
    }
    /**
     * 컨트롤러 초기화 시 별점 버튼들을 초기화하고 이벤트를 설정
     */
    @FXML
    private void initialize() {
        stars = Arrays.asList(star1, star2, star3, star4, star5);
        for (int i = 0; i < stars.size(); i++) {
            final int index = i;
            stars.get(i).setOnAction(e -> selectStars(index + 1));
        }
    }

    /**
     * 별점 버튼들을 선택된 개수만큼 활성화
     * @param count 선택된 별점 수
     */
    private void selectStars(int count) {
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setSelected(i < count);
        }
    }
    /**
     * 선택된 별점을 반환
     * @return 선택된 별점 값 (1~5)
     */
    private int getSelectedRating() {
        int rating = 0;
        for (int i = 0; i < stars.size(); i++) {
            if (stars.get(i).isSelected()) {
                rating = i + 1;
            }
        }
        return rating;
    }
    /**
     * 리뷰 제출 버튼 클릭 시 실행되는 메서드
     * - 별점과 내용 입력 확인
     * - 리뷰 작성 가능 여부 확인 (중복, 완료 상태)
     * - DB 저장 및 팝업 종료
     */
    @FXML
    public void handleSubmit(ActionEvent event) {
        System.out.println("✅ handleSubmit() 실행됨");

        UserDTO user = SessionManager.getInstance().getLoggedInUser();

        if (user == null) {
            showAlert("로그인이 필요합니다.");
            return;
        }

        if (selectedReservation == null) {
            showAlert("리뷰를 작성할 예약을 선택하세요.");
            return;
        }

        long reservationId = selectedReservation.getReservationId();
        int selectedRating = getSelectedRating();

        System.out.println("⭐️ 선택된 별점: " + selectedRating);
        System.out.println("📝 입력된 리뷰 내용: " + reviewTextArea.getText().trim());
        System.out.println("💾 대상 예약 ID: " + reservationId);

        if (selectedRating == 0) {
            showAlert("별점을 선택해주세요.");
            return;
        }

        String content = reviewTextArea.getText().trim();
        if (content.isEmpty()) {
            showAlert("리뷰 내용을 입력해주세요.");
            return;
        }

        try (Connection conn = new DatabaseConnection().getConnection()) {
            if (reviewDAO.existsReviewByReservationId(conn, reservationId)) {
                showAlert("이미 작성한 리뷰입니다.");
                return;
            }

            if (!reviewDAO.isReservationFinished(conn, reservationId)) {
                showAlert("아직 이용이 완료되지 않았습니다.");
                return;
            }

            System.out.println("💾 리뷰 저장 시도...");
            boolean result = reviewDAO.saveReview(conn, reservationId, selectedRating, content);
            System.out.println("🧨 DAO 저장 결과: " + result);

            if (result) {
                System.out.println("✅ 리뷰 등록 성공!");
                showAlert("리뷰가 등록되었습니다.");
                if (onReviewSubmittedCallback != null) {
                    onReviewSubmittedCallback.accept(null);
                }
                submitButton.getScene().getWindow().hide();
            } else {
                System.out.println("❌ 리뷰 저장 실패!");
                showAlert("리뷰 등록에 실패했습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류가 발생했습니다.");
        }
    }
    /**
     * 리뷰 작성 취소 버튼 클릭 시 팝업을 닫음
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        cancelButton.getScene().getWindow().hide();
    }
    /**
     * 경고창을 띄움
     * @param msg 출력할 메시지
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
