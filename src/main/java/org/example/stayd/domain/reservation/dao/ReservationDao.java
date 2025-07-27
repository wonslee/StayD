package org.example.stayd.domain.reservation.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.user.dto.UserDTO;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {
    private Connection connection;

    public ReservationDao() {
        this.connection = new DatabaseConnection().getConnection();
    }

    /**
     * 요일별 예약 현황을 가져오는 메서드
     *
     * @param dayOfWeek 요일 (MON, TUE, ... 등)
     * @return 예약 현황 목록
     * @throws SQLException 데이터베이스 접근 중 발생할 수 있는 예외
     */
    public List<ReservationDTO> getReservationStatusByDay(String dayOfWeek) throws SQLException {
        List<ReservationDTO> reservationList = new ArrayList<>();

        // 로그인한 유저의 user_id 가져오기
        int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();

        // 유저의 cafe_id 가져오기
        int cafeId = getCafeIdByUserId(userId);

        if (cafeId != -1) {
            String query = "SELECT usage_started_at, usage_ended_at, day_of_week FROM reservation WHERE cafe_id = ? AND day_of_week = ? AND is_canceled IS NULL";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, cafeId);  // cafe_id로 필터링
                stmt.setString(2, dayOfWeek);  // 요일 값으로 필터링

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ReservationDTO reservation = ReservationDTO.builder()
                                .usageStartedAt(rs.getInt("usage_started_at"))
                                .usageEndedAt(rs.getInt("usage_ended_at"))
                                .build();
                        reservationList.add(reservation);
                    }
                }
            }
        }
        return reservationList;
    }

    /**
     * 날짜에 해당하는 예약 현황을 가져오는 메서드
     *
     * @param selectedDate 선택된 날짜
     * @return 예약 현황 목록
     * @throws SQLException 데이터베이스 접근 중 발생할 수 있는 예외
     */
    public List<ReservationDTO> getReservationStatusByLoggedInUser(Date selectedDate) throws SQLException {
        List<ReservationDTO> reservationList = new ArrayList<>();

        // 로그인한 유저의 user_id 가져오기
        int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();

        // 유저의 cafe_id 가져오기
        int cafeId = getCafeIdByUserId(userId);

        if (cafeId != -1) {
            String query = "SELECT usage_started_at, usage_ended_at, RESERVATION_DATE FROM reservation WHERE cafe_id = ? AND RESERVATION_DATE = ? AND is_canceled IS NULL";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, cafeId);  // cafe_id로 필터링
                stmt.setDate(2, selectedDate);  // 선택된 날짜로 필터링

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ReservationDTO reservation = ReservationDTO.builder()
                                .usageStartedAt(rs.getInt("usage_started_at"))
                                .usageEndedAt(rs.getInt("usage_ended_at"))
                                .build();
                        reservationList.add(reservation);
                    }
                }
            }
        }
        return reservationList;
    }

    /**
     * 예약 ID로 예약을 삭제하는 메서드
     */
    public void deleteById(long reservationId) throws SQLException {
        String sql = "DELETE FROM reservation WHERE reservation_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reservationId);
            stmt.executeUpdate();
        }
    }

    /**
     * 로그인된 유저의 cafe_id를 가져오는 메서드
     *
     * @param userId 로그인된 유저의 ID
     * @return 해당 유저의 cafe_id
     * @throws SQLException 데이터베이스 접근 중 발생할 수 있는 예외
     */
    private int getCafeIdByUserId(int userId) throws SQLException {
        String query = "SELECT cafe_id FROM cafe WHERE owner_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cafe_id");  // cafe_id 반환
                }
            }
        }
        return -1; // 카페 ID를 찾을 수 없으면 -1 반환
    }
}