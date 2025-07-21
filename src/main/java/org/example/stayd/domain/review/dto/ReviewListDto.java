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

    private final int reservationId;
    private final int reviewerId;        // ✅ 새로 추가
    private final String reviewerName;
    private final int rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final int cafeId;

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
