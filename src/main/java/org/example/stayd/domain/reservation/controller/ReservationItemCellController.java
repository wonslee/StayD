package org.example.stayd.domain.reservation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.mypage.MypageController;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.dto.ReservationWithCafeDTO;
import org.example.stayd.domain.review.dao.ReviewDAO;
import org.example.stayd.domain.cafe.dto.CafeDto;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.example.stayd.common.FXUtils;

public class ReservationItemCellController {

    @FXML private Label cafeNameLabel;
    @FXML private Label reservationDateLabel;
    @FXML private Label timeLabel;
    @FXML private Button writeReviewButton;

    private ReservationWithCafeDTO reservation;
    private ReservationDTO reservationDTO;
    private CafeDto.DetailResponse cafeDetail;
    private MypageController mypageController;

    public void setMypageController(MypageController controller) {
        this.mypageController = controller;
    }

    public void setReservationDTO(ReservationDTO dto) {
        this.reservationDTO = dto;
    }
    public void setCafeDetail(CafeDto.DetailResponse cafe) {
        this.cafeDetail = cafe;
    }

    public void setData(ReservationWithCafeDTO reservation) {
        System.out.println("reservation = " + reservation);
        this.reservation = reservation;
        this.reservationDTO = ReservationDTO.of(reservation);
        this.cafeDetail = reservation.toCafeDetailResponse();
        System.out.println("reservationDTO = " + reservationDTO);
        System.out.println("cafeDetail = " + cafeDetail);
        System.out.println("리뷰 체크 - rating: " + reservation.getRating() + ", content: " + reservation.getContent());


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
    private void handleReservationBoxClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/reservation/reservation-detail.fxml"));
            Node detailView = loader.load();
            org.example.stayd.domain.reservation.controller.ReservationDetailController detailController = loader.getController();
            detailController.setReservation(this.reservationDTO);
            detailController.setCafe(this.cafeDetail);
            detailController.updateUI();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene((javafx.scene.Parent) detailView));
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
                    ReviewDAO reviewDAO = new ReviewDAO();
                    boolean deleted = reviewDAO.deleteReview(conn, reservation.getReservationId());
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

    // 버튼 호버 효과들
    @FXML
    private void writeReviewButtonEnter(MouseEvent event) {
        writeReviewButton.setStyle(
                "-fx-background-color: #4caf4f; -fx-font-weight: bold; -fx-background-radius: 10;");
        writeReviewButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    @FXML
    private void writeReviewButtonExit(MouseEvent event) {
        writeReviewButton.setStyle(
                "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        writeReviewButton.setTextFill(javafx.scene.paint.Color.web("#4caf4f"));
    }
}
