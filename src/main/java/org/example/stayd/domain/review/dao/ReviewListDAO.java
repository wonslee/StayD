// 작성자 : 이해든
package org.example.stayd.domain.review.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.example.stayd.domain.review.dto.ReviewDTO;
import org.example.stayd.domain.review.dto.ReviewListDTO;

public class ReviewListDAO {

    /**
     * 특정 유저가 작성한 리뷰 목록 조회 (카페 이름 포함)
     */
    public List<ReviewListDTO> findReviewsByCafeId(Connection conn, long cafeId) throws SQLException {
        String sql = """
                    SELECT r.reservation_id,
                           r.cafe_id,
                           r.user_id,
                           u.login_id,
                           r.rating,
                           r.content,
                           r.review_created_at,
                           c.name AS cafe_name
                      FROM reservation r
                      JOIN cafe c ON r.cafe_id = c.cafe_id
                      JOIN users u ON r.user_id = u.user_id
                     WHERE r.cafe_id = ?  -- ✅ 수정됨!
                       AND r.rating IS NOT NULL
                       AND r.content IS NOT NULL
                       AND r.review_created_at IS NOT NULL
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
                    dto.setCreatedAt(rs.getTimestamp("review_created_at").toLocalDateTime());
                    dto.setLoginId(rs.getString("login_id"));  // 작성자 ID
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /**
     * 특정 스터디카페의 평균 평점 계산
     */
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

        return 0.0; // 리뷰가 없는 경우 기본값
    }

    public List<ReviewDTO> findByUserId(Connection conn, long userId) throws SQLException {
        String sql = """
                    SELECT r.reservation_id,
                           r.cafe_id,
                           r.user_id,
                           u.login_id,
                           r.rating,
                           r.content,
                           r.review_created_at,
                           c.name AS cafe_name
                      FROM reservation r
                      JOIN cafe c ON r.cafe_id = c.cafe_id
                      JOIN users u ON r.user_id = u.user_id
                     WHERE r.user_id = ?
                       AND r.rating IS NOT NULL
                       AND r.content IS NOT NULL
                       AND r.review_created_at IS NOT NULL
                     ORDER BY r.review_created_at DESC
                """;

        List<ReviewDTO> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setReservationId(rs.getLong("reservation_id"));
                    dto.setCafeId(rs.getLong("cafe_id"));
                    dto.setUserId(rs.getLong("user_id"));
                    dto.setLoginId(rs.getString("login_id"));
                    dto.setRating(rs.getInt("rating"));
                    dto.setContent(rs.getString("content"));
                    dto.setReviewCreatedAt(rs.getTimestamp("review_created_at").toLocalDateTime());
                    dto.setCafeName(rs.getString("cafe_name"));

                    list.add(dto);
                }
            }
        }

        return list;
    }

}