package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.review.dto.ReviewDto;
// import org.example.stayd.global.SessionContext; // TODO: 통합 시 주석 해제 – 세션 정보 활용

/**
 * ReviewService 구현 클래스 (reservation 테이블 기반)
 *  - 리뷰 작성, 수정, 삭제 기능 담당
 */
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao dao = new ReviewDaoImpl();

    /**
     * 리뷰 작성 (reservation 테이블에 업데이트)
     * @param dto 리뷰 정보 (userId, reservationId 포함)
     * @return 성공 여부
     */
    @Override
    public boolean writeReview(ReviewDto dto) {
        try {
            int result = dao.insert(dto);  // UPDATE reservation SET review_... WHERE reservation_id = ?
            dao.commitIfNeeded();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 리뷰 수정 (내용 및 별점만 수정 가능)
     */
    @Override
    public boolean updateReview(ReviewDto dto) {
        try {
            int result = dao.update(dto);  // UPDATE reservation SET review_rating = ?, ...
            dao.commitIfNeeded();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 리뷰 삭제 (내용/별점 NULL 처리)
     * @param reservationId 예약 ID (PK)
     * @param userId        사용자 ID (작성자 본인인지 확인용)
     */
    @Override
    public boolean deleteReview(int reservationId, int userId) {
        try {
            int result = dao.delete(reservationId, userId); // WHERE reservation_id = ? AND user_id = ?
            dao.commitIfNeeded();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     ✅ TODO (통합 시 주의사항)
     ---------------------------------------
     1. writeReview(), updateReview() 호출 전에
        dto.setUserId(SessionContext.getCurrentUserId()) 설정 필요

     2. deleteReview()는 본인 예약이 맞는지 검증이 controller에서 이뤄져야 함
        - 현재는 reservation_id + user_id로 제한됨

     3. 리뷰 등록은 reservation_id가 존재하는 경우에만 가능
        - 즉, 예약한 사람만 작성 가능하게 이미 보장됨
    */
}
