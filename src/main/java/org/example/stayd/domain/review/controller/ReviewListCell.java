package org.example.stayd.domain.review.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.review.dto.ReviewListDto;
// import org.example.stayd.global.SessionContext;

import java.io.IOException;

/**
 * 리뷰 목록의 개별 셀 (리스트에서 한 줄)
 * - 본인 리뷰일 경우 '삭제', '수정' 버튼 노출
 * - 수정 시 reviewEdit.fxml 새 창으로 열림
 */
public class ReviewListCell extends ListCell<ReviewListDto> {

    @FXML private Label nameLabel, ratingLabel, contentLabel, dateLabel;
    @FXML private Button deleteBtn, editButton;
    private Parent root;

    private final ReviewDao dao = new ReviewDaoImpl();

    /** 현재 로그인 사용자 이름 (임시 하드코딩) */
    private static final int loginUserId = 66; // TODO: SessionContext 로 교체 예정

    public ReviewListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewListCell.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(ReviewListDto dto, boolean empty) {
        super.updateItem(dto, empty);

        if (empty || dto == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(dto.getReviewerName());
            ratingLabel.setText("★ " + dto.getRating());
            contentLabel.setText(dto.getContent());
            dateLabel.setText(dto.getCreatedAt().toLocalDate().toString());

            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(getListView().getWidth() - 40);

            boolean isMine = dto.getReviewerId() == loginUserId;
            deleteBtn.setVisible(isMine);
            editButton.setVisible(isMine);

            deleteBtn.setOnAction(e -> handleDelete(dto.getReservationId()));
            editButton.setOnAction(this::handleEdit);

            setGraphic(root);
        }
    }

    /** 삭제 처리 */
    private void handleDelete(int reservationId) {
        Alert confirm = new Alert(AlertType.CONFIRMATION, "정말로 삭제하시겠습니까?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                try {
                    int result = dao.delete(reservationId, getLoginUserId());
                    dao.commitIfNeeded();
                    if (result == 1) {
                        getListView().getItems().remove(getItem());
                        showInfo("리뷰가 삭제되었습니다.");
                    } else {
                        showInfo("삭제 실패: 리뷰를 찾을 수 없습니다.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showInfo("삭제 중 오류: " + e.getMessage());
                }
            }
        });
    }

    /** 수정 버튼 클릭 시 */
    @FXML
    private void handleEdit(ActionEvent event) {
        ReviewListDto dto = getItem();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewEdit.fxml"));
            Parent root = loader.load();

            ReviewEditController controller = loader.getController();
            controller.setReview(dto);

            // 수정 완료 후 콜백 → reviewList.fxml 다시 열기
            controller.setOnReviewUpdated(() -> {
                try {
                    ((Stage) editButton.getScene().getWindow()).close();

                    FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/org/example/stayd/review/reviewList.fxml"));
                    Parent listRoot = listLoader.load();
                    Stage listStage = new Stage();
                    listStage.setScene(new Scene(listRoot));
                    listStage.setTitle("리뷰 목록");
                    listStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showInfo("리뷰 목록 새로고침 중 오류 발생");
                }
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("리뷰 수정");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showInfo("수정 화면을 불러오는 데 실패했습니다.");
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    // [테스트용] 로그인된 유저의 ID를 반환
    private int getLoginUserId() {
        return 66; // TODO: 통합 시 SessionContext.getCurrentUserId() 로 교체
    }
}
