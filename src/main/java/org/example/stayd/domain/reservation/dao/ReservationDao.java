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

    // 요일별 예약 데이터를 가져오는 메서드
    public List<ReservationDTO> getReservationStatusByDay(String dayOfWeek) throws SQLException {
        List<ReservationDTO> reservationList = new ArrayList<>();
        UserDTO loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            System.out.println("Logged-in User ID: " + loggedInUser);
        } else {
            System.out.println("No user is logged in.");
        }

        // 로그인한 유저의 user_id 가져오기
        int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();

        // 유저의 cafe_id 가져오기
        int cafeId = getCafeIdByUserId(userId);

        // 유효한 cafe_id가 있는 경우, 해당 cafe_id와 선택된 요일로 예약 현황 조회
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

    // 날짜에 해당하는 예약 현황 조회
    public List<ReservationDTO> getReservationStatusByLoggedInUser(Date selectedDate) throws SQLException {
        List<ReservationDTO> reservationList = new ArrayList<>();

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

        // 유효한 cafe_id가 있는 경우, 해당 cafe_id와 선택된 날짜로 예약 현황 조회
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

    // 숫자를 시간으로 변환하여 Timestamp 객체로 변환하는 메서드
    private Timestamp convertToTimestamp(int time) {
        // 예를 들어 time=9이면 09:00:00, time=16이면 16:00:00으로 변환
        String timeString = String.format("%02d:00:00", time); // "09:00:00" 형식으로 변환
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            java.util.Date parsedDate = format.parse(timeString);
            return new Timestamp(parsedDate.getTime()); // Timestamp 객체로 변환
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
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
}