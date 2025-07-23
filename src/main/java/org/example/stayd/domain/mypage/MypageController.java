package org.example.stayd.domain.mypage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import org.example.stayd.domain.review.dao.ReviewListDao;
import org.example.stayd.domain.review.dto.ReviewDTO;

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

    private final ReservationWDAO reservationWDAO = new ReservationWDAO();
    private final ReviewListDao reviewListDao = new ReviewListDao();

    private List<ReservationWithCafeDTO> fullReservationList = new ArrayList<>();
    private List<ReviewDTO> fullReviewList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadHeader();            // 헤더 삽입
        loadReservationList();   // 예약 불러오기
        refreshReviewList();     // 리뷰 불러오기

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
                fullReservationList = reservationWDAO.findWithCafeByUser(conn, userId);
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
}