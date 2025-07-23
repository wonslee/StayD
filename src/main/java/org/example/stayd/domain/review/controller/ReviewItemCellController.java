package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.review.dto.ReviewListDto;

/**
 * 마이페이지 - 내가 쓴 리뷰 셀 컨트롤러
 * - 리뷰 내용 및 별점 표시
 * - 본인 리뷰일 경우에만 수정/삭제 버튼 노출
 * - 별점이 유효한 경우에만 표시
 */
public class ReviewItemCellController {

    @FXML private Label contentLabel;
    @FXML private Label starLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private Runnable onEdit;
    private Runnable onDelete;

    /**
     * 셀에 데이터 설정 - ReviewListDto 버전
     */
    public void setData(ReviewListDto review) {
        setCommonData(review.getContent(), review.getRating(), review.getReviewerId());
    }

    /**
     * 셀에 데이터 설정 - ReviewDto 버전 (⭐추가한 부분)
     */
    public void setData(ReviewDto review) {
        setCommonData(review.getContent(), review.getRating(), review.getReviewerId());
    }

    /**
     * 공통 렌더링 로직
     */
    private void setCommonData(String content, int rating, int reviewerId) {
        contentLabel.setText(content);

        // 별점 유효성 검사
        if (rating >= 0 && rating <= 5) {
            // 별점 표시 (예: ★★★☆☆)
            starLabel.setText("★".repeat(rating) + "☆".repeat(5 - rating));
            starLabel.setVisible(true);
            starLabel.setManaged(true);

            // 로그인 사용자와 작성자 비교
            int loginUserId = SessionManager.getInstance().getLoggedInUser().getUser_id();
            boolean isMyReview = (loginUserId == reviewerId);

            // 본인 리뷰일 경우만 수정/삭제 버튼 표시
            editButton.setVisible(isMyReview);
            editButton.setManaged(isMyReview);
            deleteButton.setVisible(isMyReview);
            deleteButton.setManaged(isMyReview);
        } else {
            // 유효하지 않은 별점일 경우 전부 숨김
            starLabel.setText("");
            starLabel.setVisible(false);
            starLabel.setManaged(false);
            editButton.setVisible(false);
            editButton.setManaged(false);
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
        }
    }

    /**
     * 수정 콜백 설정
     */
    public void setOnEdit(Runnable onEdit) {
        this.onEdit = onEdit;
        editButton.setOnAction(e -> {
            if (onEdit != null) onEdit.run();
        });
    }

    /**
     * 삭제 콜백 설정
     */
    public void setOnDelete(Runnable onDelete) {
        this.onDelete = onDelete;
        deleteButton.setOnAction(e -> {
            if (onDelete != null) onDelete.run();
        });
    }
    public void setData(String content, int rating) {
        contentLabel.setText(content);

        if (rating >= 0 && rating <= 5) {
            starLabel.setText("★".repeat(rating) + "☆".repeat(5 - rating));
            starLabel.setVisible(true);
            starLabel.setManaged(true);
        } else {
            starLabel.setVisible(false);
            starLabel.setManaged(false);
        }

        // 사용 내역은 리뷰 작성자 여부 필요 없음 → 버튼 숨김
        editButton.setVisible(false);
        editButton.setManaged(false);
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
    }

}
