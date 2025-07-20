package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dto.ReviewDto;

/** 리뷰 비즈니스 로직 인터페이스 */
public interface ReviewService {

    /** 리뷰 작성 */
    boolean writeReview(ReviewDto dto);

    /** 리뷰 삭제 – 성곻시 true / 실패시 false */
    boolean deleteReview(int reviewId);
}
