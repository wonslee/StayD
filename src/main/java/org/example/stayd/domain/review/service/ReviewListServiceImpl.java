package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dao.ReviewListDao;
import org.example.stayd.domain.review.dao.ReviewListDaoImpl;
import org.example.stayd.domain.review.dto.ReviewListDto;
// import org.example.stayd.global.SessionContext; // TODO: 나중에 주석 해제 - 세션 기반 카페 ID 연동 시 필요

import java.util.List;

/**
 * ReviewListService 구현체
 *  └ 특정 카페 리뷰 목록 조회 & 평균 평점 계산 담당
 *
 *  삭제 기능은 ReviewListCell 내부에서 ReviewDao를 직접 호출하므로
 *     이 서비스는 "읽기 전용" 역할로 유지한다.
 */
public class ReviewListServiceImpl implements ReviewListService {

    /** DB 접근 DAO */
    private final ReviewListDao dao = new ReviewListDaoImpl();

    /**
     * 카페별 리뷰 목록 조회
     *
     * @param cafeId 카페 ID (현재는 외부에서 직접 주입)
     *               // TODO: 통합 시 SessionContext.getCurrentCafeId() 등으로 대체 가능
     * @return 리뷰 목록 (문제 발생 시 빈 리스트 반환)
     */
    @Override
    public List<ReviewListDto> getReviews(int cafeId) {
        try {
            return dao.findByCafe(cafeId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * 평균 평점 계산 (소수점 1자리)
     *
     * @param cafeId 카페 ID
     * @return 평균값 (리뷰가 없으면 0.0)
     */
    @Override
    public double getAverageRating(int cafeId) {
        return getReviews(cafeId).stream()
                .mapToInt(ReviewListDto::getRating)
                .average()
                .orElse(0.0);
    }

    /*
      TODO (통합 시)
     이 클래스는 조회 전용 역할만 담당하며 삭제 기능은 ReviewListCell에서 처리됨
    */
}
