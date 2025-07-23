package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.reservation.dto.ReservationWithCafeDTO;
import org.example.stayd.domain.user.dto.UserDTO;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ReviewController {

    @FXML private TextArea reviewContentArea;
    @FXML private Button submitButton, cancelButton;

    @FXML private ToggleGroup starGroup; // 연결 필요
    @FXML private HBox ratingBox; // 별점 버튼들이 들어있는 HBox

    @FXML private Label cafeName;
    @FXML private Label useDate;
    @FXML private Label useTime;
    @FXML private Label branchName;

    private ReservationWithCafeDTO selectedReservation;
    private final ReservationWDAO reservationWDAO = new ReservationWDAO();
    private Consumer<Void> onReviewSubmittedCallback;

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

    public void setOnReviewSubmittedCallback(Consumer<Void> callback) {
        this.onReviewSubmittedCallback = callback;
    }

    @FXML
    private void initialize() {
        // 버튼 핸들러 등록 제거 (FXML에서 이미 onAction 지정됨)
        // submitButton.setOnAction(this::handleSubmit);
        // cancelButton.setOnAction(event -> submitButton.getScene().getWindow().hide());
    }

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
        System.out.println("📝 입력된 리뷰 내용: " + reviewContentArea.getText().trim());
        System.out.println("💾 대상 예약 ID: " + reservationId);

        if (selectedRating == 0) {
            showAlert("별점을 선택해주세요.");
            return;
        }

        String content = reviewContentArea.getText().trim();
        if (content.isEmpty()) {
            showAlert("리뷰 내용을 입력해주세요.");
            return;
        }

        try (Connection conn = new DatabaseConnection().getConnection()) {
            if (reservationWDAO.existsReviewByReservationId(conn, reservationId)) {
                showAlert("이미 작성한 리뷰입니다.");
                return;
            }

            if (!reservationWDAO.isReservationFinished(conn, reservationId)) {
                showAlert("아직 이용이 완료되지 않았습니다.");
                return;
            }

            boolean result = reservationWDAO.saveReview(conn, reservationId, selectedRating, content);

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

    private int getSelectedRating() {
        Toggle selected = starGroup.getSelectedToggle();
        if (selected != null) {
            for (int i = 0; i < ratingBox.getChildren().size(); i++) {
                if (ratingBox.getChildren().get(i) == selected) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        cancelButton.getScene().getWindow().hide();
    }
}
