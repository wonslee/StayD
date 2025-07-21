// ===========================================
// ReservationDaoImpl.java
// [임시 작성] 리뷰 작성 기능 테스트를 위한 임시 예약 DAO 구현체
// 실제 예약 로직 구현 담당자와 통합 시, 구조 변경 필요
// ===========================================
package org.example.stayd.domain.reservation.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.reservation.dto.ReservationDto;

import java.sql.*;

public class ReservationDaoImpl implements ReservationDao {

    private final DatabaseConnection db = new DatabaseConnection();

    // 좌석 정보는 아직 DB에 구현되지 않았으므로 주석 처리
    private static final String SQL = """
        SELECT r.reservation_id,
               r.cafe_id,
               c.name AS cafe_name,
               /* s.seat_number, */                     -- ★ 좌석 생기면 주석 해제
               r.usage_started_at,
               r.usage_ended_at,
               (SELECT NVL(ROUND(AVG(r2.review_rating),1),0)
                  FROM reservation r2
                 WHERE r2.cafe_id = c.cafe_id AND r2.review_rating IS NOT NULL) AS avg_score
        FROM   reservation r
        JOIN   cafe c ON r.cafe_id = c.cafe_id
        /* JOIN seat s ON r.seat_id = s.seat_id */      -- ★ 좌석 FK 생기면 사용
        WHERE  r.user_id              = ?
          AND  NVL(r.is_canceled,'N') = 'N'
          AND  TRUNC(r.usage_ended_at) <= TRUNC(SYSDATE)
        ORDER  BY r.usage_ended_at DESC
        FETCH FIRST 1 ROWS ONLY
        """;

    @Override
    public ReservationDto latestFinishedForUser(int userId) throws SQLException {
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ReservationDto(
                            rs.getInt("reservation_id"),
                            rs.getInt("cafe_id"),
                            rs.getString("cafe_name"),
                            rs.getTimestamp("usage_started_at").toLocalDateTime(),
                            rs.getTimestamp("usage_ended_at").toLocalDateTime(),
                            rs.getDouble("avg_score")
                    );
                }
            }
        }
        return null;   // 예약 이력 없을 시 null 반환
    }

    // TODO: 통합 시 조치사항
    // - ReservationDaoImpl 클래스는 예약 모듈 담당자와 상의하여 제거 또는 통합
    // - 예약 쿼리 최적화, 좌석 정보 추가 등은 실제 DB 구조 기준으로 조정
    // - 리뷰 작성 기능에서는 인터페이스로만 의존하도록 변경해도 좋음
}
