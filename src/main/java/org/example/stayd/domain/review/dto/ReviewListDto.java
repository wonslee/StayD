// ===========================================
// ReviewListDto.java  (revised)
// reviewerId 제거 → reviewerName 비교로 본인 글 판별
// ===========================================
package org.example.stayd.domain.review.dto;

import lombok.Getter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@ToString
public class ReviewListDto {

    private final int reservationId;      // 예약 ID
    private final int reviewerId;         // 작성자 user_id
    private final String reviewerName;    // 작성자 로그인 ID
    private final int rating;             // 별점 (1~5)
    private final String content;         // 리뷰 내용
    private final LocalDateTime createdAt;// 작성 시각
    private final int cafeId;             // 카페 ID

    public ReviewListDto(int reservationId, int reviewerId, String reviewerName, int rating,
                         String content, LocalDateTime createdAt, int cafeId) {
        this.reservationId = reservationId;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.cafeId = cafeId;
    }
}

/*
  TODO (삭제 기능)
  - SessionContext.getLoginId() 등으로 가져온 현재 로그인 loginId 와
    reviewerName 비교 후 본인 글이면 삭제 버튼 노출
*/
