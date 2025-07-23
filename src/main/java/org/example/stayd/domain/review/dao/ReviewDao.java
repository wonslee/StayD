package org.example.stayd.domain.review.dao;

import org.example.stayd.domain.review.dto.ReviewDto;

import java.sql.SQLException;
import java.util.List;

/**
 * Reservation 테이블 내 리뷰 컬럼 관련 DAO 인터페이스
 * - 예약 ID 기반 리뷰 저장, 수정, 삭제, 조회 기능 담당
 * - 별도의 review 테이블은 존재하지 않음
 */
public interface ReviewDao {

    /** 리뷰 최초 등록 (UPDATE) : 성공 시 1 반환 */
    int insert(ReviewDto review) throws SQLException;

    /** 리뷰 수정 (UPDATE) : 성공 시 1 반환 */
    int update(ReviewDto review) throws SQLException;

    /** 리뷰 삭제 (NULL 처리) : 성공 시 1 반환 */
    int delete(int reservationId, int userId) throws SQLException;

    /** 특정 카페의 모든 리뷰 조회 (리뷰가 존재하는 예약만) */
    List<ReviewDto> findByCafe(int cafeId) throws SQLException;

    /**  예약에 대한 리뷰 존재 여부 확인 */
    boolean existsByReservation(int reservationId, int userId) throws SQLException;

    /** 트랜잭션 커밋 (insert/update/delete 이후 호출 필수) */
    void commitIfNeeded() throws SQLException;
    /** 특정 유저가 작성한 모든 리뷰 조회 */
    List<ReviewDto> findAllByUserId(int userId) throws SQLException;
    /*
     TODO (통합 시)
     1. insert/update/delete 모두 SessionContext를 통해 로그인 유저 정보 (userId) 주입 필요
     2. reservationId는 사용자가 예약한 건인지 controller에서 사전 검증 권장
     3. findByCafe()는 review_rating IS NOT NULL 조건으로 필터링
     4. 예외 발생 시 rollback도 고려할 것
     */
}
