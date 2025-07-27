// 작성자 : 이해든
package org.example.stayd.domain.review.dto;

import java.time.LocalDateTime;

/**
 * 리뷰 목록 화면에 보여줄 리뷰 1건에 대한 정보 DTO - 사용자 아이디 (login_id) - 리뷰 내용 - 평점 - 작성일
 */
public class ReviewListDTO {

    private String loginId;          // 리뷰 작성자 로그인 아이디
    private String content;          // 리뷰 본문 내용
    private int rating;              // 평점 (1~5)
    private LocalDateTime createdAt; // 리뷰 작성일시
    private long userId;

    // 기본 생성자
    public ReviewListDTO() {
    }

    // getter/setter
    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setReviewCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;

    }
}