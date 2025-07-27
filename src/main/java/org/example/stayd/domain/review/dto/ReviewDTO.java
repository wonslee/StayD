package org.example.stayd.domain.review.dto;

import java.time.LocalDateTime;
/**
 * 리뷰 정보를 담는 DTO 클래스
 * - 예약 ID, 카페 ID, 사용자 ID 등 식별자 포함
 * - 리뷰 내용, 평점, 작성일 등 포함
 */
public class ReviewDTO {

    private long reservationId;
    private long cafeId;
    private long userId;
    private String loginId;
    private String cafeName; // 화면에 표시용 (조인 또는 서비스단에서 세팅)
    private int rating;
    private String content;
    private LocalDateTime reviewCreatedAt;

    public ReviewDTO() {}

    public ReviewDTO(long reservationId, long cafeId, long userId, String cafeName,
                     int rating, String content, LocalDateTime reviewCreatedAt) {
        this.reservationId = reservationId;
        this.cafeId = cafeId;
        this.userId = userId;
        this.cafeName = cafeName;
        this.rating = rating;
        this.content = content;
        this.reviewCreatedAt = reviewCreatedAt;
    }
    /** 예약 ID 반환 */
    public long getReservationId() {
        return reservationId;
    }

    /** 예약 ID 설정 */
    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
    }

    public long getCafeId() {
        return cafeId;
    }

    public void setCafeId(long cafeId) {
        this.cafeId = cafeId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public int getRating() {
        return rating;
    }
    /** 평점 반환 */
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    /** 리뷰 작성일 반환 */
    public LocalDateTime getReviewCreatedAt() {
        return reviewCreatedAt;
    }
    /** 리뷰 작성일 설정 */
    public void setReviewCreatedAt(LocalDateTime reviewCreatedAt) {
        this.reviewCreatedAt = reviewCreatedAt;
    }
    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

}