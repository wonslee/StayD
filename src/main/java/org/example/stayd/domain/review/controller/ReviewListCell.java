package org.example.stayd.domain.review.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.review.dto.ReviewListDto;

import java.io.IOException;

/**
 * 리뷰 목록의 개별 셀 컨트롤러
 * - 본인 리뷰에만 수정/삭제 버튼 노출
 * - 삭제 시 확인창 표시
 * - 수정 시 팝업 열기
 */
public class ReviewListCell extends ListCell<ReviewListDto> {

    // FXML 요소들
    @FXML private Label nameLabel;
    @FXML private Label ratingLabel;
    @FXML private Label contentLabel;
    @FXML private Label dateLabel;
    @FXML private Button deleteBtn;
    @FXML private Button editButton;

    private Parent root;
    private final ReviewDao dao = new ReviewDaoImpl();  // DAO 초기화

    @Override
    protected void updateItem(ReviewListDto review, boolean empty) {
        super.updateItem(review, empty);

        if (empty || review == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        try {
            // FXML 로딩 및 컨트롤러 설정
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/reviewListCell.fxml"));
            loader.setController(this);
            root = loader.load();

            // UI에 데이터 표시
            nameLabel.setText(review.getReviewerName());
            contentLabel.setText(review.getContent());
            ratingLabel.setText("★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating()));
            dateLabel.setText(review.getCreatedAt().toString());

            // 현재 로그인 사용자 ID 조회
            int loginUserId = SessionManager.getInstance().getLoggedInUser().getUser_id();

            // 본인 리뷰 여부 판단
            boolean isMyReview = loginUserId == review.getReviewerId();

            // 본인 리뷰인 경우에만 버튼 노출
            editButton.setVisible(isMyReview);
            editButton.setManaged(isMyReview);
            deleteBtn.setVisible(isMyReview);
            deleteBtn.setManaged(isMyReview);

            // 버튼 이벤트 등록
            deleteBtn.setOnAction(e -> handleDelete(review));
            editButton.setOnAction(e -> openEditPopup(review));

            setGraphic(root);

        } catch (IOException e) {
            e.printStackTrace();
            setText("오류 발생");
        }
    }

    /**
     * 리뷰 삭제 전 확인창 표시 후, 삭제 수행
     */
    private void handleDelete(ReviewListDto review) {
        Alert confirm = new Alert(AlertType.CONFIRMATION, "정말로 삭제하시겠습니까?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                try {
                    int loginUserId = SessionManager.getInstance().getLoggedInUser().getUser_id();
                    int result = dao.delete(review.getReservationId(), loginUserId); // ✅ 메서드 및 파라미터 수정
                    dao.commitIfNeeded();
                    if (result == 1) {
                        getListView().getItems().remove(review); // UI에서도 제거
                        showAlert("리뷰가 삭제되었습니다.");
                    } else {
                        showAlert("삭제에 실패했습니다.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("삭제 중 오류 발생: " + e.getMessage());
                }
            }
        });
    }

    /**
     * 수정 팝업 창 열기
     */
    private void openEditPopup(ReviewListDto reviewListDto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/reviewEdit.fxml"));
            Parent popupRoot = loader.load();

            ReviewEditController controller = loader.getController();

            // ReviewListDto → ReviewDto 변환
            ReviewDto reviewDto = new ReviewDto();
            reviewDto.setReviewerId(reviewListDto.getReviewerId());
            reviewDto.setContent(reviewListDto.getContent());
            reviewDto.setRating(reviewListDto.getRating());
            reviewDto.setReservationId(reviewListDto.getReservationId());
            reviewDto.setCafeId(reviewListDto.getCafeId());

            // 콜백 등록 (수정 완료 시 목록 새로고침)
            controller.setReviewData(reviewDto, () -> {
                getListView().refresh();
            });

            // 팝업 창 표시
            Stage stage = new Stage();
            stage.setScene(new Scene(popupRoot));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("리뷰 수정");
            stage.setResizable(false);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("리뷰 수정 창을 여는 데 실패했습니다.");
        }
    }

    /**
     * 정보 알림창 표시
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
