package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dao.ReviewListDao;
import org.example.stayd.domain.review.dao.ReviewListDaoImpl;
import org.example.stayd.domain.review.dto.ReviewDto;
import org.example.stayd.domain.review.dto.ReviewListDto;
// import org.example.stayd.global.SessionContext; // TODO: 나중에 주석 해제 - 세션 기반 카페 ID 연동 시 필요

import java.sql.SQLException;
import java.util.List;

/**
 *
 *  특정 카페 리뷰 목록 조회 & 평균 평점 계산 담당
 *
 *  삭제 기능은 ReviewListCell 내부에서 ReviewDao를 직접 호출하므로
 *     이 서비스는 "읽기 전용" 역할로 유지
 */
public class ReviewListServiceImpl implements ReviewListService {

    /** DB 접근 DAO */
    private final ReviewListDao dao = new ReviewListDaoImpl();


    @Override
    public List<ReviewListDto> getReviews(int cafeId) {
        try {
            return dao.findByCafe(cafeId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // 빈 리스트 반환
        }
    }

    /**
     * 평균 평점 계산 (소수점 1자리)
     *평균값 (리뷰가 없으면 0.0)
     */
    @Override
    public double getAverageRating(int cafeId) {
        try {
            List<ReviewListDto> list = dao.findByCafe(cafeId);
            if (list.isEmpty()) return 0.0;

            int total = list.stream().mapToInt(ReviewListDto::getRating).sum();
            return (double) total / list.size();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    @Override
    public List<ReviewDto> findAllByUserId(int userId) throws SQLException {
        return dao.findByUserId(userId); // dao에 정의된 메서드 있어야 함
    }
}
