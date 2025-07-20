// ===========================================
// ReservationDto.java
// [임시 작성] 리뷰 작성 기능을 위한 간소화된 예약 DTO
// 실제 예약 DTO와 통합 시 중복 방지 및 필드 조정 필요
// ===========================================
package org.example.stayd.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationDto {
    private final int reservationId;
    private final int cafeId;
    private final String cafeName;
    // private final String seatNumber; // ★ 좌석 정보 생기면 주석 해제
    private final LocalDateTime usageStart;
    private final LocalDateTime usageEnd;
    private final double avgScore;

    // TODO: 통합 시 조치사항
    // - ReservationDto가 이미 존재한다면 이 클래스 삭제 또는 통합
    // - 필요한 필드(좌석, 가격 등) 포함 여부 점검
}