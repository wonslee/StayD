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
        SELECT r.review_id,
               u.login_id AS reviewer_name,
               r.rating,
               r.content,
               r.created_at
        FROM   review r
        JOIN   users u ON r.reviewer_id = u.user_id
        WHERE  r.cafe_id = ?
        ORDER  BY r.created_at DESC
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
                            rs.getInt("review_id"),                    // 리뷰 ID
                            rs.getString("reviewer_name"),             // 작성자 login_id (JOIN)
                            rs.getInt("rating"),                       // 별점
                            rs.getString("content"),                   // 리뷰 내용
                            rs.getTimestamp("created_at").toLocalDateTime() // 작성일시
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
