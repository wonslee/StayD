package org.example.stayd.domain.reservation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.stayd.domain.cafe.dto.CafeDto;

@Data
@Builder
public class ReservationWithCafeDTO {
    private long reservationId;
    private long userId;
    private long cafeId;
    private String cafeName;
    private String phoneNumber;
    private String address;
    private Integer pricePerHour;
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
    private String branchName;

    public CafeDto.DetailResponse toCafeDetailResponse() {
        return new CafeDto.DetailResponse(
            this.cafeId,
            this.cafeName,
            address, // address는 ReservationWithCafeDTO에 없으므로 null
            pricePerHour, // pricePerHour도 없으면 null
            null, // description
            phoneNumber, // phoneNumber
            null, // imageUrl
            null, // operatingDays
            null, // operatingStartHour
            null  // operatingEndHour
        );
    }
}