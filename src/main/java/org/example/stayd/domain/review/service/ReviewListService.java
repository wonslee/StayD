package org.example.stayd.domain.review.service;

import org.example.stayd.domain.review.dto.ReviewListDto;
import java.util.List;

public interface ReviewListService {
    List<ReviewListDto> getReviews(int cafeId);
    double getAverageRating(int cafeId);
}
