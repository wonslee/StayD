package org.example.stayd.domain.review.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.review.dto.ReviewDto;
// import org.example.stayd.global.SessionContext; // TODO: 나중에 주석 해제 - 세션에서 사용자 정보 연동할 경우

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ReviewDao 인터페이스의 구현 클래스
 * 리뷰 삽입, 삭제, 조회, 커밋 기능 담당
 */
public class ReviewDaoImpl implements ReviewDao {

    private final DatabaseConnection db = new DatabaseConnection();

    // insert(), delete()와 commitIfNeeded()에서 공유할 커넥션
    private Connection conn;

    /**
     * 리뷰 1건 저장 (INSERT)
     * 수동 커밋 모드로 수행됨
     */
    @Override
    public int insert(ReviewDto r) throws SQLException {
        String sql = "INSERT INTO review (reviewer_id, cafe_id, rating, content) VALUES (?,?,?,?)";

        conn = db.getConnection();
        conn.setAutoCommit(false);  // 수동 커밋 전환

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getReviewerId()); // TODO: controller에서 SessionContext 기반으로 값 넘어와야 함
            ps.setInt(2, r.getCafeId());
            ps.setInt(3, r.getRating());
            ps.setString(4, r.getContent());
            return ps.executeUpdate();  // 1행 삽입 성공 시 1 반환
        }
    }

    /**
     * 리뷰 1건 삭제 (DELETE)
     * 수동 커밋 모드로 수행됨
     */
    @Override
    public int delete(int reviewId) throws SQLException {
        String sql = "DELETE FROM review WHERE review_id = ?";

        conn = db.getConnection();
        conn.setAutoCommit(false);  // 수동 커밋 전환

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId); // TODO: 삭제 전 '본인 리뷰인지' 검증은 controller 단에서 필요
            return ps.executeUpdate();  // 1행 삭제 성공 시 1 반환
        }
    }

    /**
     * insert/delete 이후 수동 커밋 + 커넥션 종료
     */
    @Override
    public void commitIfNeeded() throws SQLException {
        if (conn != null && !conn.getAutoCommit()) {
            conn.commit();     // 명시적 커밋
            conn.close();      // 커넥션 닫기
            conn = null;       // 참조 해제
        }
    }

    /**
     * 특정 카페의 모든 리뷰 조회
     * @param cafeId 카페 ID
     * @return 해당 카페의 ReviewDto 목록
     */
    @Override
    public List<ReviewDto> findByCafe(int cafeId) throws SQLException {
        String sql = "SELECT * FROM review WHERE cafe_id = ?";
        List<ReviewDto> list = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, cafeId); // TODO: controller에서 cafeId 세션 또는 외부 주입
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReviewDto dto = new ReviewDto();
                dto.setId(rs.getInt("review_id"));
                dto.setReviewerId(rs.getInt("reviewer_id"));
                dto.setCafeId(cafeId);
                dto.setRating(rs.getInt("rating"));
                dto.setContent(rs.getString("content"));
                dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                list.add(dto);
            }
        }
        return list;
    }

    /*
     TODO (통합 시)
     1. insert(): controller가 세션 정보(SessionContext)를 통해 reviewerId, cafeId를 넘기도록 보장
     2. delete(): controller에서 "본인 글인지 여부" 사전 검증 필요
     3. findByCafe(): 현재 cafeId는 파라미터로 주입되므로 외부에서 세션 또는 선택값으로 설정해야 함
     4. DB 연결 및 수동 커밋 방식은 유지하되 예외 발생 시 rollback 고려할지 판단
    */
}
