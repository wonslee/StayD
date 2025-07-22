package org.example.stayd.domain.reservation.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import org.example.stayd.common.YesNoBooleanConverter;
import org.example.stayd.domain.reservation.model.DayOfWeek;
import org.example.stayd.domain.reservation.model.Reservation;

@Getter
@Builder
public class ReservationDTO {

    private Long reservationId;

    private Long userId;
    private Long cafeId;

    private int usageStartedAt;
    private int usageEndedAt;
    private DayOfWeek dayOfWeek;

    private int originalPrice;
    private int discountPrice;

    private Instant createdAt;

    private boolean canceled;
    private Instant canceledAt;

    private Integer rating;
    private String content;
    private Instant reviewCreatedAt;

    // TODO: 얘네 고민
    /* JDBC ↔ DTO 변환 편의 */

    public static ReservationDTO of(Reservation reservation) {
        return ReservationDTO.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUserId())
                .cafeId(reservation.getCafeId())
                .usageStartedAt(reservation.getUsageStartedAt())
                .usageEndedAt(reservation.getUsageEndedAt())
                .dayOfWeek(reservation.getDayOfWeek())
                .originalPrice(reservation.getOriginalPrice())
                .discountPrice(reservation.getDiscountPrice())
                .createdAt(reservation.getCreatedAt())
                .canceledAt(reservation.getCanceledAt())
                .rating(reservation.getRating())
                .content(reservation.getContent())
                .reviewCreatedAt(reservation.getReviewCreatedAt())
                .build();
    }

    // NOTE: 개발용
    @Override
    public String toString() {
        return "ReservationDTO{" +
                "reservationId=" + reservationId +
                ", userId=" + userId +
                ", cafeId=" + cafeId +
                ", usageStartedAt=" + usageStartedAt +
                ", usageEndedAt=" + usageEndedAt +
                ", dayOfWeek=" + dayOfWeek +
                ", originalPrice=" + originalPrice +
                ", discountPrice=" + discountPrice +
                ", createdAt=" + createdAt +
                ", canceled=" + canceled +
                ", canceledAt=" + canceledAt +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                ", reviewCreatedAt=" + reviewCreatedAt +
                '}';
    }
}
