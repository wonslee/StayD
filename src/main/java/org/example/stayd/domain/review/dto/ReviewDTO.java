package org.example.stayd.domain.review.dto;

import java.time.LocalDateTime;

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

    public long getReservationId() {
        return reservationId;
    }

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

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getReviewCreatedAt() {
        return reviewCreatedAt;
    }

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
