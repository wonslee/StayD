package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.review.dto.ReviewDto;
// import org.example.stayd.global.SessionContext; // TODO: 나중에 주석 해제 - 세션 정보 연동 시 필요

/**
 * ReviewService 구현 클래스
 *  └ 작성(insert) · 삭제(delete) 기능 담당
 *
 *  작성 시: controller에서 SessionContext 기반 사용자 ID 주입 필요
 *  삭제 시: controller 또는 상위 로직에서 본인 글인지 검증 후 호출되어야 함
 */
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao dao = new ReviewDaoImpl();

    /**
     * 리뷰 작성
     * @param dto 리뷰 정보 (작성자 ID 포함)
     * @return 성공 여부
     */
    @Override
    public boolean writeReview(ReviewDto dto) {
        try {
            int result = dao.insert(dto);        // TODO: dto.getReviewerId()는 세션 기반 값이어야 함
            dao.commitIfNeeded();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 리뷰 삭제
     * @param reviewId 삭제할 리뷰 ID
     * @return 성공 여부
     *
     * 주의: 본인 글인지 여부는 이 서비스에서는 검사하지 않음
     *       → controller 또는 ReviewListCell 등에서 사전 검증 필요
     */
    @Override
    public boolean deleteReview(int reviewId) {
        try {
            int result = dao.delete(reviewId);   // TODO: 본인 글인지 여부는 상위 계층에서 확인해야 함
            dao.commitIfNeeded();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
      TODO (통합 시)
      1. writeReview() 호출 전에 dto.setReviewerId(SessionContext.getCurrentUserId()) 로 설정 필수
      2. deleteReview()는 본인 글인지 확인 후 호출되어야 하므로
         controller 또는 ReviewListCell 등에서 loginId 비교 필요
    */
}
