package org.example.stayd.domain.review.dao;

import org.example.stayd.domain.review.dto.ReviewDto;
import java.sql.SQLException;
import java.util.List;

public interface ReviewDao {

    /** 1건 저장 : 성공하면 1 리턴 */
    int insert(ReviewDto review) throws SQLException;

    /** 카페별 리뷰 목록 */
    List<ReviewDto> findByCafe(int cafeId) throws SQLException;
    void commitIfNeeded() throws SQLException;
}
