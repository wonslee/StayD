package org.example.stayd.domain.cafe.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class Cafe {
    @NotNull
    private Long cafeId;
    @NotNull
    private Long ownerId;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 100)
    private String address;

    @NotNull
    @PositiveOrZero
    private Integer pricePerHour;

    @NotBlank
    @Size(max = 100)
    private String description;

    @NotBlank
    @Size(max = 30)
    private String phoneNumber;

    @Size(max = 50)
    private String imageUrl;

    private LocalDateTime createdAt;

    private List<OperationHours> operationHourList;
    private List<DiscountHours> discountHourList;

    // constructor - call validation method
    public Cafe(Long cafeId, Long ownerId, String name, String address, Integer pricePerHour, String description,
                String phoneNumber, String imageUrl, LocalDateTime createdAt, List<OperationHours> operationHourList,
                List<DiscountHours> discountHourList) {
        this.cafeId = cafeId;
        this.ownerId = ownerId;
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.operationHourList = operationHourList;
        this.discountHourList = discountHourList;
        validate();
    }

    private void validate() {

        if (name == null || name.length() > 50) {
            throw new IllegalArgumentException("name은 1~50자");
        }
        if (address != null && address.length() > 100) {
            throw new IllegalArgumentException("address는 최대 100자");
        }
        if (pricePerHour == null || pricePerHour < 0) {
            throw new IllegalArgumentException("pricePerHour는 0 이상");
        }
        if (description == null || description.length() > 100) {
            throw new IllegalArgumentException("description은 1~100자");
        }
        if (phoneNumber == null || phoneNumber.length() > 30) {
            throw new IllegalArgumentException("phoneNumber는 1~30자");
        }
        if (imageUrl != null && imageUrl.length() > 50) {
            throw new IllegalArgumentException("imageUrl은 최대 50자");
        }
    }
}
