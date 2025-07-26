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

/**
 * 예약 관련 데이터베이스 접근을 담당하는 DAO 클래스입니다.
 * <p>
 * 예약 생성, 조회, 취소 등 예약 관련 모든 DB 작업을 수행합니다.
 * Oracle PL/SQL 프로시저 및 JDBC를 활용합니다.
 * </p>
 */
public class ReservationDao {
    private Connection connection;

    public ReservationDao() {
        this.connection = new DatabaseConnection().getConnection();
    }

    /**
     * 지정한 요일의 예약 현황을 조회합니다.
     *
     * @param dayOfWeek 요일 (예: "MON", "TUE")
     * @return 해당 요일의 예약 목록
     * @throws SQLException 데이터베이스 접근 중 오류 발생 시
     */
    public List<ReservationDTO> getReservationStatusByDay(String dayOfWeek) throws SQLException {
        List<ReservationDTO> reservationList = new ArrayList<>();

        // 로그인한 유저의 user_id 가져오기
        int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();

        // 유저의 cafe_id 가져오기
        int cafeId = getCafeIdByUserId(userId);

        if (cafeId != -1) {
            // PL/SQL 프로시저 호출
            String query = "{call get_reservation_status_by_day(?, ?, ?)}";  // 프로시저 호출

            try (CallableStatement stmt = connection.prepareCall(query)) {
                stmt.setInt(1, cafeId);  // cafe_id
                stmt.setString(2, dayOfWeek);  // day_of_week
                stmt.registerOutParameter(3, Types.REF_CURSOR);  // 출력 커서

                stmt.execute();

                // 출력 커서를 통해 결과 처리
                try (ResultSet rs = (ResultSet) stmt.getObject(3)) {
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
            // PL/SQL 프로시저 호출
            String query = "{call get_reservation_status_by_date(?, ?, ?)}";  // 프로시저 호출

            try (CallableStatement stmt = connection.prepareCall(query)) {
                stmt.setInt(1, cafeId);  // cafe_id
                stmt.setDate(2, selectedDate);  // selected_date
                stmt.registerOutParameter(3, Types.REF_CURSOR);  // 출력 커서

                stmt.execute();

                // 출력 커서를 통해 결과 처리
                try (ResultSet rs = (ResultSet) stmt.getObject(3)) {
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
        String query = "{call get_cafe_id_by_user_id(?, ?)}";  // PL/SQL 프로시저 호출

        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setInt(1, userId);  // user_id
            stmt.registerOutParameter(2, Types.INTEGER);  // 출력 값은 cafe_id

            stmt.execute();

            // cafe_id 반환
            return stmt.getInt(2);
        }
    }
}