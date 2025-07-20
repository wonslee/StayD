package org.example.stayd.domain.review.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 리뷰 작성/조회용 데이터 전송 객체 (DTO)
 * reviewerId, cafeId는 외부(controller)에서 세션 기반으로 주입됨
 */
@Getter
@Setter
@ToString
public class ReviewDto {
    private int    id;           // PK, auto increment

    /** 작성자 ID (SessionContext.getCurrentUserId() 등에서 주입) */
    private int    reviewerId;   // TODO: 컨트롤러에서 세션 기반 값으로 설정해야 함

    /** 카페 ID (현재 선택된 카페 - 세션 or 외부 주입) */
    private int    cafeId;       // TODO: 컨트롤러에서 주입, 테스트용으로 하드코딩 중일 수 있음

    private int    rating;
    private String content;
    private LocalDateTime createdAt; // SELECT용으로 사용됨
}
