package org.example.stayd.domain.reservation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.mypage.MypageController;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.reservation.dto.ReservationWithCafeDTO;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;

public class ReservationItemCellController {

    @FXML private Label cafeNameLabel;
    @FXML private Label reservationDateLabel;
    @FXML private Label timeLabel;
    @FXML private Button writeReviewButton;

    private ReservationWithCafeDTO reservation;
    private MypageController mypageController;

    public void setMypageController(MypageController controller) {
        this.mypageController = controller;
    }

    public void setData(ReservationWithCafeDTO reservation) {
        this.reservation = reservation;

        cafeNameLabel.setText( reservation.getCafeName());
        reservationDateLabel.setText("예약일: " + reservation.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")));
        timeLabel.setText("이용 시간: " + reservation.getUsageStartedAt() + "시 ~ " + reservation.getUsageEndedAt() + "시");

        boolean reviewed = reservation.getRating() > 0 || (reservation.getContent() != null && !reservation.getContent().isEmpty());
        writeReviewButton.setDisable(reviewed);
        writeReviewButton.setVisible(!reviewed);

        writeReviewButton.setOnAction(e -> {
            if (mypageController != null) {
                mypageController.handleWriteReviewButton(reservation);
            }
        });
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
                    boolean deleted = new ReservationWDAO().deleteReview(conn, reservation.getReservationId());
                    if (deleted) {
                        showAlert("리뷰가 삭제되었습니다.");
                        this.cafeNameLabel.getScene().getWindow().hide(); // 셀 숨기기 또는 새로고침 처리
                    } else {
                        showAlert("삭제에 실패했습니다.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("오류가 발생했습니다.");
                }
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
