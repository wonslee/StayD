package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.review.dto.ReviewListDto;
import org.example.stayd.domain.review.service.ReviewService;
import org.example.stayd.domain.review.service.ReviewServiceImpl;

import java.util.Arrays;
import java.util.List;

public class ReviewEditController {

    @FXML private TextArea reviewTextArea;
    @FXML private ToggleButton star1, star2, star3, star4, star5;
    @FXML private Button saveButton, cancelButton;
    @FXML private HBox ratingBox;

    private final ReviewService svc = new ReviewServiceImpl();

    private ReviewDto review;  // 실제 수정 대상 DTO
    private List<ToggleButton> starButtons;

    private Runnable onReviewUpdated;

    // TODO [통합 시]: 세션에서 현재 로그인한 사용자 ID로 대체
    private final int loginUserId = 66; // ← 테스트용

    /** 초기화: 별점 버튼 세팅 */
    @FXML
    private void initialize() {
        starButtons = Arrays.asList(star1, star2, star3, star4, star5);

        for (int i = 0; i < starButtons.size(); i++) {
            int rating = i + 1;
            starButtons.get(i).setOnAction(e -> updateStarRating(rating));
        }
    }

    /** 외부에서 리뷰 데이터 주입 */
    public void setReview(ReviewListDto dto) {
        this.review = new ReviewDto();
        review.setReservationId(dto.getReservationId());  // reservationId 기반
        review.setReviewerId(loginUserId);                // reviewerId (세션 연동 예정)
        review.setCafeId(dto.getCafeId());                // dto에 cafeId 있어야 함
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());

        reviewTextArea.setText(dto.getContent());
        updateStarRating(dto.getRating());
    }

    /** 별점 UI 설정 */
    private void updateStarRating(int rating) {
        for (int i = 0; i < starButtons.size(); i++) {
            starButtons.get(i).setSelected(i < rating);
        }
        review.setRating(rating);
    }

    /** 저장 버튼 클릭 시 */
    @FXML
    private void handleSave() {
        if (loginUserId != review.getReviewerId()) {  // ✅ userId 기준 검증
            showAlert("본인의 리뷰만 수정할 수 있습니다.");
            return;
        }

        String content = reviewTextArea.getText().trim();
        if (content.isEmpty()) {
            showAlert("내용을 입력해주세요.");
            return;
        }

        review.setContent(content);

        boolean success = svc.updateReview(review);
        if (success) {
            showAlert("리뷰가 수정되었습니다.");
            if (onReviewUpdated != null) {
                onReviewUpdated.run(); // 새로고침 콜백 실행
            }
            closeWindow();
        } else {
            showAlert("수정에 실패했습니다.");
        }
    }

    /** 취소 버튼 */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /** 창 닫기 */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /** 알림창 */
    private void showAlert(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /** [★] 수정 완료 후 콜백 등록 */
    public void setOnReviewUpdated(Runnable callback) {
        this.onReviewUpdated = callback;
    }
}
