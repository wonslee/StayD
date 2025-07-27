// 작성자 : 이원석, 방대혁
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
import org.example.stayd.domain.reservation.dao.ReservationDao;
import org.example.stayd.domain.reservation.dao.ReservationWDAO;
import org.example.stayd.domain.reservation.dao.SeatDAO;
import org.example.stayd.domain.reservation.dto.ReservationDTO;
import org.example.stayd.domain.reservation.model.Reservation;
import org.example.stayd.domain.reservation.model.Seat;

/**
 * 예약 관련 비즈니스 로직을 담당하는 서비스 클래스입니다.
 * <p>
 * 예약 생성, 예약 현황 조회, 예약 취소 등 도메인 규칙 및 검증, 트랜잭션 관리, 예외 처리 등 핵심 로직을 구현합니다.
 * </p>
 */
public class ReservationService {

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
     * 선택한 요일의 예약 현황을 조회합니다.
     *
     * @param selectedDay 요일 (예: "MON", "TUE")
     * @return 예약 현황 목록
     * @throws SQLException 데이터베이스 접근 중 오류 발생 시
     */
    public List<ReservationDTO> getReservationStatusByDay(String selectedDay) throws SQLException {
        // 선택한 요일에 대한 예약 현황 데이터를 반환
        return reservationDao.getReservationStatusByDay(selectedDay);
    }


    /**
     * 예약을 생성합니다.
     * <p>
     * 1. 예약 정보 유효성 검증 (Bean Validation, 커스텀 검증) 2. 좌석 가용성 확인 및 락킹 3. 좌석 상태 업데이트 4. 예약 정보 DB 저장
     * </p>
     *
     * @param cafeId         카페 ID
     * @param seatId         좌석 ID
     * @param userId         사용자 ID
     * @param reservationDTO 예약 정보 DTO
     * @return 생성된 예약 정보
     * @throws SQLException                 데이터베이스 접근 중 오류 발생 시
     * @throws ConstraintViolationException 예약 정보 유효성 검증 실패 시
     * @throws IllegalStateException        좌석이 이미 예약된 경우 등 비즈니스 로직 위반 시
     */
    public Reservation createReservation(
            ReservationDTO reservationDTO,
            long seatId
    ) throws SQLException {
        Reservation reservation = reservationDTO.toEntity();

        try {
            // Bean Validation
            var v = validator.validate(reservation);
            reservation.validateCustom();
            if (!v.isEmpty()) {
                throw new ConstraintViolationException(v);
            }
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        }

        try (Connection conn = new DatabaseConnection().getConnection()) {
            try {
                // 1) 좌석 잠금 & 가용성 확인 (SELECT … FOR UPDATE)
                if (!seatDAO.lockAndCheckAvailable(conn, seatId)) {
                    throw new IllegalStateException("이미 예약된 좌석입니다.");
                }

                // 2) 좌석 사용 Y → N 업데이트
                if (!seatDAO.updateAvailability(conn, seatId, false)) {
                    throw new IllegalStateException("좌석 상태 갱신 실패");
                }

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
        if (reservationOpt.isEmpty()) {
            return null;
        }
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