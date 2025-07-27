package org.example.stayd.domain.review.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.review.dao.ReviewListDAO;
import org.example.stayd.domain.review.dto.ReviewListDTO;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
/**
 * 리뷰 리스트 컨트롤러
 * - 특정 카페에 대한 리뷰 목록을 ListView로 출력
 */
public class ReviewListController implements Initializable {

    @FXML private Label averageRatingLabel;           // 평균 평점 표시용 라벨
    @FXML private ListView<ReviewListDTO> reviewListView; // 리뷰 목록 리스트뷰

    private final ReviewListDAO reviewListDAO = new ReviewListDAO();
    private int cafeId; // 이건 외부에서 setCafeId로 주입받음
    /**
     * 초기화 메서드 (cafeId 주입 이후 loadReviews 호출됨)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 초기화 시에는 아무것도 안 함 (cafeId 설정 후 loadReviews 호출)
    }

    /**
     * 외부에서 카페 ID를 주입받을 때 사용하는 메서드
     * - cafeId가 설정되면 리뷰 목록을 로딩함
     */
    public void setCafeId(int cafeId) {
        this.cafeId = cafeId;
        loadReviews(); // 해당 카페의 리뷰 로딩
    }

    /**
     * 해당 카페의 평균 평점과 리뷰 목록을 불러오는 메서드
     */
    private void loadReviews() {
        try (Connection conn = new DatabaseConnection().getConnection()) {
            // 평균 평점 가져와서 표시
            double avg = reviewListDAO.findAverageRatingByCafeId(conn, cafeId);
            averageRatingLabel.setText(String.format("⭐ 평균 평점: %.1f점", avg));

            // 리뷰 목록 가져오기
            List<ReviewListDTO> reviews = reviewListDAO.findReviewsByCafeId(conn, cafeId);


// ListView에 셀 팩토리 설정 (중요!)
            reviewListView.setCellFactory(list -> new ReviewListCell());


            // 리스트뷰에 리뷰 데이터 주입
            reviewListView.getItems().setAll(reviews);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}