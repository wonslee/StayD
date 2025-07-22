// ===========================================
// ReservationDao.java
// [임시 작성] 리뷰 작성 기능 테스트용으로 생성한 예약 DAO 인터페이스
// 실제 예약 모듈 담당자와 통합 시, 삭제 또는 수정될 수 있음
// ===========================================
package org.example.stayd.domain.reservation.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.reservation.dto.ReservationDto;
import org.example.stayd.domain.user.dto.UserDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {
    private Connection connection;

    public ReservationDao() {
        this.connection = new DatabaseConnection().getConnection();
    }

    // 로그인한 유저의 cafe_id를 기준으로 예약 현황 조회
    public List<ReservationDto> getReservationStatusByLoggedInUser() throws SQLException {
        List<ReservationDto> reservationList = new ArrayList<>();

        UserDTO loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            System.out.println("Logged-in User ID: " + loggedInUser);
        } else {
            System.out.println("No user is logged in.");
        }

        // 로그인한 유저의 user_id 가져오기
        int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();
        System.out.println("Logged-in User ID: " + userId);

        // 유저의 cafe_id 가져오기
        int cafeId = getCafeIdByUserId(userId);
        System.out.println("Cafe ID for User " + userId + ": " + cafeId);

        // 유효한 cafe_id가 있는 경우, 해당 cafe_id로 예약 현황 조회
        if (cafeId != -1) {
            String query = "SELECT usage_started_at, usage_ended_at FROM reservation WHERE cafe_id = ? AND is_canceled IS NULL";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, cafeId);  // cafe_id로 필터링

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ReservationDto reservation = new ReservationDto();
                        reservation.setUsageStartedAt(rs.getTimestamp("usage_started_at"));
                        reservation.setUsageEndedAt(rs.getTimestamp("usage_ended_at"));
                        reservationList.add(reservation);
                    }
                }
            }
        }
        return reservationList;
    }

    // 유저의 cafe_id를 가져오는 메서드
    private int getCafeIdByUserId(int userId) throws SQLException {
        String query = "SELECT cafe_id FROM cafe WHERE owner_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cafe_id");
                }
            }
        }

        return -1; // cafe_id가 없으면 -1 반환
    }
import org.example.stayd.domain.reservation.dto.ReservationDto;
import java.sql.SQLException;

public interface ReservationDao {
    /**
     * 유저가 이용 완료한 예약 중 가장 최근 1건을 반환 (없으면 null)
     * [리뷰 작성 전 검증용으로 사용]
     */
    ReservationDto latestFinishedForUser(int userId) throws SQLException;
}
