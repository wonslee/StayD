// ===========================================
// ReservationServiceImpl.java
// [임시 작성] 리뷰 기능에서 예약 정보를 받아오기 위해 만든 테스트용 서비스
// 실제 예약 서비스 구현체와 통합 시 대체 필요
// ===========================================
package org.example.stayd.domain.reservation.service;

import org.example.stayd.domain.reservation.dao.ReservationDao;
import org.example.stayd.domain.reservation.dao.ReservationDaoImpl;
import org.example.stayd.domain.reservation.dto.ReservationDto;

public class ReservationServiceImpl implements ReservationService {

    private final ReservationDao dao = new ReservationDaoImpl();

    @Override
    public ReservationDto latestFinished(int userId) {
        try {
            return dao.latestFinishedForUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO: 통합 시 조치사항
    // - ReservationServiceImpl은 진짜 예약 비즈니스 로직 클래스와 통합
    // - 예외처리 방식 통일 (로그 or 서비스 응답 모델)
}
