package org.example.stayd.domain.reservation.service;


import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.reservation.dao.ReservationDao;
import org.example.stayd.domain.reservation.dao.SeatDAO;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.model.Reservation;
import org.example.stayd.domain.reservation.model.Seat;

public class ReservationService {

    //    TODO: reservationDAO 하나로 합치기
    private final ReservationDao reservationDao;
    private final ReservationWDAO reservationDAO;
    private final SeatDAO seatDAO = new SeatDAO();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public ReservationService() {
        this.reservationDao = new ReservationDao();
        this.reservationDAO = new ReservationWDAO();
    }

    // 날짜별 예약 현황을 가져오는 메서드
    public List<ReservationDTO> getReservationStatusByLoggedInUser(Date selectedDate) throws SQLException {
        return reservationDao.getReservationStatusByLoggedInUser(selectedDate);  // 로그인한 유저의 cafe_id로 필터링된 데이터 반환
    }

    // 요일별 예약 현황을 가져오는 메서드
    public List<ReservationDTO> getReservationStatusByDay(String selectedDay) throws SQLException {
        return reservationDao.getReservationStatusByDay(selectedDay);
    }

    /**
     * 예약 생성
     *
     * @param seatId 예약하려는 좌석
     * @return 생성된 Reservation
     */
//    TODO: 유저 로그인 여부 검증
    public Reservation createReservation(
            long cafeId,
            long seatId,
            long userId,
            ReservationDTO reservationDTO
    ) throws SQLException {

        Reservation reservation = Reservation.builder()
                .cafeId(cafeId)
                .userId(userId)
                .reservationDate(reservationDTO.getReservationDate())
                .usageStartedAt(reservationDTO.getUsageStartedAt())
                .usageEndedAt(reservationDTO.getUsageEndedAt())
                .dayOfWeek(reservationDTO.getDayOfWeek())
                .originalPrice(reservationDTO.getOriginalPrice())
                .discountPrice(reservationDTO.getDiscountPrice())
                .build();
        System.out.println("reservation = " + reservation);
        System.out.println("reservation.getReservationDate() = " + reservation.getReservationDate());

        try {

            // Bean Validation
            var v = validator.validate(reservation);
            System.out.println("v = " + v);
            System.out.println("v.isEmpty() = " + v.isEmpty());
            reservation.validateCustom();
            if (!v.isEmpty()) {
                throw new ConstraintViolationException(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection conn = new DatabaseConnection().getConnection()) {
            System.out.println("conn.getCatalog() = " + conn.getCatalog());
            try {
                System.out.println("좌석 잠금 & 가용성 확인 (SELECT … FOR UPDATE)");
                // 1) 좌석 잠금 & 가용성 확인 (SELECT … FOR UPDATE)
                if (!seatDAO.lockAndCheckAvailable(conn, seatId)) {
                    throw new IllegalStateException("이미 예약된 좌석입니다.");
                }

                System.out.println("좌석 사용 Y → N 업데이트");
                // 2) 좌석 사용 Y → N 업데이트
                if (!seatDAO.updateAvailability(conn, seatId, false)) {
                    throw new IllegalStateException("좌석 상태 갱신 실패");
                }

                System.out.println("예약 INSERT");
                System.out.println("reservation.getCafeId() = " + reservation.getCafeId());
                System.out.println("reservation.getOriginalPrice() = " + reservation.getOriginalPrice());
                System.out.println("reservation.getReservationDate() = " + reservation.getReservationDate());
                // 3) 예약 INSERT
                long newId = reservationDAO.create(conn, reservation);

                Optional<Reservation> optionalReservation = reservationDAO.findById(conn, newId);
                if (optionalReservation.isEmpty()) {
                    throw new IllegalStateException("예약 생성 상태 비정상");
                } else {
                    return optionalReservation.get();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    /**
     * 카페별 좌석 현황 조회
     */
    public List<Seat> getSeats(long cafeId) throws SQLException {
        try (Connection conn = new DatabaseConnection().getConnection()) {
            return seatDAO.findByCafeId(conn, cafeId);
        }
    }

    /**
     * 특정 유저의 예약 목록 조회
     */
    public List<ReservationDTO> findByUser(int userId) {
        try {
            return reservationDAO.findByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}