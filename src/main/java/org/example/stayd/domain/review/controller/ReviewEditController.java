package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.review.service.ReviewService;
import org.example.stayd.domain.review.service.ReviewServiceImpl;

import java.util.Arrays;
import java.util.List;

/**
 * 리뷰 수정 팝업 컨트롤러
 * - 기존 리뷰 내용을 불러와 수정 가능
 * - 본인 리뷰인지 확인 후 수정 가능
 * - 저장/취소 기능
 */
public class ReviewEditController {

    // ==== FXML 연결 UI 컴포넌트 ====
    @FXML private TextArea reviewTextArea;         // 리뷰 입력 필드
    @FXML private ToggleButton star1, star2, star3, star4, star5;  // 별점 버튼
    @FXML private Button saveButton, cancelButton; // 저장/취소 버튼
    @FXML private HBox ratingBox;                  // 별점 감싸는 HBox

    // ==== 서비스 객체 생성 ====
    private final ReviewService reviewService = new ReviewServiceImpl();

    // ==== 내부 상태 ====
    private ReviewDto review;                      // 현재 수정 중인 리뷰 정보
    private List<ToggleButton> starButtons;        // 별점 버튼 리스트
    private Runnable onReviewUpdated;              // 수정 후 새로고침 콜백

    /**
     * FXML 로딩 후 초기화 메서드
     * - 별점 버튼 클릭 이벤트 설정
     * - 저장/취소 버튼 핸들링
     */
    @FXML
    public void initialize() {
        starButtons = Arrays.asList(star1, star2, star3, star4, star5);

        // 별점 버튼 클릭 시 해당 점수까지 하이라이트
        for (int i = 0; i < starButtons.size(); i++) {
            final int rating = i + 1;
            starButtons.get(i).setOnAction(e -> highlightStars(rating));
        }

        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

    /**
     * 외부에서 리뷰 정보를 주입받고 화면에 초기 설정
     * @param review 수정할 리뷰 DTO
     * @param onReviewUpdated 수정 완료 후 실행할 콜백 (ex. 목록 새로고침)
     */
    public void setReviewData(ReviewDto review, Runnable onReviewUpdated) {
        this.review = review;
        this.onReviewUpdated = onReviewUpdated;

        // 기존 리뷰 내용과 별점 표시
        reviewTextArea.setText(review.getContent());
        highlightStars(review.getRating());
    }

    /**
     * 별점 버튼을 하이라이트하는 함수
     * @param rating 선택한 별점 (1~5)
     */
    private void highlightStars(int rating) {
        for (int i = 0; i < starButtons.size(); i++) {
            starButtons.get(i).setSelected(i < rating);
        }
    }

    /**
     * 저장 버튼 클릭 시 실행
     * - 본인 리뷰인지 확인
     * - 내용과 별점 유효성 체크
     * - 수정 처리 및 콜백 실행
     */
    @FXML
    private void handleSave() {
        int loginUserId = SessionManager.getInstance().getLoggedInUser().getUser_id();

        //  본인 리뷰인지 검증
        if (loginUserId != review.getReviewerId()) {
            showAlert("본인의 리뷰만 수정할 수 있습니다.");
            return;
        }

        String content = reviewTextArea.getText().trim();
        int score = (int) starButtons.stream().filter(ToggleButton::isSelected).count();

        // 유효성 검사
        if (content.isEmpty() || score == 0) {
            showAlert("내용과 별점을 모두 입력해주세요.");
            return;
        }

        // DTO에 수정 내용 반영
        review.setContent(content);
        review.setRating(score);

        //  서비스 계층에서 수정 처리
        boolean success = reviewService.updateReview(review);
        if (success) {
            showAlert("리뷰가 수정되었습니다.");
            if (onReviewUpdated != null) {
                onReviewUpdated.run();  //  새로고침 콜백 실행
            }
            closeWindow();
        } else {
            showAlert("수정에 실패했습니다.");
        }
    }

    /**
     * 취소 버튼 클릭 시 창 닫기
     */
    private void handleCancel() {
        closeWindow();
    }

    /**
     * 현재 팝업창 닫기
     */
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * 경고창 출력 유틸
     * @param msg 사용자에게 표시할 메시지
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("경고");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
