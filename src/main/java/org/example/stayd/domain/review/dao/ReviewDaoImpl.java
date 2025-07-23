package org.example.stayd.domain.review.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.review.dto.ReviewDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reservation 테이블 내 리뷰 컬럼 관련 DAO 구현체
 * - 별도 review 테이블 없이 reservation 테이블에서 관리
 */
public class ReviewDaoImpl implements ReviewDao {

    private final Connection conn;

    public ReviewDaoImpl() {
        this.conn = new DatabaseConnection().getConnection();
    }

    @Override
    public int insert(ReviewDto review) throws SQLException {
        String sql = """
            UPDATE reservation
               SET rating = ?,
                   content = ?,
                   review_created_at = CURRENT_TIMESTAMP
             WHERE reservation_id = ?
               AND user_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getContent());
            ps.setInt(3, review.getReservationId());
            ps.setInt(4, review.getReviewerId());
            return ps.executeUpdate();
        }
    }

    @Override
    public int update(ReviewDto review) throws SQLException {
        String sql = """
            UPDATE reservation
               SET rating = ?,
                   content = ?,
                   review_created_at = CURRENT_TIMESTAMP
             WHERE reservation_id = ?
               AND user_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getContent());
            ps.setInt(3, review.getReservationId());
            ps.setInt(4, review.getReviewerId());
            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(int reservationId, int userId) throws SQLException {
        String sql = """
            UPDATE reservation
               SET rating = NULL,
                   content = NULL,
                   review_created_at = NULL
             WHERE reservation_id = ?
               AND user_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.setInt(2, userId);
            return ps.executeUpdate();
        }
    }

    @Override
    public List<ReviewDto> findByCafe(int cafeId) throws SQLException {
        List<ReviewDto> list = new ArrayList<>();
        String sql = """
            SELECT reservation_id, user_id, cafe_id, rating, content, review_created_at
              FROM reservation
             WHERE cafe_id = ?
               AND review_created_at IS NOT NULL
             ORDER BY review_created_at DESC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cafeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReviewDto dto = ReviewDto.builder()
                        .reservationId(rs.getInt("reservation_id"))
                        .reviewerId(rs.getInt("user_id"))
                        .cafeId(rs.getInt("cafe_id"))
                        .rating(rs.getInt("rating"))
                        .content(rs.getString("content"))
                        .createdAt(rs.getTimestamp("review_created_at").toLocalDateTime())
                        .build();
                list.add(dto);
            }
        }

        return list;
    }

    @Override
    public boolean existsByReservation(int reservationId, int userId) throws SQLException {
        String sql = """
            SELECT COUNT(*)
              FROM reservation
             WHERE reservation_id = ?
               AND user_id = ?
               AND review_created_at IS NOT NULL
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public void commitIfNeeded() throws SQLException {
        if (conn != null && !conn.getAutoCommit()) {
            conn.commit();
        }
    }

    /** ✅ 유저가 작성한 모든 리뷰 조회 */
    @Override
    public List<ReviewDto> findAllByUserId(int userId) throws SQLException {
        List<ReviewDto> list = new ArrayList<>();
        String sql = """
            SELECT reservation_id, user_id, cafe_id, rating, content, review_created_at
              FROM reservation
             WHERE user_id = ?
               AND review_created_at IS NOT NULL
             ORDER BY review_created_at DESC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReviewDto dto = ReviewDto.builder()
                        .reservationId(rs.getInt("reservation_id"))
                        .reviewerId(rs.getInt("user_id"))
                        .cafeId(rs.getInt("cafe_id"))
                        .rating(rs.getInt("rating"))
                        .content(rs.getString("content"))
                        .createdAt(rs.getTimestamp("review_created_at").toLocalDateTime())
                        .build();
                list.add(dto);
            }
        }

        return list;
    }
}
