// 작성자 : 이원석
package org.example.stayd.domain.reservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.stayd.common.YesNoBooleanConverter;
import org.example.stayd.domain.reservation.model.Seat;

public class SeatDAO {

    /* SeatDao.java */
    public List<Seat> findByCafeId(Connection conn, long cafeId) throws SQLException {
        System.out.println("cafeId = " + cafeId);
        String selectSeatsSQL = """
                SELECT seat_id,
                       cafe_id,
                       seat_number,
                       is_available,
                       created_at
                  FROM seat
                 WHERE cafe_id = ?
                 ORDER BY seat_number
                """;

        List<Seat> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(selectSeatsSQL)) {
            ps.setLong(1, cafeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Seat seat = Seat.builder()
                            .seatId(rs.getLong("seat_id"))
                            .cafeId(rs.getLong("cafe_id"))
                            .seatNumber(rs.getString("seat_number"))
                            .isAvailable(YesNoBooleanConverter.toBoolean(rs.getString("is_available").charAt(0)))
                            .createdAt(rs.getObject("created_at", LocalDateTime.class))
                            .build();
                    list.add(seat);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 좌석 잠금 + 가용 여부 확인
     */
    public boolean lockAndCheckAvailable(Connection conn, long seatId) throws SQLException {
        String sql = "SELECT is_available FROM seat WHERE seat_id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, seatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("좌석 없음");
                }
                return rs.getString(1).charAt(0) == 'Y';
            }
        }
    }

    /**
     * is_available = 'Y'/'N' 업데이트, 행 수 1이면 성공
     */
    public boolean updateAvailability(Connection conn, long seatId, boolean available) throws SQLException {
        String sql = "UPDATE seat SET is_available = ? WHERE seat_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(YesNoBooleanConverter.toChar(available)));
            ps.setLong(2, seatId);
            return ps.executeUpdate() == 1;
        }
    }
}
