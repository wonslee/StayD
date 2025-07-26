package org.example.stayd.domain.reservation.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.stayd.domain.cafe.model.DayOfWeek;

/**
 * 예약 정보를 나타내는 도메인 모델 클래스입니다.
 * <p>
 * 예약의 고유 ID, 사용자, 카페, 예약 날짜, 시간, 가격, 취소 여부, 리뷰 등
 * 예약과 관련된 모든 정보를 보유합니다.
 * Bean Validation 어노테이션을 통해 데이터 유효성을 검증합니다.
 * </p>
 */
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
    @Min(0)
    @Max(23)
    private int usageStartedAt;

    @NotNull
    @Min(0)
    @Max(23)
    private int usageEndedAt;

    @NotNull
    private DayOfWeek dayOfWeek;  // DB: VARCHAR2(3)

    @PositiveOrZero
    private Integer originalPrice;

    @PositiveOrZero
    private Integer discountPrice;

    private LocalDateTime createdAt;

    /* 예약 취소 */
    private boolean isCanceled;     // DB = 'Y' / NULL
    private LocalDateTime canceledAt;

    /* 리뷰 */
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 2000)
    private String content;

    private LocalDateTime reviewCreatedAt;

    // TODO: 'Y' OR NULL -> boolean 변환
    /**
     * 예약의 추가 비즈니스 제약을 검증합니다.
     * <p>
     * 예: 종료 시간이 시작 시간보다 빠르면 예외 발생
     * </p>
     * @throws IllegalArgumentException 종료 시간이 시작 시간보다 빠른 경우
     */
    public void validateCustom() {
        System.out.println("validateCustom()");
        if (usageEndedAt < usageStartedAt) {
            throw new IllegalArgumentException("종료 시간이 시작 시간보다 이전입니다.");
        }
    }
}
