package org.example.stayd.domain.reservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.stayd.common.YesNullableConverter;
import org.example.stayd.domain.reservation.model.DayOfWeek;
import org.example.stayd.domain.reservation.model.Reservation;

public class ReservationDAO {
    // TODO: 특정 스터디카페 전체 좌석 조회
    // TODO: ENTITY & DTO Seat, Cafe

    // 예약 생성
    public long create(Connection conn, Reservation reservation) throws SQLException {
        String sql = """
                INSERT INTO reservation
                (user_id, cafe_id, usage_started_at, usage_ended_at, day_of_week,
                 original_price, discount_price, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, SYSTIMESTAMP)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"reservation_id"})) {
            ps.setLong(1, reservation.getUserId());
            ps.setLong(2, reservation.getCafeId());
            ps.setObject(3, reservation.getUsageStartedAt());
            ps.setObject(4, reservation.getUsageEndedAt());
            ps.setString(5, reservation.getDayOfWeek().name());
            ps.setObject(6, reservation.getOriginalPrice());
            ps.setObject(7, reservation.getDiscountPrice());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);   // PK 값 반환
                }
                throw new SQLException("PK 생성 실패");
            }
        }
    }

    /* ReservationDao.java */
    public Optional<Reservation> findById(Connection conn, long reservationId) throws SQLException {
        String sql = """
                SELECT reservation_id,
                       user_id,
                       cafe_id,
                       usage_started_at,
                       usage_ended_at,
                       day_of_week,
                       original_price,
                       discount_price,
                       created_at,
                       is_canceled,
                       canceled_at,
                       rating,
                       content,
                       review_created_at
                  FROM reservation
                 WHERE reservation_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reservationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                Reservation reservation = Reservation.builder()
                        .reservationId(rs.getLong("reservation_id"))
                        .userId(rs.getLong("user_id"))
                        .cafeId(rs.getLong("cafe_id"))
                        .usageStartedAt(rs.getInt("usage_started_at"))
                        .usageEndedAt(rs.getInt("usage_ended_at"))
                        .dayOfWeek(DayOfWeek.from(rs.getString("day_of_week")))
                        .originalPrice(rs.getInt("original_price"))
                        .discountPrice(rs.getInt("discount_price"))
                        .createdAt(rs.getObject("created_at", Instant.class))
                        .isCanceled(YesNullableConverter.toBoolean(Optional.of(rs.getString("is_canceled"))))
                        .canceledAt(rs.getObject("canceled_at", Instant.class))
                        .rating(rs.getInt("rating"))
                        .content(rs.getString("content"))
                        .reviewCreatedAt(rs.getObject("review_created_at", Instant.class))
                        .build();

                return Optional.of(reservation);
            }
        }
    }

    // TODO: 예약 조회 - 특정 유저 PK 기반
    public List<Reservation> findByUser(Connection conn, long userId) throws SQLException {
        String sql = """
                SELECT reservation_id,
                       user_id,
                       cafe_id,
                       usage_started_at,
                       usage_ended_at,
                       day_of_week,
                       original_price,
                       discount_price,
                       created_at,
                       is_canceled,
                       canceled_at,
                       rating,
                       content,
                       review_created_at
                  FROM reservation
                 WHERE user_id = ?
                 ORDER BY created_at DESC
                """;

        List<Reservation> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = Reservation.builder()
                            .reservationId(rs.getLong("reservation_id"))
                            .userId(rs.getLong("user_id"))
                            .cafeId(rs.getLong("cafe_id"))
                            .usageStartedAt(rs.getInt("usage_started_at"))
                            .usageEndedAt(rs.getInt("usage_ended_at"))
                            .dayOfWeek(DayOfWeek.from(rs.getString("day_of_week")))
                            .originalPrice(rs.getInt("original_price"))
                            .discountPrice(rs.getInt("discount_price"))
                            .createdAt(rs.getObject("created_at", Instant.class))
                            .isCanceled(YesNullableConverter.toBoolean(Optional.of(rs.getString("is_canceled"))))
                            .canceledAt(rs.getObject("canceled_at", Instant.class))
                            .rating(rs.getInt("rating"))
                            .content(rs.getString("content"))
                            .reviewCreatedAt(rs.getObject("review_created_at", Instant.class))
                            .build();
                    list.add(reservation);
                }
            }
        }
        return list;
    }

    // TODO: 예약 취소(수정?)

}
