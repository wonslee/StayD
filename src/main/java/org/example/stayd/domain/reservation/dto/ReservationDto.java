package org.example.stayd.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private int reservationId;
    private int userId;
    private int cafeId;
    private Timestamp usageStartedAt;
    private Timestamp usageEndedAt;
    private String dayOfWeek;
    private double originalPrice;
    private double discountPrice;
    private Timestamp createdAt;
    private String isCanceled;
    private Timestamp canceledAt;
    private int rating;
    private String content;
    private Timestamp reviewCreatedAt;
}