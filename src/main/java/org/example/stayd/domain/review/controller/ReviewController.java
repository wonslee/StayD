package org.example.stayd.domain.review.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ─────────────────────────────────────────────────────────────────────
 * 화면 : 리뷰 작성 (Review.fxml)
 * 역할 : 입력 받은 별점 + 텍스트를 DB(review 테이블)에 INSERT
 *        테스트 편의용(로그인/예약 미구현 시) 더미 ID 자동생성
 * ─────────────────────────────────────────────────────────────────────
 */
public class ReviewController {

    /* ───────── FXML 컴포넌트 바인딩 ───────── */
    @FXML private ToggleButton star1, star2, star3, star4, star5; // 별 5개
    @FXML private TextArea     reviewTextArea;                    // 리뷰 입력
    private ToggleButton[]     stars;                             // 별 배열 (편의용)

    /* ───────── 외부 화면에서 주입받는 값 ───────── */
    private int reviewerId;   // 로그인한 유저 ID
    private int cafeId;       // 리뷰 대상 카페 ID
    public void initData(int reviewerId, int cafeId) {
        this.reviewerId = reviewerId;
        this.cafeId     = cafeId;
    }

    /* ───────── 테스트 전용 시퀀스 (ID 자동 증가) ───────── */
    // TODO: 통합 시 삭제 (정상 로그인·예약 화면에서 값 주입됨)
    private static final AtomicInteger FAKE_REVIEWER_SEQ = new AtomicInteger(20); // 20,21,22…


    /* ───────── DAO 객체 ───────── */
    private final ReviewDao dao = new ReviewDaoImpl();

    /* ───────── 초기화 : 별 버튼 클릭 로직 바인딩 ───────── */
    @FXML
    private void initialize() {
        stars = new ToggleButton[]{ star1, star2, star3, star4, star5 };

        // 클릭된 번호(idx) 이하 별 모두 선택 상태로 변경
        for (int i = 0; i < stars.length; i++) {
            final int idx = i;
            stars[i].setOnAction(e -> fillStars(idx));
        }
    }

    /** idx 이하 별 선택(노랗게), 나머지 해제(회색) */
    private void fillStars(int idx) {
        for (int i = 0; i < stars.length; i++) {
            stars[i].setSelected(i <= idx);
        }
    }
    /** 현재 선택된 별 개수(=별점) */
    private int getRating() {
        int score = 0;
        for (ToggleButton btn : stars)
            if (btn.isSelected()) score++;
        return score;
    }

    /* ───────── 버튼 핸들러 ───────── */
    @FXML
    private void handleCancel(ActionEvent e) { close(e); }

    @FXML
    private void handleSubmit(ActionEvent e) {
        /* 1️. DTO 구성 */
        ReviewDto dto = new ReviewDto();
        // TODO: initData() 미호출 시 테스트용 더미 ID 부여. 통합 후 제거
        dto.setReviewerId(reviewerId == 0 ? FAKE_REVIEWER_SEQ.getAndIncrement() : reviewerId);
        dto.setCafeId(1);
        dto.setRating    (getRating());
        dto.setContent   (reviewTextArea.getText().trim());

        /* 2️. 최소 입력 검증 */
        if (dto.getRating() == 0)       { alert("별점을 선택해야 합니다."); return; }
        if (dto.getContent().isBlank()) { alert("리뷰를 작성해야 합니다."); return; }

        /* 3️. INSERT + COMMIT + 로그 */
        try {
            int rows = dao.insert(dto);               // DB INSERT
            System.out.println("insert rows = " + rows);

            dao.commitIfNeeded();                     // 수동 커밋
            System.out.println("commit done");

            if (rows == 1) {
                alert("리뷰가 저장되었습니다.");
                close(e);
            } else {
                alert("리뷰 저장에 실패했습니다.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();                     // 콘솔에 상세 오류
            alert("DB 오류: " + ex.getMessage());
        }
    }

    /* ───────── 공용 유틸 ───────── */
    private void close(ActionEvent e) {
        ((Stage)((Node)e.getSource()).getScene().getWindow()).close();
    }
    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

/* ★★★ 통합 시 체크리스트 ★★★
   1. FAKE_REVIEWER_SEQ / FAKE_CAFE_SEQ 사용 부분 제거
      → 로그인/예약 화면에서 실제 ID 넘겨줌 (initData 호출)
*/
