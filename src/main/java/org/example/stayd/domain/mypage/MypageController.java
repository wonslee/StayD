package org.example.stayd.domain.mypage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.reservation.controller.ReservationItemCellController;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.reservation.dto.ReservationWithCafeDTO;
import org.example.stayd.domain.review.controller.ReviewController;
import org.example.stayd.domain.review.controller.ReviewItemCellController;
import org.example.stayd.domain.review.dto.ReviewDTO;
import org.example.stayd.domain.user.controller.ResetPwController;
import org.example.stayd.domain.user.dto.PasswordResetDTO;
import org.example.stayd.domain.user.dto.UserDTO;
import org.example.stayd.domain.review.dao.ReviewListDAO;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MypageController implements Initializable {

    @FXML private AnchorPane headerPlaceholder;

    @FXML private ListView<ReservationWithCafeDTO> reservationListView;
    @FXML private ListView<ReviewDTO> reviewListView;
    @FXML private Label nicknameLabel;
    @FXML private Label emailLabel;
    @FXML private Button changePwButton;

    private final ReservationWDAO reservationWDAO = new ReservationWDAO();
    private final ReviewListDAO reviewListDao = new ReviewListDAO();

    private List<ReservationWithCafeDTO> fullReservationList = new ArrayList<>();
    private List<ReviewDTO> fullReviewList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadHeader();            // 헤더 삽입
        loadReservationList();   // 예약 불러오기
        refreshReviewList();

        UserDTO user = SessionManager.getInstance().getLoggedInUser();
        if (user != null) {
            nicknameLabel.setText("아이디: " + user.getLogin_id());
            emailLabel.setText("이메일: " + user.getEmail());
        }
// 리뷰 불러오기

        System.out.println("로그인 유저: " + SessionManager.getInstance().getLoggedInUser());
    }

    private void loadHeader() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/common/header.fxml"));
            HBox header = loader.load();
            headerPlaceholder.getChildren().add(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReservationList() {
        try (Connection conn = new DatabaseConnection().getConnection()) {
            long userId = SessionManager.getInstance().getLoggedInUser().getUserId();
            try {
                fullReservationList = reservationWDAO.findWithCafeByUser(userId);
            } catch (Exception e) {
                System.out.println("예약 목록 가져오는 중 예외 발생");
                e.printStackTrace();
            }

            System.out.println("예약 개수: " + fullReservationList.size());

            reservationListView.getItems().setAll(fullReservationList);

            reservationListView.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(ReservationWithCafeDTO reservation, boolean empty) {
                    super.updateItem(reservation, empty);

                    System.out.println("📦 updateItem() 호출됨 → empty = " + empty + ", reservation = " + reservation);

                    if (empty || reservation == null) {
                        setGraphic(null);
                    } else {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/mypage/reservationCell.fxml"));
                            Parent cellRoot = loader.load();

                            ReservationItemCellController controller = loader.getController();
                            System.out.println(reservation);
                            controller.setData(reservation);
                            controller.setMypageController(MypageController.this);

                            setGraphic(cellRoot);

                            System.out.println("✅ 셀 생성 완료: " + reservation.getCafeName());

                        } catch (Exception e) {
                            System.out.println("❌ FXML 로딩 실패");
                            e.printStackTrace();
                            setGraphic(null);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("예약 불러오기 중 오류 발생");
        }
    }

    public void refreshReviewList() {
        try (Connection conn = new DatabaseConnection().getConnection()) {
            long userId = SessionManager.getInstance().getLoggedInUser().getUserId();
            fullReviewList = reviewListDao.findByUserId(conn, userId);

            reviewListView.getItems().setAll(fullReviewList);

            reviewListView.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(ReviewDTO dto, boolean empty) {
                    super.updateItem(dto, empty);

                    if (empty || dto == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        try {
                            System.out.println("리뷰 셀 생성: " + dto.getContent());

                            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                                    "/org/example/stayd/review/reviewItemCell.fxml"));
                            Parent cellRoot = loader.load();

                            ReviewItemCellController controller = loader.getController();
                            controller.setData(dto);
                            controller.setMypageController(MypageController.this);

                            setGraphic(cellRoot);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            System.out.println("리뷰 개수: " + fullReviewList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleWriteReviewButton(ReservationWithCafeDTO reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/review.fxml"));
            Parent root = loader.load();

            ReviewController controller = loader.getController();
            controller.setReservation(reservation);
            controller.setOnReviewSubmittedCallback(v -> refreshReviewList());

            Stage stage = new Stage();
            stage.setTitle("리뷰 작성");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleChangePw(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/user/resetPw.fxml"));
            Parent root = loader.load();

            ResetPwController controller = loader.getController();

            // 로그인 유저 정보를 PasswordResetDTO로 생성
            UserDTO user = SessionManager.getInstance().getLoggedInUser();
            PasswordResetDTO dto = new PasswordResetDTO();
            dto.setUserId(user.getUserId());
            dto.setLoginId(user.getLogin_id());

            // ✅ initData() 메서드로 전달
            controller.initData(dto);

            // 👉 현재 마이페이지 Stage 가져오기
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // 👉 현재 Stage에 새로운 Scene 설정 (즉, 페이지 이동)
            currentStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 마우스가 버튼 위에 올라갔을 때 색상 변경
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseEnter(MouseEvent event) {
        changePwButton.setStyle(
                "-fx-background-color: #4caf4f; -fx-font-weight: bold; -fx-background-radius: 10;");
        changePwButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    /**
     * 마우스가 버튼을 벗어났을 때 원래 색상으로 복원
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseExit(MouseEvent event) {
        changePwButton.setStyle(
                "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        changePwButton.setTextFill(javafx.scene.paint.Color.web("#4caf4f"));
    }
}