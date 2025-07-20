package org.example.stayd.domain.review.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.review.dto.ReviewDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDaoImpl implements ReviewDao {

    private final DatabaseConnection db = new DatabaseConnection();

    /* ---------- 커넥션을 필드로 보관 ---------- */
    private Connection conn;     // insert() → commitIfNeeded() 에서 재사용

    @Override
    public int insert(ReviewDto r) throws SQLException {
        String sql = "INSERT INTO review (reviewer_id, cafe_id, rating, content) VALUES (?,?,?,?)";

        conn = db.getConnection();
        System.out.println("▶ connected schema = " + conn.getMetaData().getUserName());
        conn.setAutoCommit(false);             // ★ 수동 커밋 모드로 전환

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, r.getReviewerId());
            ps.setInt   (2, r.getCafeId());
            ps.setInt   (3, r.getRating());
            ps.setString(4, r.getContent());
            return ps.executeUpdate();            // 1 행 성공 시 1 반환
        }
    }

    /** Controller → dao.commitIfNeeded() 호출 시 사용 */
    public void commitIfNeeded() throws SQLException {
        if (conn != null && !conn.getAutoCommit()) {
            conn.commit();
            conn.close();          // 커넥션 닫아주기
            conn = null;
        }
    }

    @Override
    public List<ReviewDto> findByCafe(int cafeId) throws SQLException {
        String sql = "SELECT * FROM review WHERE cafe_id = ?";
        List<ReviewDto> list = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, cafeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReviewDto dto = new ReviewDto();
                dto.setId         (rs.getInt("review_id"));   // PK 컬럼명 확인
                dto.setReviewerId (rs.getInt("reviewer_id"));
                dto.setCafeId     (cafeId);
                dto.setRating     (rs.getInt("rating"));
                dto.setContent    (rs.getString("content"));
                dto.setCreatedAt  (rs.getTimestamp("created_at").toLocalDateTime());
                list.add(dto);
            }
        }
        return list;
    }
}
