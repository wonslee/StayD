package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dto.ReviewDto;

import java.sql.SQLException;
import java.util.List;

public interface ReviewService {

    /** 리뷰 등록 (작성) */
    boolean writeReview(ReviewDto dto);

    /** 리뷰 수정 */
    boolean updateReview(ReviewDto dto);

    /**
     * 리뷰 삭제 (내용 NULL 처리)
     */
    boolean deleteReview(int reservationId, int userId);

    List<ReviewDto> findAllByUserId(int userId) throws SQLException;
}