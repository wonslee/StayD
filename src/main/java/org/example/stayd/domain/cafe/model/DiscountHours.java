package org.example.stayd.domain.cafe.model;

import lombok.Builder;


@Builder
public record DiscountHours(DayOfWeek dayOfWeek, int discountStart, int discountEnd, int discountRate) {
} 