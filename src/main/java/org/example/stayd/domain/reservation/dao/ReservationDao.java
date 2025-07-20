// ===========================================
// ReservationDao.java
// [임시 작성] 리뷰 작성 기능 테스트용으로 생성한 예약 DAO 인터페이스
// 실제 예약 모듈 담당자와 통합 시, 삭제 또는 수정될 수 있음
// ===========================================
package org.example.stayd.domain.reservation.dao;

import org.example.stayd.domain.reservation.dto.ReservationDto;
import java.sql.SQLException;

public interface ReservationDao {
    /**
     * 유저가 이용 완료한 예약 중 가장 최근 1건을 반환 (없으면 null)
     * [리뷰 작성 전 검증용으로 사용]
     */
    ReservationDto latestFinishedForUser(int userId) throws SQLException;
}
