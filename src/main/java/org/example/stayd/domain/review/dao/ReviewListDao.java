package org.example.stayd.domain.review.dao;

import org.example.stayd.domain.review.dto.ReviewListDto;
import java.sql.SQLException;
import java.util.List;

public interface ReviewListDao {
    /** 특정 카페의 리뷰 목록 (최신순) */
    List<ReviewListDto> findByCafe(int cafeId) throws SQLException;
}
