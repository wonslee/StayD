package org.example.stayd.domain.review.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ReviewDto {
    private int    id;        // PK, auto increment
    private int    reviewerId;
    private int    cafeId;
    private int    rating;
    private String content;
    private LocalDateTime createdAt; // 필요하면
}
