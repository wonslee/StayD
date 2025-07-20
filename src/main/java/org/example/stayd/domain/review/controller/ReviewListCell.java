package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.review.dto.ReviewListDto;
// import org.example.stayd.global.SessionContext; // TODO: 나중에 주석 해제 - 로그인 정보 연동 시 필요

import java.io.IOException;

/**
 * ListView 셀 하나 (리뷰 1건) 담당
 *  └ 본인 글이면 삭제 버튼 노출 → 확인 후 DB 삭제 & UI 갱신
 */
public class ReviewListCell extends ListCell<ReviewListDto> {

    // ── FXML 바인딩 ──
    @FXML private Label nameLabel, ratingLabel, contentLabel, dateLabel;
    @FXML private Button deleteBtn;
    private Parent root;

    private final ReviewDao dao = new ReviewDaoImpl();

    /** ⚠️ 테스트용 사용자 이름 (임시) */
    private static final String loginUserName = "eunji_l"; // TODO: 나중에 제거 → 아래 SessionContext 방식으로 교체할 것

    // private static final String loginUserName = SessionContext.getCurrentUserName(); // TODO: 나중에 주석 해제 → 실제 로그인 사용자 연동

    public ReviewListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/stayd/review/reviewListCell.fxml"));
            loader.setController(this);
            root = loader.load();
            /* FXML 정상 로딩 확인 */
            System.out.println("[DEBUG] deleteBtn wired? " + deleteBtn);
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
            /* ─ 값 바인딩 ─ */
            nameLabel.setText(dto.getReviewerName());
            ratingLabel.setText("★ " + dto.getRating());
            contentLabel.setText(dto.getContent());
            dateLabel.setText(dto.getCreatedAt().toLocalDate().toString());

            contentLabel.setMaxWidth(getListView().getWidth() - 40);
            contentLabel.setWrapText(true);

            /* ─ 삭제 버튼 권한 & 이벤트 ─ */
            boolean isMine = dto.getReviewerName().equals(loginUserName);
            System.out.println("[DEBUG] reviewer=" + dto.getReviewerName()
                    + ", login=" + loginUserName + " → isMine=" + isMine);

            deleteBtn.setVisible(isMine);
            deleteBtn.setOnAction(e -> handleDelete(dto.getId()));

            setGraphic(root);
        }
    }

    /** 삭제 확인 → DB 삭제 → ListView 갱신 */
    private void handleDelete(int reviewId) {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "정말로 삭제하시겠습니까?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                try {
                    int result = dao.delete(reviewId);
                    dao.commitIfNeeded();
                    if (result == 1) {
                        getListView().getItems().remove(getItem());
                        showInfo("리뷰가 삭제되었습니다.");
                    } else {
                        showInfo("삭제 실패: 리뷰를 찾을 수 없습니다.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showInfo("삭제 중 오류: " + ex.getMessage());
                }
            }
        });
    }

    /** 단순 알림 */
    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
/*
  TODO (통합 시)
  1. loginUserName → SessionContext.getCurrentUserName() 으로 교체
     - 현재: private static final String loginUserName = "eunji_l";
     - 변경: private static final String loginUserName = SessionContext.getCurrentUserName();
     - import org.example.stayd.global.SessionContext; 필요
     - 기존 하드코딩 라인 제거 필요

  2. FXML 위치 확인 및 reviewListCell.fxml 내용 정비 필요 시 반영
*/
