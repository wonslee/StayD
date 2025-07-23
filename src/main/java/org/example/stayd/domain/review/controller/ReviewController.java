// ===========================================
// ReviewController.java  (fixed: userId 하드코딩 복구 + 예약당 리뷰 1개 제한 로직 추가)
// 리뷰 작성 화면 컨트롤러 - 리뷰 작성 및 유효성 검증 포함
// ===========================================
package org.example.stayd.domain.review.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.common.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class ReviewController {

    // FXML 컴포넌트: 별점 버튼 5개
    @FXML private ToggleButton star1;
    @FXML private ToggleButton star2;
    @FXML private ToggleButton star3;
    @FXML private ToggleButton star4;
    @FXML private ToggleButton star5;

    // FXML 컴포넌트: 예약 정보 및 리뷰 입력란
    @FXML private Text useDate;
    @FXML private Text useTime;
    @FXML private TextArea reviewTextArea;
    @FXML private Label cafeName, avgScore;  // 선택적으로 화면에 표시할 수 있는 카페 정보

    // 별점 버튼 배열로 저장
    private ToggleButton[] stars;

    // 로그인 사용자 ID
    private int userId;

    // 리뷰 작성 대상 예약 정보
    private ReservationDTO resInfo;

    // 리뷰 DAO
    private final ReviewDao reviewDao = new ReviewDaoImpl();

    // 예약 DAO (팀원이 만든 ReservationWDAO 사용)
    private final ReservationWDAO reservationDao = new ReservationWDAO();

    // 초기화 메서드: 로그인 정보 불러오고, 별점 버튼 셋업, 예약 정보 불러오기
    @FXML
    private void initialize() {
        // 현재 로그인한 사용자 ID 가져오기
        userId = SessionManager.getInstance().getLoggedInUser().getUser_id();

        // 별점 버튼 클릭 이벤트 설정
        stars = new ToggleButton[]{star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            final int idx = i;
            stars[i].setOnAction(e -> fillStars(idx));  // 해당 인덱스까지 별 채우기
        }

        // 예약 정보 불러오기
        loadReservation();
    }

    // 사용자가 작성 가능한 예약 1건 불러오기 (리뷰 미작성인 예약)
    private void loadReservation() {
        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();
            List<ReservationDTO> list = reservationDao.findWritableReservationsByUserId(userId);


            // 리뷰가 아직 작성되지 않은 예약 1건 찾기
            for (ReservationDTO dto : list) {
                if (dto.getRating() == null && dto.getUserId() == userId) {
                    resInfo = dto;
                    break;
                }
            }

            // 없으면 안내 메시지
            if (resInfo == null) {
                alert("작성 가능한 리뷰가 없습니다.");
            } else {
                // 화면에 예약 정보 표시
                useDate.setText(resInfo.getReservationDate().toString());
                useTime.setText(resInfo.getUsageStartedAt() + " ~ " + resInfo.getUsageEndedAt());
                if (cafeName != null) cafeName.setText(resInfo.getCafeName());
                if (avgScore != null) avgScore.setText(resInfo.getRating() != null ? String.valueOf(resInfo.getRating()) : "-");
            }
        } catch (Exception e) {
            e.printStackTrace();
            alert("예약 정보 불러오기 오류");
        }
    }

    // 선택한 별점 채우기 (0~4까지 선택하면 해당 인덱스까지 활성화)
    private void fillStars(int index) {
        for (int i = 0; i < stars.length; i++) {
            stars[i].setSelected(i <= index);
        }
    }

    // 현재 선택된 별점 계산
    private int getRating() {
        int rating = 0;
        for (ToggleButton star : stars) {
            if (star.isSelected()) rating++;
        }
        return rating;
    }

    // 리뷰 저장 버튼 클릭 시 호출
    @FXML
    private void handleSubmit(ActionEvent e) {
        if (resInfo == null) {
            alert("작성 가능한 예약이 없습니다.");
            return;
        }

        // 유효성 체크
        if (getRating() == 0) {
            alert("별점을 선택해 주세요.");
            return;
        }
        if (reviewTextArea.getText().isBlank()) {
            alert("리뷰 내용을 입력해 주세요.");
            return;
        }

        // 예약당 리뷰 1개 제한 체크
        try {
            if (((ReviewDaoImpl) reviewDao).existsByReservation(resInfo.getReservationId().intValue(), userId)) {
                alert("이 예약에는 이미 리뷰를 작성하셨습니다.");
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("리뷰 중복 확인 중 오류 발생: " + ex.getMessage());
            return;
        }

        // 리뷰 DTO 생성 및 정보 세팅
        ReviewDto dto = new ReviewDto();
        dto.setReviewerId(userId);
        dto.setCafeId(resInfo.getCafeId().intValue());
        dto.setReservationId(resInfo.getReservationId().intValue());
        dto.setRating(getRating());
        dto.setContent(reviewTextArea.getText().trim());

        try {
            int result = reviewDao.insert(dto);
            if (result == 1) {
                reviewDao.commitIfNeeded();
                alert("리뷰가 저장되었습니다.");
                close(e);  // 창 닫기
            } else {
                alert("리뷰 저장 실패");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1) {
                alert("리뷰는 한 카페당 한 번만 작성 가능합니다.");
            } else {
                ex.printStackTrace();
                alert("리뷰 저장 중 오류: " + ex.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("리뷰 저장 중 오류: " + ex.getMessage());
        }
    }

    // 취소 버튼 클릭 시 창 닫기
    @FXML
    private void handleCancel(ActionEvent e) {
        close(e);
    }

    // 알림창 출력
    private void alert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // 현재 창 닫기
    private void close(ActionEvent e) {
        ((Button) e.getSource()).getScene().getWindow().hide();
    }
}
