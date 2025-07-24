package org.example.stayd.domain.review.dao;

import org.example.stayd.common.YesNullableConverter;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReviewDAO {
//리뷰 등록
    public boolean saveReview(Connection conn, long reservationId, int rating, String content) throws SQLException {
        String sql = "UPDATE reservation SET rating = ?, content = ?, review_created_at = SYSTIMESTAMP WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, content);
            ps.setLong(3, reservationId);
            return ps.executeUpdate() > 0;

        }
    }
//리뷰 수정
    public boolean updateReview(Connection conn, long reservationId, int rating, String content) throws SQLException {
        String sql = "UPDATE reservation SET rating = ?, content = ?, review_created_at = SYSTIMESTAMP WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, content);
            ps.setLong(3, reservationId);
            return ps.executeUpdate() > 0;
        }
    }
//리뷰 삭제
    public boolean deleteReview(Connection conn, long reservationId) throws SQLException {
        String sql = "UPDATE reservation SET rating = NULL, content = NULL, review_created_at = NULL WHERE reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reservationId);
            return ps.executeUpdate() > 0;
        }
    }
// 해당 예약에 리뷰가 있는지
    public boolean existsReviewByReservationId(Connection conn, long reservationId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservation WHERE reservation_id = ? AND content IS NOT NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
// 이용시간이 지났는지
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
}
