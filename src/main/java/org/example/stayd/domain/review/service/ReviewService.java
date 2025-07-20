package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dto.ReviewDto;

public interface ReviewService {
    boolean writeReview(ReviewDto dto);
}
