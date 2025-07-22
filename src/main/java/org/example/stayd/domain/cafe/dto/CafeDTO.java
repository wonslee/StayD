package org.example.stayd.domain.cafe.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.stayd.domain.cafe.model.Cafe;

@Getter
@Builder
@AllArgsConstructor
public class CafeDTO {
    private Long cafeId;
    private Long ownerId;
    private String name;
    private String address;
    private int pricePerHour;
    private String description;
    private String phoneNumber;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static CafeDTO of(Cafe cafe) {
        return CafeDTO.builder()
                .cafeId(cafe.getCafeId())
                .ownerId(cafe.getOwnerId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .pricePerHour(cafe.getPricePerHour())
                .description(cafe.getDescription())
                .phoneNumber(cafe.getPhoneNumber())
                .imageUrl(cafe.getImageUrl())
                .createdAt(cafe.getCreatedAt())
                .build();
    }
}
