// ===========================================
// ReviewListDaoImpl.java
// 특정 카페의 리뷰 목록 조회 DAO 구현체
// reviewer_id 는 SQL에서 조회하지 않고, reviewer_name (login_id)만 사용
// ===========================================

package org.example.stayd.domain.review.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.review.dto.ReviewListDto;
// import org.example.stayd.global.SessionContext; // TODO: 나중에 주석 해제 - 세션으로 카페 ID를 받는 구조라면

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewListDaoImpl implements ReviewListDao {

    private final DatabaseConnection db = new DatabaseConnection();

    // 리뷰 목록 조회 SQL (JOIN으로 작성자 login_id 가져옴)
    private static final String SQL = """
        SELECT r.reservation_id,
               r.cafe_id,
               r.user_id AS reviewer_id, 
               u.login_id AS reviewer_name,
               r.rating AS rating,
               r.content AS content,
               r.review_created_at AS created_at
        FROM   reservation r
        JOIN   users u ON r.user_id = u.user_id
        WHERE  r.cafe_id = ?
          AND  r.content IS NOT NULL
        ORDER  BY r.review_created_at DESC
        """;

    @Override
    public List<ReviewListDto> findByCafe(int cafeId) throws SQLException {
        List<ReviewListDto> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, cafeId);  // controller에서 주입한 cafeId 사용


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ReviewListDto(
                            rs.getInt("reservation_id"),
                            rs.getInt("reviewer_id"),            // 작성자 ID
                            rs.getString("reviewer_name"),       // 작성자 로그인 ID
                            rs.getInt("rating"),
                            rs.getString("content"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getInt("cafe_id")
                    ));
                }
            }
        }
        return list;
    }
    // 🔽 사용자 ID 기준 리뷰 조회 (마이페이지용)
    @Override
    public List<ReviewDto> findByUserId(int userId) throws SQLException {
        String sql = """
            SELECT reservation_id, user_id, cafe_id, rating, content, review_created_at
            FROM reservation
            WHERE user_id = ? AND content IS NOT NULL
            ORDER BY review_created_at DESC
        """;

        List<ReviewDto> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReviewDto dto = new ReviewDto();
                dto.setReservationId(rs.getInt("reservation_id"));
                dto.setReviewerId(rs.getInt("user_id"));
                dto.setCafeId(rs.getInt("cafe_id"));
                dto.setRating(rs.getInt("rating"));
                dto.setContent(rs.getString("content"));
                dto.setCreatedAt(rs.getTimestamp("review_created_at").toLocalDateTime());
                list.add(dto);
            }
        }

        return list;
    }
}
