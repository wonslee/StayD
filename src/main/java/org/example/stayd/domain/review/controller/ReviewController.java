// ===========================================
// ReviewController.java
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

import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class ReviewController {

    // ────── FXML 컴포넌트 바인딩 ──────
    @FXML private Label cafeName, avgScore;
    @FXML private Label useDate, useTime;
    // @FXML private Label seat;  // 좌석 정보 추후 구현 시 사용
    @FXML private ToggleButton star1, star2, star3, star4, star5;
    @FXML private TextArea reviewTextArea;

    private ToggleButton[] stars;
    private int userId = 30; // 로그인 연결 전, 임시 하드코딩 테스트용 ID
    private ReservationDto resInfo; // 이용 완료된 예약 정보 캐시

    private final ReviewDao reviewDao = new ReviewDaoImpl();
    private final ReservationService resSvc = new ReservationServiceImpl();

    private static final AtomicInteger FAKE_REVIEWER_SEQ = new AtomicInteger(20); // 통합 전 더미 시퀀스

    @FXML
    private void initialize() {
        // 별점 클릭 로직 구성
        stars = new ToggleButton[]{star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            final int idx = i;
            stars[i].setOnAction(e -> fillStars(idx));
        }
        // 예약 정보 로드하여 UI에 바인딩
        loadReservation();
    }

    private void loadReservation() {
        resInfo = resSvc.latestFinished(userId);

        if (resInfo == null) {
            alert("이용 완료된 예약이 없습니다. 리뷰를 작성할 수 없습니다.");
            disableForm();
            return;
        }

        // UI에 정보 바인딩
        cafeName.setText(resInfo.getCafeName());
        avgScore.setText(String.valueOf(resInfo.getAvgScore()));

        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter tFmt = DateTimeFormatter.ofPattern("HH:mm");

        useDate.setText(resInfo.getUsageStart().format(dFmt));
        useTime.setText(String.format("%s - %s",
                resInfo.getUsageStart().format(tFmt),
                resInfo.getUsageEnd().format(tFmt)));
    }

    private void fillStars(int idx) {
        for (int i = 0; i <= 4; i++) {
            stars[i].setSelected(i <= idx);
        }
    }

    private int getRating() {
        int r = 0;
        for (ToggleButton b : stars) if (b.isSelected()) r++;
        return r;
    }

    @FXML
    private void handleCancel(ActionEvent e) {
        close(e);
    }

    @FXML
    private void handleSubmit(ActionEvent e) {
        if (resInfo == null) return;

        // 유효성 체크
        if (getRating() == 0) {
            alert("별점을 선택해 주세요.");
            return;
        }
        if (reviewTextArea.getText().isBlank()) {
            alert("리뷰 내용을 입력해 주세요.");
            return;
        }

        // DTO 구성
        ReviewDto dto = new ReviewDto();
        dto.setReviewerId(userId == 0
                ? FAKE_REVIEWER_SEQ.getAndIncrement()
                : userId);
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
            if (ex.getErrorCode() == 1) { // UNIQUE 제약조건 위반
                alert("리뷰는 한 카페당 한 번만 작성 가능합니다.");
            } else {
                alert("DB 오류: " + ex.getMessage());
            }
            ex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            alert("알 수 없는 오류: " + ex.getMessage());
        }
    }

    // UI 비활성화
    private void disableForm() {
        for (ToggleButton b : stars) b.setDisable(true);
        reviewTextArea.setDisable(true);
    }

    // 창 닫기
    private void close(ActionEvent e) {
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    // 알림창 유틸
    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

/*
  통합 시 TODO 목록:
  1. userId 실제 로그인 정보와 연동 (현재는 임시 1번)
  2. 좌석 정보 seatNumber가 ReservationDto 및 DB에 추가되면 바인딩 및 출력 처리
  3. 리뷰 작성 후 리뷰 목록 갱신 or 창 닫기 후 새로고침 필요 시 이벤트 처리 연동
  4. 작성자명(loginId) → 로그인 정보에서 받아서 넣기
*/
// 리뷰 등록 유효성 검증 로직
// 1. 필수 입력(별점, 내용) 체크 → ReviewController.handleSubmit()
// 2. 이용 이력 존재 여부 → ReviewController.loadReservation()
// 3. 중복 작성 방지 → ReviewDaoImpl.insert()의 SQLException 처리
