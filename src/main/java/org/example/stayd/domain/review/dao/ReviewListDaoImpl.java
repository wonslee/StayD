// ===========================================
// ReviewListDaoImpl.java
// 특정 카페의 리뷰 목록 조회 DAO 구현체
// reviewer_id 는 SQL에서 조회하지 않고, reviewer_name (login_id)만 사용
// ===========================================

package org.example.stayd.domain.review.dao;

import org.example.stayd.common.DatabaseConnection;
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
               r.review_rating AS rating,
               r.review_content AS content,
               r.review_created_at AS created_at
        FROM   reservation r
        JOIN   users u ON r.user_id = u.user_id
        WHERE  r.cafe_id = ?
          AND  r.review_content IS NOT NULL
        ORDER  BY r.review_created_at DESC
        """;

    /**
     * 카페별 리뷰 목록 조회
     * @param cafeId 카페 ID
     * @return 해당 카페의 리뷰 목록
     */
    @Override
    public List<ReviewListDto> findByCafe(int cafeId) throws SQLException {
        List<ReviewListDto> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, cafeId);  // TODO: controller에서 SessionContext 또는 외부 주입값으로 전달되어야 함

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ReviewListDto(
                            rs.getInt("reservation_id"),
                            rs.getInt("reviewer_id"),            // ✅ 이 부분!
                            rs.getString("reviewer_name"),
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

    /*
      TODO (통합 시)
      1. cafeId는 controller에서 SessionContext.getCurrentCafeId() 또는 setCafeId()로 주입되어야 함
         - 현재는 파라미터 직접 전달 방식
      2. reviewer_name은 JOIN으로 가져오므로 따로 세션과 비교는 하지 않음 (→ Controller에서 처리)
    */
}
