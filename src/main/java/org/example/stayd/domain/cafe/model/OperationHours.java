package org.example.stayd.domain.cafe.model;

import lombok.Builder;

@Builder
public record OperationHours(DayOfWeek dayOfWeek, int operationStart, int operationEnd) {
}