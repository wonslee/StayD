package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dto.ReviewDto;

public interface ReviewService {

    /** 리뷰 등록 (작성) */
    boolean writeReview(ReviewDto dto);

    /** 리뷰 수정 */
    boolean updateReview(ReviewDto dto);

    /**
     * 리뷰 삭제 (내용 NULL 처리)
     * @param reservationId 예약 ID
     * @param userId 리뷰 작성자 ID
     */
    boolean deleteReview(int reservationId, int userId);
}