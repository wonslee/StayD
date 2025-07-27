package org.example.stayd.domain.review.dao;


import org.example.stayd.domain.review.dto.ReviewListDTO;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * 리뷰 관련 DB 작업을 처리하는 DAO 클래스
 * - 리뷰 저장, 수정, 삭제
 * - 예약 완료 여부 및 중복 리뷰 체크
 */
public class ReviewDAO {
    /**
     * 예약에 리뷰를 저장
     */
    public boolean saveReview(Connection conn, long reservationId, int rating, String content) throws SQLException {
        String sql = "UPDATE reservation SET rating = ?, content = ?, review_created_at = SYSTIMESTAMP WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, content);
            ps.setLong(3, reservationId);
            return ps.executeUpdate() > 0;

        }
    }
    /**
     * 예약에 등록된 리뷰를 수정
     */
    public boolean updateReview(Connection conn, long reservationId, int rating, String content) throws SQLException {
        String sql = "UPDATE reservation SET rating = ?, content = ?, review_created_at = SYSTIMESTAMP WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, content);
            ps.setLong(3, reservationId);
            return ps.executeUpdate() > 0;
        }
    }
    /**
     * 예약에 등록된 리뷰를 삭제
     */
    public boolean deleteReview(Connection conn, long reservationId) throws SQLException {
        String sql = "UPDATE reservation SET rating = NULL, content = NULL, review_created_at = NULL WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reservationId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 해당 예약에 이미 리뷰가 등록되어 있는지 확인
     */

    public boolean existsReviewByReservationId(Connection conn, long reservationId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservation WHERE reservation_id = ? AND content IS NOT NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    /**
     * 해당 예약이 이용 완료 상태인지 확인
     */
    public boolean isReservationFinished(Connection conn, long reservationId) throws SQLException {
        String sql = "SELECT usage_ended_at, reservation_date FROM reservation WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int endHour = rs.getInt("usage_ended_at");
                    LocalDate date = rs.getDate("reservation_date").toLocalDate();
                    LocalDateTime endTime = date.atTime(endHour, 0);
                    return LocalDateTime.now().isAfter(endTime);
                }
            }
        }
        return false;
    }
    public List<ReviewListDTO> findReviewsByCafeId(Connection conn, long cafeId) throws SQLException {
        String sql = """
        SELECT r.user_id, u.login_id, r.rating, r.content, r.review_created_at
        FROM reservation r
        JOIN users u ON r.user_id = u.user_id
        WHERE r.cafe_id = ?
          AND r.rating IS NOT NULL
          AND r.content IS NOT NULL
        ORDER BY r.review_created_at DESC
    """;

        List<ReviewListDTO> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cafeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReviewListDTO dto = new ReviewListDTO();
                    dto.setUserId(rs.getLong("user_id"));
                    dto.setRating(rs.getInt("rating"));
                    dto.setContent(rs.getString("content"));
                    dto.setReviewCreatedAt(rs.getTimestamp("review_created_at").toLocalDateTime());
                    dto.setLoginId(rs.getString("login_id"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
    public double findAverageRatingByCafeId(Connection conn, long cafeId) throws SQLException {
        String sql = """
        SELECT AVG(rating) AS avg_rating
        FROM reservation
        WHERE cafe_id = ?
          AND rating IS NOT NULL
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cafeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        }

        return 0.0;
    }

}
