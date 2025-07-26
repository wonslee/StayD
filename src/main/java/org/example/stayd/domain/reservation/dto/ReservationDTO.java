package org.example.stayd.domain.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.stayd.domain.cafe.model.DayOfWeek;
import org.example.stayd.domain.reservation.model.Reservation;

/**
 * 예약 정보를 데이터 전송 객체(DTO)로 표현한 클래스입니다.
 * <p>
 * Controller-View, Service-DAO 계층 간 데이터 전달에 사용됩니다.
 * Reservation 모델과 유사하지만, 계층 간 변환 및 직렬화에 최적화되어 있습니다.
 * </p>
 */
@Getter
@Builder
public class ReservationDTO {

    private Long reservationId;

    private Long userId;
    private Long cafeId;

    private LocalDate reservationDate;
    private int usageStartedAt;
    private int usageEndedAt;
    private DayOfWeek dayOfWeek;

    private int originalPrice;
    private int discountPrice;

    private LocalDateTime createdAt;

    private boolean canceled;
    private LocalDateTime canceledAt;

    private Integer rating;
    private String content;
    private LocalDateTime reviewCreatedAt;

    // TODO: 얘네 고민
    /* JDBC ↔ DTO 변환 편의 */

    /**
     * Reservation 도메인 객체를 ReservationDTO로 변환합니다.
     *
     * @param reservation 변환할 Reservation 객체
     * @return ReservationDTO로 변환된 객체
     */
    public static ReservationDTO of(Reservation reservation) {
        return ReservationDTO.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUserId())
                .cafeId(reservation.getCafeId())
                .reservationDate(reservation.getReservationDate())
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

    public static ReservationDTO of(ReservationWithCafeDTO reservation) {
        return ReservationDTO.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUserId())
                .cafeId(reservation.getCafeId())
                .reservationDate(reservation.getReservationDate())
                .usageStartedAt(reservation.getUsageStartedAt())
                .usageEndedAt(reservation.getUsageEndedAt())
                .dayOfWeek(DayOfWeek.from(reservation.getDayOfWeek()))
                .originalPrice(reservation.getOriginalPrice())
                .discountPrice(reservation.getDiscountPrice())
                .createdAt(reservation.getCreatedAt())
                .canceledAt(reservation.getCanceledAt())
                .rating(reservation.getRating())
                .content(reservation.getContent())
                .reviewCreatedAt(reservation.getReviewCreatedAt())
                .build();
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
                "reservationId=" + reservationId +
                ", userId=" + userId +
                ", cafeId=" + cafeId +
                ", reservationDate=" + reservationDate +
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