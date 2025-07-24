package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.review.dao.ReviewDAO;
import org.example.stayd.domain.review.dto.ReviewDTO;
import org.example.stayd.domain.review.controller.ReviewEditController;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.mypage.MypageController;
import org.example.stayd.common.DatabaseConnection;

import java.sql.Connection;

public class ReviewItemCellController {

    @FXML private Label ratingLabel;
    @FXML private Label contentLabel;
    @FXML private Label dateLabel;

    @FXML private Button editReviewButton;
    @FXML private Button deleteReviewButton;
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private ReviewDTO review;
    private MypageController mypageController;
    @FXML private Label cafeNameLabel;

    public void setData(ReviewDTO dto) {
        this.review = dto;

        cafeNameLabel.setText(dto.getCafeName());
        ratingLabel.setText("⭐ " + dto.getRating() + "점");
        contentLabel.setText(dto.getContent());
        dateLabel.setText("작성일: " + dto.getReviewCreatedAt().toLocalDate());

        long loginUserId = SessionManager.getInstance().getLoggedInUser().getUserId();

        boolean isMyReview = (dto.getUserId() == loginUserId);
        editReviewButton.setVisible(isMyReview);
        deleteReviewButton.setVisible(isMyReview);
    }

    public void setMypageController(MypageController controller) {
        this.mypageController = controller;
    }

    @FXML
    private void onEditClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewEdit.fxml"));
            Parent root = loader.load();

            ReviewEditController controller = loader.getController();
            controller.setReview(review);
            controller.setOnReviewUpdatedCallback(() -> {
                if (mypageController != null) {
                    mypageController.refreshReviewList();
                }
            });

            Stage stage = new Stage();
            stage.setTitle("리뷰 수정");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteClicked() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("리뷰 삭제");
        confirm.setHeaderText(null);
        confirm.setContentText("정말 이 리뷰를 삭제하시겠습니까?");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try (Connection conn = new DatabaseConnection().getConnection()) {
                    boolean success = new ReviewDAO().deleteReview(conn, review.getReservationId());
                    if (success) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setContentText("리뷰가 삭제되었습니다.");
                        successAlert.showAndWait();

                        if (mypageController != null) {
                            mypageController.refreshReviewList();
                        }
                    } else {
                        showError("삭제에 실패했습니다.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("오류가 발생했습니다.");
                }
            }
        });
    }

    private void showError(String msg) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setHeaderText(null);
        error.setContentText(msg);
        error.showAndWait();
    }
}
