// ===========================================
// ReservationService.java
// [임시 작성] 리뷰 기능 검증을 위해 만든 임시 Reservation 서비스 인터페이스
// ===========================================
package org.example.stayd.domain.reservation.service;

import org.example.stayd.domain.reservation.dto.ReservationDto;

public interface ReservationService {
    ReservationDto latestFinished(int userId);
}