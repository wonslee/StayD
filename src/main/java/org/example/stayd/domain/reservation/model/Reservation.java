package org.example.stayd.domain.reservation.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.stayd.common.YesNoBooleanConverter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    private Long reservationId;   // IDENTITY

    @NotNull
    private Long userId;          // FK

    @NotNull
    private Long cafeId;          // FK


    /** 예약한 “날짜” (시간대와 분리) */
    @NotNull
    private LocalDate reservationDate;   // DB: DATE

    @NotNull
    @Size(min = 0, max = 24)
    private int usageStartedAt;

    @NotNull
    @Size(min = 0, max = 24)
    private int usageEndedAt;

    @NotNull
    private DayOfWeek dayOfWeek;  // DB: VARCHAR2(3)

    @PositiveOrZero
    private Integer originalPrice;

    @PositiveOrZero
    private Integer discountPrice;

    private Instant createdAt;

    /* 예약 취소 */
    private boolean isCanceled;     // DB = 'Y' / NULL
    private Instant canceledAt;

    /* 리뷰 */
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 2000)
    private String content;

    private Instant reviewCreatedAt;

    // TODO: 'Y' OR NULL -> boolean 변환
    /**
     * 추가 논리 제약
     */
    public void validateCustom() {
        if (usageEndedAt < usageStartedAt) {
            throw new IllegalArgumentException("종료 시간이 시작 시간보다 이전입니다.");
        }
    }
}
