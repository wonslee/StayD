package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dao.ReviewDao;
import org.example.stayd.domain.review.dao.ReviewDaoImpl;
import org.example.stayd.domain.review.dto.ReviewDto;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao dao = new ReviewDaoImpl();

    @Override
    public boolean writeReview(ReviewDto dto) {
        // 욕설 필터, 블랙리스트 체크 등 추가 가능
        try {
            return dao.insert(dto) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
