package org.example.stayd.domain.review.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 리뷰 작성/조회용 데이터 전송 객체 (DTO)
 * reviewerId, cafeId는 외부(controller)에서 세션 기반으로 주입됨
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ReviewDto {

    private int reservationId;   // 리뷰 대상 예약 ID
    private int reviewerId;      // 작성자 ID (user_id)
    private int cafeId;          // 카페 ID

    private int rating;              // 별점 (1~5)
    private String content;          // 리뷰 내용
    private LocalDateTime createdAt; // 작성일시
}