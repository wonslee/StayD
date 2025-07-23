package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.review.dto.ReviewListDto;

import java.sql.SQLException;
import java.util.List;

/**
 * 리뷰 목록 출력 전용 서비스 인터페이스
 * - 단순 조회만 지원 (등록/수정/삭제는 ReviewService 에서 처리)
 */
public interface ReviewListService {
    List<ReviewListDto> getReviews(int cafeId);
    double getAverageRating(int cafeId);

    List<ReviewDto> findAllByUserId(int userId) throws SQLException;
}
