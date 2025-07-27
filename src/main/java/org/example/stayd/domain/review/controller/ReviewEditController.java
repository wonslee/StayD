// 작성자 : 이해든, 방대혁
package org.example.stayd.domain.review.controller;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.review.dao.ReviewDAO;
import org.example.stayd.domain.review.dto.ReviewDTO;

public class ReviewEditController {

    @FXML
    private ToggleButton star1;
    @FXML
    private ToggleButton star2;
    @FXML
    private ToggleButton star3;
    @FXML
    private ToggleButton star4;
    @FXML
    private ToggleButton star5;

    @FXML
    private TextArea reviewTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private List<ToggleButton> stars;
    private ReviewDTO review;
    private Runnable onReviewUpdatedCallback;

    private final ReservationWDAO reservationWDAO = new ReservationWDAO();

    @FXML
    public void initialize() {
        stars = Arrays.asList(star1, star2, star3, star4, star5);
        for (int i = 0; i < stars.size(); i++) {
            final int index = i;
            stars.get(i).setOnAction(e -> selectStars(index + 1));
        }
    }

    public void setReview(ReviewDTO review) {
        this.review = review;
        reviewTextArea.setText(review.getContent());
        selectStars(review.getRating());
    }

    public void setOnReviewUpdatedCallback(Runnable callback) {
        this.onReviewUpdatedCallback = callback;
    }


    private void selectStars(int count) {
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setSelected(i < count);
        }
    }

    private int getSelectedRating() {
        int rating = 0;
        for (int i = 0; i < stars.size(); i++) {
            if (stars.get(i).isSelected()) {
                rating = i + 1;
            }
        }
        return rating;
    }

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

    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

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