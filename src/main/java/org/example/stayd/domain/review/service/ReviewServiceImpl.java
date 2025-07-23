package org.example.stayd.domain.review.service;

import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.review.dto.ReviewDto;

import java.sql.SQLException;
import java.util.List;

/**
 * 리뷰 등록, 수정, 삭제 등 쓰기 관련 서비스 구현체
 */
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao dao = new ReviewDaoImpl();

    /**
     * 리뷰 등록
     * - SessionManager에서 로그인한 사용자 정보를 가져와 reviewerId에 세팅
     */
    @Override
    public boolean writeReview(ReviewDto dto) {
        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();
            dto.setReviewerId(userId);
            int result = dao.insert(dto);
            dao.commitIfNeeded();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 리뷰 수정
     * - SessionManager에서 사용자 ID 주입
     * - 본인 리뷰만 수정 가능
     */
    @Override
    public boolean updateReview(ReviewDto dto) {
        try {
            int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();
            dto.setReviewerId(userId);
            int result = dao.update(dto);
            dao.commitIfNeeded();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 리뷰 삭제
     * - reservationId와 SessionManager에서 가져온 userId를 기반으로 삭제
     * - 리뷰 컬럼을 NULL로 설정
     */
    @Override
    public boolean deleteReview(int reservationId, int userId) {
        try {
            int result = dao.delete(reservationId, userId);
            dao.commitIfNeeded();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public List<ReviewDto> findAllByUserId(int userId) throws SQLException {
        return dao.findAllByUserId(userId);
    }

}
