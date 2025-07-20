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

    private final int id;               // 리뷰 ID (PK)
    private final String reviewerName;  // 로그인 ID
    private final int rating;
    private final String content;
    private final LocalDateTime createdAt;

    public ReviewListDto(int id, String reviewerName, int rating,
                         String content, LocalDateTime createdAt) {
        this.id = id;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
    }
}

/*
  TODO (삭제 기능)
  - SessionContext.getLoginId() 등으로 가져온 현재 로그인 loginId 와
    reviewerName 비교 후 본인 글이면 삭제 버튼 노출
*/
