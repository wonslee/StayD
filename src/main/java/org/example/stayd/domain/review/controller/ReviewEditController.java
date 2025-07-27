package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.stayd.domain.review.dao.ReviewDAO;
import org.example.stayd.domain.review.dto.ReviewDTO;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.common.DatabaseConnection;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
/**
 * 리뷰 수정 컨트롤러
 * - 기존 리뷰 내용을 불러와 수정할 수 있도록 제공
 * - 수정된 리뷰를 저장 또는 취소
 */
public class ReviewEditController {

    @FXML private ToggleButton star1;
    @FXML private ToggleButton star2;
    @FXML private ToggleButton star3;
    @FXML private ToggleButton star4;
    @FXML private ToggleButton star5;

    @FXML private TextArea reviewTextArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private List<ToggleButton> stars;
    private ReviewDTO review;
    private Runnable onReviewUpdatedCallback;

    private final ReservationWDAO reservationWDAO = new ReservationWDAO();
    /**
     * 컨트롤러 초기화
     * 별점 버튼 초기화 및 이벤트 설정
     */
    @FXML
    public void initialize() {
        stars = Arrays.asList(star1, star2, star3, star4, star5);
        for (int i = 0; i < stars.size(); i++) {
            final int index = i;
            stars.get(i).setOnAction(e -> selectStars(index + 1));
        }
    }
    /**
     * 리뷰 데이터를 세팅하여 별점과 내용 초기화
     */
    public void setReview(ReviewDTO review) {
        this.review = review;
        reviewTextArea.setText(review.getContent());
        selectStars(review.getRating());
    }
    /**
     * 수정 완료 후 동작할 콜백을 등록
     */
    public void setOnReviewUpdatedCallback(Runnable callback) {
        this.onReviewUpdatedCallback = callback;
    }

    /**
     * 선택된 별점만큼 버튼 활성화
     */
    private void selectStars(int count) {
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setSelected(i < count);
        }
    }
    /**
     * 현재 선택된 별점 수를 반환
     * @return 별점 수
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
     * 저장 버튼 클릭 시 리뷰를 DB에 수정 반영
     */
    @FXML
    private void handleSave() {
        int rating = getSelectedRating();
        String content = reviewTextArea.getText().trim();

        if (rating == 0 || content.isEmpty()) {
            showAlert("별점과 리뷰 내용을 모두 입력해주세요.");
            return;
        }

        try (Connection conn = new DatabaseConnection().getConnection()) {
            boolean success = reviewDAO.updateReview(conn, review.getReservationId(), rating, content);
            if (success) {
                showAlert("리뷰가 수정되었습니다.");
                if (onReviewUpdatedCallback != null) {
                    onReviewUpdatedCallback.run();
                }
                close();
            } else {
                showAlert("리뷰 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류가 발생했습니다.");
        }
    }
    /**
     * 취소 버튼 클릭 시 창을 닫음
     */
    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    /**
     * 경고창을 띄움
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // 버튼 호버 효과들
    @FXML
    private void onModifyButtonEnter(MouseEvent event) {
        saveButton.setStyle(
                "-fx-background-color: #4caf4f; -fx-font-weight: bold; -fx-background-radius: 10;");
        saveButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onModifyButtonExit(MouseEvent event) {
        saveButton.setStyle(
                "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        saveButton.setTextFill(javafx.scene.paint.Color.web("#4caf4f"));
    }

    @FXML
    private void onDeleteButtonEnter(MouseEvent event) {
        cancelButton.setStyle(
                "-fx-background-color: #ff6b6b; -fx-font-weight: bold; -fx-background-radius: 10; ");
        cancelButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void onDeleteButtonExit(MouseEvent event) {
        cancelButton.setStyle(
                "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        cancelButton.setTextFill(javafx.scene.paint.Color.web("#ff6b6b"));
    }
}