package org.example.stayd.domain.cafe.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 스터디 카페 엔티티 클래스
 */
@Getter
@Setter
public class CafeModel {
    private Long cafeId;
    private Long ownerId;
    private String name;
    private String address;
    private Integer pricePerHour;
    private String description;
    private String phoneNumber;
    private String imageUrl;
    private LocalDateTime createdAt;

    // 기본 생성자
    public CafeModel() {}

    // 생성자 (ID 제외)
    public CafeModel(Long ownerId, String name, String address, Integer pricePerHour,
                     String description, String phoneNumber, String imageUrl) {
        this.ownerId = ownerId;
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    // 전체 생성자
    public CafeModel(Long cafeId, Long ownerId, String name, String address, Integer pricePerHour,
                     String description, String phoneNumber, String imageUrl, LocalDateTime createdAt) {
        this.cafeId = cafeId;
        this.ownerId = ownerId;
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CafeModel{" +
                "cafeId=" + cafeId +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", description='" + description + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}