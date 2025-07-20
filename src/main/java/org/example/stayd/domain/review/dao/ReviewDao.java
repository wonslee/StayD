package org.example.stayd.domain.review.dao;

import org.example.stayd.domain.review.dto.ReviewDto;

import java.sql.SQLException;
import java.util.List;

/**
 * Review 테이블 관련 DAO 인터페이스
 * - 리뷰 저장, 조회, 삭제, 트랜잭션 커밋 등 정의
 * - 통합 시 SessionContext 기반 사용자 정보 활용 예정
 */
public interface ReviewDao {

    /** 리뷰 1건 저장 : 성공 시 1 반환 */
    int insert(ReviewDto review) throws SQLException;

    /** 특정 카페의 모든 리뷰 조회 */
    List<ReviewDto> findByCafe(int cafeId) throws SQLException;

    /** 수동 커밋 (insert/delete 이후에 호출 필요) */
    void commitIfNeeded() throws SQLException;

    /** 리뷰 1건 삭제 */
    int delete(int reviewId) throws SQLException;

    /*
     TODO (통합 시)
     1. insert 시 review.getReviewerId()가 SessionContext 기반 값인지 확인 필요
        - 현재는 Controller 단에서 userId 하드코딩 중 (→ 추후 SessionContext 연동)
     2. findByCafe 메서드는 외부에서 cafeId 주입 → Controller 또는 Session에서 받아올 수 있음
     3. 삭제 역시 본인 리뷰 여부 검증은 Controller에서 처리 중
    */
}
