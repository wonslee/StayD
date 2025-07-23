package org.example.stayd.domain.reservation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservationWithCafeDTO {
    private long reservationId;
    private long userId;
    private long cafeId;
    private String cafeName;
    private LocalDate reservationDate;
    private int usageStartedAt;
    private int usageEndedAt;
    private String dayOfWeek;
    private int originalPrice;
    private int discountPrice;
    private LocalDateTime createdAt;
    private boolean isCanceled;
    private LocalDateTime canceledAt;
    private int rating;
    private String content;
    private LocalDateTime reviewCreatedAt;
}