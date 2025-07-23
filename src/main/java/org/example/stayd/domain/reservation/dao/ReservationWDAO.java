package org.example.stayd.domain.reservation.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.YesNullableConverter;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.model.DayOfWeek;
import org.example.stayd.domain.reservation.model.Reservation;



public class ReservationWDAO {
    // TODO: 특정 스터디카페 전체 좌석 조회
    // TODO: ENTITY & DTO Seat, Cafe

    // 예약 생성
    public long create(Connection conn, Reservation reservation) throws SQLException {

        String sql = """
                INSERT INTO reservation(
                        USER_ID, 
                        CAFE_ID, 
                        RESERVATION_DATE, 
                        USAGE_STARTED_AT, 
                        USAGE_ENDED_AT, 
                        DAY_OF_WEEK,
                        ORIGINAL_PRICE, 
                        DISCOUNT_PRICE
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        System.out.println("======reservation.getReservationDate() = " + reservation.getReservationDate());
        try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"reservation_id"})) {
            ps.setLong(1, reservation.getUserId());
            ps.setLong(2, reservation.getCafeId());
            ps.setDate(3, Date.valueOf(reservation.getReservationDate()));
            ps.setObject(4, reservation.getUsageStartedAt());
            ps.setObject(5, reservation.getUsageEndedAt());
            ps.setString(6, reservation.getDayOfWeek().name());
            ps.setObject(7, reservation.getOriginalPrice());
            ps.setObject(8, reservation.getDiscountPrice());
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
                       reservation_date,
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


                long id = rs.getLong("reservation_id");
                long userId = rs.getLong("user_id");
                long cafeId = rs.getLong("cafe_id");
                LocalDate reservationDate = rs.getDate("reservation_date").toLocalDate();
                int usageStartedAt = rs.getInt("usage_started_at");
                int usageEndedAt = rs.getInt("usage_ended_at");
                DayOfWeek dayOfWeek = DayOfWeek.from(rs.getString("day_of_week"));
                int originalPrice = rs.getInt("original_price");
                int discountPrice = rs.getInt("discount_price");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                boolean isCanceled = YesNullableConverter.toBoolean(rs.getString("is_canceled"));
                LocalDateTime canceledAt = rs.getTimestamp("canceled_at") == null ? null
                        : rs.getTimestamp("canceled_at").toLocalDateTime();
                int rating = rs.getInt("rating");
                String content = rs.getString("content");
                LocalDateTime reviewCreatedAt = rs.getTimestamp("review_created_at") == null ? null
                        : rs.getTimestamp("review_created_at").toLocalDateTime();

                Reservation reservation = Reservation.builder()
                        .reservationId(id)
                        .userId(userId)
                        .cafeId(cafeId)
                        .reservationDate(reservationDate)
                        .usageStartedAt(usageStartedAt)
                        .usageEndedAt(usageEndedAt)
                        .dayOfWeek(dayOfWeek)
                        .originalPrice(originalPrice)
                        .discountPrice(discountPrice)
                        .createdAt(createdAt)
                        .isCanceled(isCanceled)
                        .canceledAt(canceledAt)
                        .rating(rating)
                        .content(content)
                        .reviewCreatedAt(reviewCreatedAt)
                        .build();

                System.out.println("reservation = " + reservation);
                System.out.println("reservation.getReservationId() = " + reservation.getReservationId());
                System.out.println("reservation.getUserId() = " + reservation.getUserId());
                System.out.println("===================");

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
                            .reservationId(rs.getLong("RESERVATION_ID"))
                            .userId(rs.getLong("USER_ID"))
                            .cafeId(rs.getLong("CAFE_ID"))
                            .reservationDate(rs.getDate("RESERVATION_DATE").toLocalDate())
                            .usageStartedAt(rs.getInt("USAGE_STARTED_AT"))
                            .usageEndedAt(rs.getInt("USAGE_ENDED_AT"))
                            .dayOfWeek(DayOfWeek.from(rs.getString("DAY_OF_WEEK")))
                            .originalPrice(rs.getInt("ORIGINAL_PRICE"))
                            .discountPrice(rs.getInt("DISCOUNT_PRICE"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .isCanceled(YesNullableConverter.toBoolean(rs.getString("IS_CANCELED")))
                            .canceledAt(rs.getTimestamp("CANCELED_AT").toLocalDateTime())
                            .rating(rs.getInt("RATING"))
                            .content(rs.getString("CONTENT"))
                            .reviewCreatedAt(rs.getTimestamp("REVIEW_CREATED_AT").toLocalDateTime())
                            .build();
                    list.add(reservation);

                }
            }
        }
        return list;
    }
    // 리뷰 작성 가능한 예약 목록 조회 (리뷰 작성 안 했고, 취소되지 않은 예약)
    public List<ReservationDTO> findWritableReservationsByUserId(int userId) {
        List<ReservationDTO> list = new ArrayList<>();
        String sql = """
                SELECT reservation_id,
                       user_id,
                       cafe_id,
                       reservation_date,
                       usage_started_at,
                       usage_ended_at
                  FROM reservation
                 WHERE user_id = ?
                   AND is_canceled = 'N'
                   AND review_created_at IS NULL
                ORDER BY reservation_date DESC
                """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))  {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReservationDTO dto = ReservationDTO.builder()
                        .reservationId(rs.getLong("reservation_id"))
                        .userId(rs.getLong("user_id"))
                        .cafeId(rs.getLong("cafe_id"))
                        .reservationDate(rs.getDate("reservation_date").toLocalDate())
                        .usageStartedAt(rs.getInt("usage_started_at"))
                        .usageEndedAt(rs.getInt("usage_ended_at"))
                        .build();

                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ✅ 특정 유저의 예약 리스트를 ReservationDTO로 반환하는 메서드
    public List<ReservationDTO> findByUserId(int userId) throws SQLException {
        List<ReservationDTO> list = new ArrayList<>();

        String sql = """
        SELECT r.reservation_id,
               r.cafe_id,
               c.name AS cafe_name,
               r.reservation_date,
               r.usage_started_at,
               r.usage_ended_at,
               r.original_price,
               r.discount_price,
               r.review_created_at,
               r.rating,
               r.content
        FROM reservation r
        JOIN cafe c ON r.cafe_id = c.cafe_id
        WHERE r.user_id = ?
        ORDER BY r.reservation_date DESC
        """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservationDTO dto = ReservationDTO.builder()
                            .reservationId(rs.getLong("reservation_id"))
                            .cafeId(rs.getLong("cafe_id"))
                            .cafeName(rs.getString("cafe_name"))
                            .reservationDate(rs.getDate("reservation_date").toLocalDate())
                            .usageStartedAt(rs.getInt("usage_started_at"))
                            .usageEndedAt(rs.getInt("usage_ended_at"))
                            .originalPrice(rs.getInt("original_price"))
                            .discountPrice(rs.getInt("discount_price"))
                            .reviewCreatedAt(rs.getTimestamp("REVIEW_CREATED_AT").toLocalDateTime())
                            .rating(rs.getInt("rating"))
                            .content(rs.getString("content"))
                            .build();

                    list.add(dto);
                }
            }
        }

        return list;
    }

    // TODO: 예약 취소(수정?)

}