package org.example.stayd.domain.reservation.service;

import org.example.stayd.domain.reservation.dao.ReservationDao;
import org.example.stayd.domain.reservation.dto.ReservationDto;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private ReservationDao reservationDao;

    public ReservationService() {
        this.reservationDao = new ReservationDao();
    }

    // 로그인한 유저의 cafe_id에 대한 예약 현황 조회 메서드
    public List<ReservationDto> getReservationStatusByLoggedInUser() throws SQLException {
        return reservationDao.getReservationStatusByLoggedInUser();  // 로그인한 유저의 cafe_id로 필터링된 데이터 반환
    }
}
