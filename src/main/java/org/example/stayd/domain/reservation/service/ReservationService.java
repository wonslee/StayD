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

    /**
     * 선택한 날짜에 대한 예약 현황을 가져오는 메서드
     *
     * @param selectedDate 선택한 날짜 (java.sql.Date)
     * @return 예약 현황 목록 (List<ReservationDTO>)
     * @throws SQLException SQL 쿼리 실행 중 발생할 수 있는 예외
     */
    public List<ReservationDTO> getReservationStatusByLoggedInUser(Date selectedDate) throws SQLException {
        // 로그인한 유저의 cafe_id로 필터링된 예약 현황 데이터를 반환
        return reservationDao.getReservationStatusByLoggedInUser(selectedDate);
    }

    /**
     * 선택한 요일에 대한 예약 현황을 가져오는 메서드
     *
     * @param selectedDay 선택한 요일 (예: "MON", "TUE" 등)
     * @return 예약 현황 목록 (List<ReservationDTO>)
     * @throws SQLException SQL 쿼리 실행 중 발생할 수 있는 예외
     */
    public List<ReservationDTO> getReservationStatusByDay(String selectedDay) throws SQLException {
        // 선택한 요일에 대한 예약 현황 데이터를 반환
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
                long newId = reservationDAO.create(reservation);

                Optional<Reservation> optionalReservation = reservationDAO.findById(newId);
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
     * 예약 상세 조회
     */
    public ReservationDTO getReservationDetail(long reservationId) throws SQLException {
        Optional<Reservation> reservationOpt = reservationDAO.findById(reservationId);
        if (reservationOpt.isEmpty()) return null;
        return ReservationDTO.of(reservationOpt.get());
    }

    /**
     * 예약 삭제 (존재, 권한, 상태 체크 포함)
     */
    public void deleteReservation(long reservationId, long userId) throws SQLException {
        Optional<Reservation> reservationOpt = reservationDAO.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException("예약이 존재하지 않습니다.");
        }
        Reservation reservation = reservationOpt.get();
        if (reservation.isCanceled()) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new SecurityException("본인 예약만 취소할 수 있습니다.");
        }
        // 실제 삭제
        reservationDao.deleteById(reservationId);
    }
}