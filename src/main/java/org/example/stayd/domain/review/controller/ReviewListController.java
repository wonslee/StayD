package org.example.stayd.domain.review.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import org.example.stayd.domain.review.dto.ReviewListDto;
import org.example.stayd.domain.review.service.ReviewListService;
import org.example.stayd.domain.review.service.ReviewListServiceImpl;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ───────────────────────────────────────────────────────────────
 *  화면 : reviewList.fxml  (리뷰 목록 + 평균 평점 + 페이지네이션)
 *  역할 :
 *   - 외부에서 cafeId 를 주입받아 해당 카페의 리뷰 목록을 조회
 *   - 평균 평점 및 리뷰 수 표시
 *   - ListView 및 Pagination을 활용한 페이징 처리
 * ───────────────────────────────────────────────────────────────
 */
public class ReviewListController implements Initializable {

    /* ────── FXML 컴포넌트 ────── */
    @FXML private Label ratingAvgLabel;      // 평균 평점 표시 라벨
    @FXML private Label reviewCountLabel;    // 리뷰 개수 표시 라벨
    @FXML private ListView<ReviewListDto> reviewListView;
    @FXML private Pagination pagination;

    /* ────── 내부 필드 ────── */
    private final ReviewListService svc = new ReviewListServiceImpl();

    private static final int ROWS = 5; // 페이지당 리뷰 수
    private int cafeId;                // 외부에서 주입되는 카페 ID
    private List<ReviewListDto> all;  // DB에서 가져온 전체 리뷰 목록

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ListView 셀 커스터마이징
        reviewListView.setCellFactory(lv -> new ReviewListCell());

        // 페이지 변경 시 호출될 콜백 등록
        pagination.setPageFactory(this::createPage);
    }

    /**
     * 외부 컨트롤러에서 카페 ID를 주입받을 수 있도록 세터 제공
     * ex) CafeDetailController에서 setCafeId() 호출
     */
    public void setCafeId(int cafeId) {
        this.cafeId = cafeId;
        load(); // 주입된 ID 기준으로 리뷰 데이터 로딩
    }

    /**
     * DB에서 리뷰 목록 로딩 + 평균 평점 + 리뷰 수 표시 + 페이지네이션 설정
     */
    private void load() {
        // 1. 리뷰 전체 조회
        all = svc.getReviews(cafeId);

        // 2. 평균 평점 표시
        double avg = svc.getAverageRating(cafeId);
        ratingAvgLabel.setText(String.format("%.1f", avg));

        // 3. 리뷰 개수 표시
        reviewCountLabel.setText("리뷰 " + all.size() + "개");

        // 4. 페이지 수 계산 및 설정
        int pageCnt = Math.max(1, (int) Math.ceil(all.size() / (double) ROWS));
        pagination.setPageCount(pageCnt);
        pagination.setCurrentPageIndex(0);
        createPage(0); // 첫 페이지 미리 로딩
    }

    /**
     * Pagination이 호출하는 콜백
     * - 현재 페이지에 맞는 리뷰 5개만 ListView에 표시
     */
    private Node createPage(int pageIndex) {
        int from = pageIndex * ROWS;
        int to = Math.min(from + ROWS, all.size());

        reviewListView.setItems(FXCollections.observableArrayList(all.subList(from, to)));
        return reviewListView;
    }

    /**
     * 정보 출력용 Alert
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
