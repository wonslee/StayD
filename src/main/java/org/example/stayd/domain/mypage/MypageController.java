package org.example.stayd.domain.mypage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.reservation.dao.ReservationDaoImpl;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.review.controller.ReviewEditController;
import org.example.stayd.domain.review.controller.ReviewItemCellController;
import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.reservation.service.ReservationService;
import org.example.stayd.domain.review.service.ReviewService;
import org.example.stayd.domain.review.service.ReviewServiceImpl;
import org.example.stayd.domain.user.controller.ResetPwController;
import org.example.stayd.domain.user.dto.PasswordResetDTO;
import org.example.stayd.domain.user.dto.UserDTO;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MypageController {

    @FXML private Label loginIdLabel;
    @FXML private Label emailLabel;
    @FXML private VBox usageListContainer;
    @FXML private VBox reviewListContainer;
    @FXML private Button usageMoreBtn;
    @FXML private Button reviewMoreBtn;
    @FXML private Button btnChangePw;

    // ✅ 그냥 생성자 사용 (익명 클래스 제거)
    private final ReservationService reservationService = new ReservationService();

    private final ReviewService reviewService = new ReviewServiceImpl();

    private final int PAGE_SIZE = 5;
    private int usageLoadedCount = 0;
    private int reviewLoadedCount = 0;

    private List<ReservationDTO> allReservations = new ArrayList<>();
    private List<ReviewDto> allReviews = new ArrayList<>();

    @FXML
    public void initialize() {
        UserDTO loginUser = SessionManager.getInstance().getLoggedInUser();
        if (loginUser == null) return;

        loginIdLabel.setText(loginUser.getLogin_id());
        emailLabel.setText("email: " + loginUser.getEmail());

        int userId = loginUser.getUser_id();
        allReservations = reservationService.findByUser(userId);  // 내부에서 try-catch 처리돼 있음
        try {
            allReviews = reviewService.findAllByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            allReviews = List.of();
        }

        loadMoreUsageItems();
        loadMoreReviewItems();
    }

    @FXML
    private void handleChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/user/resetPw.fxml"));
            Parent root = loader.load();
            ResetPwController controller = loader.getController();
            UserDTO user = SessionManager.getInstance().getLoggedInUser();
            PasswordResetDTO dto = new PasswordResetDTO(user.getLogin_id(), user.getEmail());
            controller.initData(dto);
            btnChangePw.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleLoadMoreUsage() {
        loadMoreUsageItems();
    }

    @FXML private void handleLoadMoreReviews() {
        loadMoreReviewItems();
    }

    private void loadMoreUsageItems() {
        int end = Math.min(usageLoadedCount + PAGE_SIZE, allReservations.size());
        for (int i = usageLoadedCount; i < end; i++) {
            ReservationDTO res = allReservations.get(i);
            LocalDate date = res.getCreatedAt().toLocalDate();
            LocalTime startTime = LocalTime.of(res.getUsageStartedAt(), 0);
            LocalTime endTime = LocalTime.of(res.getUsageEndedAt(), 0);
            String content = "📍 " + res.getCafeName() + " | " + date + " " + startTime + " ~ " + endTime;
            addUsageItem(content, -1);
        }
        usageLoadedCount = end;
        if (usageLoadedCount >= allReservations.size()) {
            usageMoreBtn.setVisible(false);
            usageMoreBtn.setManaged(false);
        }
    }

    private void loadMoreReviewItems() {
        int end = Math.min(reviewLoadedCount + PAGE_SIZE, allReviews.size());
        for (int i = reviewLoadedCount; i < end; i++) {
            ReviewDto review = allReviews.get(i);
            addReviewItem(review);
        }
        reviewLoadedCount = end;
        if (reviewLoadedCount >= allReviews.size()) {
            reviewMoreBtn.setVisible(false);
            reviewMoreBtn.setManaged(false);
        }
    }

    private void addReviewItem(ReviewDto review) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewItemCell.fxml"));
            Parent cell = loader.load();
            ReviewItemCellController controller = loader.getController();
            controller.setData(review);  // ✅ ReviewDto 그대로 넘김

            controller.setOnEdit(() -> handleEditReview(review));
            controller.setOnDelete(() -> handleDeleteReview(review.getReservationId(), review.getReviewerId(), cell));

            reviewListContainer.getChildren().add(cell);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addUsageItem(String content, int rating) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewItemCell.fxml"));
            Parent cell = loader.load();
            ReviewItemCellController controller = loader.getController();
            controller.setData(content, rating);
            usageListContainer.getChildren().add(cell);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEditReview(ReviewDto review) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewEdit.fxml"));
            Parent root = loader.load();
            ReviewEditController controller = loader.getController();

            controller.setReviewData(review, this::refreshReviews);

            Stage popup = new Stage();
            popup.setTitle("리뷰 수정");
            popup.initModality(Modality.WINDOW_MODAL);
            popup.initOwner(reviewListContainer.getScene().getWindow());
            popup.setScene(new Scene(root));
            popup.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteReview(int reservationId, int userId, Parent cell) {
        boolean success = reviewService.deleteReview(reservationId, userId);
        if (success) {
            reviewListContainer.getChildren().remove(cell);
            System.out.println("리뷰 삭제 성공");
        } else {
            System.out.println("리뷰 삭제 실패");
        }
    }

    private void refreshReviews() {
        reviewListContainer.getChildren().clear();
        reviewLoadedCount = 0;

        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();
            allReviews = reviewService.findAllByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            allReviews = List.of();
        }

        loadMoreReviewItems();
    }
}
