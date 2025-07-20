// ===========================================
// ReviewController.java  (fixed: userId 하드코딩 복구)
// 리뷰 작성 화면 컨트롤러 - 리뷰 작성 및 유효성 검증 포함
// ===========================================
package org.example.stayd.domain.review.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.example.stayd.domain.reservation.dto.ReservationDto;
import org.example.stayd.domain.reservation.service.ReservationService;
import org.example.stayd.domain.reservation.service.ReservationServiceImpl;
import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.review.dto.ReviewDto;
// import org.example.stayd.global.SessionContext; // TODO: 나중에 주석 해제 - 로그인 정보 연동 시 필요

import java.time.format.DateTimeFormatter;

public class ReviewController {

    // ────── FXML 컴포넌트 바인딩 ──────
    @FXML private Label cafeName, avgScore;
    @FXML private Label useDate, useTime;
    // @FXML private Label seat;  // 좌석 정보 추후 구현 시 사용
    @FXML private ToggleButton star1, star2, star3, star4, star5;
    @FXML private TextArea reviewTextArea;

    private ToggleButton[] stars;

    /** 테스트용 사용자 ID (임시) */
    private int userId = 1; // TODO: 나중에 제거 → 아래 SessionContext 방식으로 교체할 것

    // private int userId = SessionContext.getCurrentUserId(); // TODO: 나중에 주석 해제 → 실제 로그인 사용자 연동

    private ReservationDto resInfo; // 이용 완료된 예약 정보 캐시

    private final ReviewDao reviewDao = new ReviewDaoImpl();
    private final ReservationService resSvc = new ReservationServiceImpl();

    // ─────────────────────────────────────────

    @FXML
    private void initialize() {
        // 별점 버튼 배열 초기화 & 클릭 이벤트 연결
        stars = new ToggleButton[]{star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            final int idx = i;
            stars[i].setOnAction(e -> fillStars(idx));
        }
        // 예약 정보 로드 → 화면 바인딩
        loadReservation();
    }

    /** 최근 완료 예약 1건 로드 & 화면 바인딩 */
    private void loadReservation() {
        resInfo = resSvc.latestFinished(userId);

        if (resInfo == null) {
            alert("이용 완료된 예약이 없습니다. 리뷰를 작성할 수 없습니다.");
            disableForm();
            return;
        }

        cafeName.setText(resInfo.getCafeName());
        avgScore.setText(String.valueOf(resInfo.getAvgScore()));

        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter tFmt = DateTimeFormatter.ofPattern("HH:mm");

        useDate.setText(resInfo.getUsageStart().format(dFmt));
        useTime.setText(String.format("%s - %s",
                resInfo.getUsageStart().format(tFmt),
                resInfo.getUsageEnd().format(tFmt)));
    }

    /** 별점 채우기 */
    private void fillStars(int idx) {
        for (int i = 0; i < stars.length; i++) stars[i].setSelected(i <= idx);
    }

    private int getRating() {
        int r = 0;
        for (ToggleButton b : stars) if (b.isSelected()) r++;
        return r;
    }

    // ────── 버튼 핸들러 ──────
    @FXML private void handleCancel(ActionEvent e) { close(e); }

    @FXML
    private void handleSubmit(ActionEvent e) {
        if (resInfo == null) return; // 안전망

        // 유효성 체크
        if (getRating() == 0) { alert("별점을 선택해 주세요."); return; }
        if (reviewTextArea.getText().isBlank()) { alert("리뷰 내용을 입력해 주세요."); return; }

        // DTO 구성
        ReviewDto dto = new ReviewDto();
        dto.setReviewerId(userId);
        dto.setCafeId(resInfo.getCafeId());
        dto.setRating(getRating());
        dto.setContent(reviewTextArea.getText().trim());

        try {
            if (reviewDao.insert(dto) == 1) {
                reviewDao.commitIfNeeded();
                alert("리뷰가 저장되었습니다.");
                close(e);
            } else alert("리뷰 저장 실패");

        } catch (java.sql.SQLException ex) {
            // UNIQUE 제약조건 위반 (이미 작성한 리뷰)
            if (ex.getErrorCode() == 1) {
                alert("리뷰는 한 카페당 한 번만 작성 가능합니다.");
            } else alert("DB 오류: " + ex.getMessage());
            ex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            alert("알 수 없는 오류: " + ex.getMessage());
        }
    }

    // ────── 공용 유틸 ──────
    private void disableForm() {
        for (ToggleButton b : stars) b.setDisable(true);
        reviewTextArea.setDisable(true);
    }

    private void close(ActionEvent e) {
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

/*
  TODO (통합 시)
  1. userId → SessionContext.getCurrentUserId() 등 실제 로그인 정보 연동
     - 현재 테스트용 userId 하드코딩 사용 중
     - 실제 연동 시:
         - import org.example.stayd.global.SessionContext;
         - private int userId = SessionContext.getCurrentUserId();
         - 하드코딩 라인 제거 필요

  2. 좌석 seatNumber 컬럼이 추가되면 ReservationDto·FXML 바인딩 확장
  3. 리뷰 작성 후 ReviewList 화면 새로고침 이벤트 연결
*/