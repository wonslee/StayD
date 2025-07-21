package org.example.stayd.domain.review.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.example.stayd.domain.review.dto.ReviewListDto;
import org.example.stayd.domain.review.service.ReviewListService;
import org.example.stayd.domain.review.service.ReviewListServiceImpl;
// import org.example.stayd.global.SessionContext; // TODO: 나중에 주석 해제 - 카페 정보 세션에서 받을 경우

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ───────────────────────────────────────────────────────────────
 *  화면 : reviewList.fxml  (리뷰 목록 + 평균 평점 + 페이지네이션)
 *  역할 :
 *   - 특정 카페(cafeId)의 리뷰를 DB에서 조회해서
 *     ListView 및 Pagination 컴포넌트에 표시
 *   - 페이징 처리 (5개씩)
 * ───────────────────────────────────────────────────────────────
 */
public class ReviewListController implements Initializable {

    /* ────── FXML 바인딩 컴포넌트 ────── */
    @FXML private Label ratingAvgLabel;          // 평균 평점 표시 (ex. ★ 4.2)
    @FXML private Label reviewCountLabel;        // 리뷰 개수 표시 (ex. 리뷰 5개)
    @FXML private ListView<ReviewListDto> reviewListView;  // 각 리뷰 보여주는 목록
    @FXML private Pagination pagination;         // 페이지네이션

    /* ────── 비즈니스 로직 처리용 서비스 객체 ────── */
    private final ReviewListService svc = new ReviewListServiceImpl();

    /* ────── 화면 구성 상수 ────── */
    private static final int ROWS = 5;           // 페이지당 보여줄 리뷰 수

    /* ────── 현재 조회 중인 카페 ID ────── */
    private int cafeId = 1;                      // ⚠ 테스트용 cafeId (임시)  // TODO: 나중에 제거

    // private int cafeId = SessionContext.getCurrentCafeId(); // TODO: 나중에 주석 해제 → 실제 로그인 연동 시 사용

    private List<ReviewListDto> all;             // DB에서 가져온 전체 리뷰 목록

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ListView 의 각 셀을 우리가 만든 ReviewListCell 로 설정
        reviewListView.setCellFactory(lv -> new ReviewListCell());

        // 페이지가 바뀔 때마다 createPage 메서드가 호출되어 새로운 페이지 생성
        pagination.setPageFactory(this::createPage);

        // 초기 데이터 로딩
        load();
    }

    /**
     * 외부(다른 화면)에서 카페 ID를 받아올 수 있게 세터 제공
     * ex) CafeDetailController 등에서 호출 가능
     */
    public void setCafeId(int cafeId) {
        this.cafeId = cafeId;
        load();                 // 새 ID로 다시 로딩
    }

    /**
     * DB에서 리뷰 불러오기 + 평균 평점 계산 + Pagination 초기화
     */
    private void load() {
        // 1. 해당 카페의 리뷰 전체 조회
        all = svc.getReviews(cafeId);

        // 2. 평균 평점 계산 후 Label 표시
        ratingAvgLabel.setText(String.format("%.1f", svc.getAverageRating(cafeId)));

        // 3. "리뷰 n개" 텍스트 표시
        reviewCountLabel.setText("리뷰 " + all.size() + "개");

        // 4. 전체 페이지 수 계산 → Pagination 세팅
        int pageCnt = Math.max(1, (int) Math.ceil(all.size() / (double) ROWS));
        pagination.setPageCount(pageCnt);
        pagination.setCurrentPageIndex(0);
        createPage(0);   // 첫 페이지 로딩
    }

    /**
     * Pagination이 호출하는 콜백
     * 해당 페이지 번호에 맞는 리뷰 5개만 ListView에 설정
     */
    private Node createPage(int idx) {
        int from = idx * ROWS;
        int to   = Math.min(from + ROWS, all.size());

        // from ~ to 사이 리뷰를 리스트뷰에 주입
        reviewListView.setItems(FXCollections.observableArrayList(all.subList(from, to)));

        return reviewListView;
    }
}
/*
  TODO (통합 시)
  1. cafeId → 외부에서 setCafeId()로 반드시 주입되도록 변경
     또는 SessionContext 등에서 현재 선택된 카페 ID를 가져오도록 통합
     - 현재: private int cafeId = 1; (임시)
     - 나중에: private int cafeId = SessionContext.getCurrentCafeId();

  2. CafeDetailController 등에서 ReviewListController에 cafeId 전달 연동 필요
  3. SessionContext 사용 시 import 해제 필요
*/
