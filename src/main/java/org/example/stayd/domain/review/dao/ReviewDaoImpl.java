// ===========================================
// ReviewDaoImpl.java - 예약당 리뷰 1건만 작성 가능하도록 수정
// ===========================================
package org.example.stayd.domain.review.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.review.dto.ReviewDto;
// import org.example.stayd.global.SessionContext; // TODO: 통합 시 세션에서 userId 가져오기

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDaoImpl implements ReviewDao {

    private final DatabaseConnection db = new DatabaseConnection();
    private Connection conn; // 수동 커밋용

    // 리뷰 등록
    @Override
    public int insert(ReviewDto r) throws SQLException {
        String sql = """
            UPDATE reservation
            SET review_rating = ?, review_content = ?, review_created_at = SYSTIMESTAMP
            WHERE reservation_id = ? AND user_id = ?
        """;

        conn = db.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getRating());
            ps.setString(2, r.getContent());
            ps.setInt(3, r.getReservationId());
            ps.setInt(4, r.getReviewerId()); // TODO: 세션 기반으로 교체
            return ps.executeUpdate();
        }
    }

    // 리뷰 수정
    @Override
    public int update(ReviewDto r) throws SQLException {
        String sql = """
            UPDATE reservation
            SET review_rating = ?, review_content = ?
            WHERE reservation_id = ? AND user_id = ?
        """;

        conn = db.getConnection();
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getRating());
            ps.setString(2, r.getContent());
            ps.setInt(3, r.getReservationId());
            ps.setInt(4, r.getReviewerId());
            return ps.executeUpdate();
        }
    }

    // 리뷰 삭제 (NULL 처리)
    @Override
    public int delete(int reservationId, int userId) throws SQLException {
        String sql = """
        UPDATE reservation
        SET review_rating = NULL,
            review_content = NULL,
            review_created_at = NULL
        WHERE reservation_id = ?
          AND user_id = ?
        """;
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            ps.setInt(2, userId);

            return ps.executeUpdate(); // 성공 시 1 반환
        }
    }

    // 수동 커밋
    @Override
    public void commitIfNeeded() throws SQLException {
        if (conn != null && !conn.getAutoCommit()) {
            conn.commit();
            conn.close();
            conn = null;
        }
    }

    // 카페별 리뷰 조회 (rating 있는 것만)
    @Override
    public List<ReviewDto> findByCafe(int cafeId) throws SQLException {
        String sql = """
            SELECT reservation_id, user_id, cafe_id,
                   review_rating, review_content, review_created_at
            FROM reservation
            WHERE cafe_id = ? AND review_rating IS NOT NULL
            ORDER BY review_created_at DESC
        """;

        List<ReviewDto> list = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, cafeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReviewDto dto = new ReviewDto();
                dto.setReservationId(rs.getInt("reservation_id"));
                dto.setReviewerId(rs.getInt("user_id"));
                dto.setCafeId(rs.getInt("cafe_id"));
                dto.setRating(rs.getInt("review_rating"));
                dto.setContent(rs.getString("review_content"));
                dto.setCreatedAt(rs.getTimestamp("review_created_at").toLocalDateTime());
                list.add(dto);
            }
        }

        return list;
    }

    // 예약에 대해 이미 리뷰가 작성되었는지 확인
    @Override
    public boolean existsByReservation(int reservationId, int userId) throws SQLException {
        String sql = """
            SELECT COUNT(*) 
            FROM reservation 
            WHERE reservation_id = ? AND user_id = ? 
                  AND review_rating IS NOT NULL
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // 리뷰가 이미 존재하면 true
                }
            }
        }

        return false;
    }

    // TODO: 통합 시 보완할 부분 주석 정리됨
}
