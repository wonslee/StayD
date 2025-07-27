package org.example.stayd.domain.review.dto;

import java.time.LocalDateTime;

/**
 * 카페 또는 마이페이지에 출력할 리뷰 목록용 DTO
 * - 작성자 ID, 평점, 내용, 작성일 등 포함
 */
public class ReviewListDTO {

    private String loginId;          // 리뷰 작성자 로그인 아이디
    private String content;          // 리뷰 본문 내용
    private int rating;              // 평점 (1~5)
    private LocalDateTime createdAt; // 리뷰 작성일시
    private long userId;
    // 기본 생성자
    public ReviewListDTO() {}


    /** 로그인 ID 반환 */
    public String getLoginId() {
        return loginId;
    }
    /** 로그인 ID 설정 */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    /** 리뷰 내용 반환 */
    public String getContent() {
        return content;
    }
    /** 리뷰 내용 설정 */
    public void setContent(String content) {
        this.content = content;
    }
    /** 평점 반환 */
    public int getRating() {
        return rating;
    }
    /** 평점 설정 */
    public void setRating(int rating) {
        this.rating = rating;
    }
    /** 리뷰 작성일 반환 */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    /** 리뷰 작성일 설정 */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    /** 사용자 ID 설정 */
    public void setUserId(long userId) {
        this.userId = userId;
    }
    /** 사용자 ID 반환 */
    public long getUserId() {
        return userId;
    }

    public void setReviewCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;

    }
}