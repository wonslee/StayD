package org.example.stayd.domain.cafe.service;

import org.example.stayd.domain.cafe.dao.CafeDao;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.model.CafeModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 스터디 카페 생성 비즈니스 로직 서비스
 */
public class CafeService {

    private final CafeDao cafeDao;

    public CafeService() {
        this.cafeDao = new CafeDao();
    }

    // 테스트용 생성자 (DAO 주입)
    public CafeService(CafeDao cafeDao) {
        this.cafeDao = cafeDao;
    }

    /**
     * 스터디 카페 생성
     * @param request 카페 생성 요청 정보
     * @param ownerId 카페 소유자 ID (현재는 더미 데이터)
     * @return 생성 결과
     */
    public CafeDto.CreateResponse createCafe(CafeDto.CreateRequest request, Long ownerId) {
        try {
            // 입력 검증
            validateCreateRequest(request);

            // TODO: 사용자 권한 확인 (CAFE_OWNER 권한인지 확인)
            // validateCafeOwnerPermission(ownerId);

            // 카페 모델 생성
            CafeModel cafe = new CafeModel(
                    ownerId,
                    request.getName().trim(),
                    request.getAddress().trim(),
                    request.getPricePerHour(),
                    request.getDescription().trim(),
                    request.getPhoneNumber().trim(),
                    request.getImageUrl() != null ? request.getImageUrl().trim() : null
            );

            // 운영시간 생성
            List<CafeDto.OperatingHours> operatingHours = createOperatingHours(
                    request.getOperatingDays(),
                    request.getOperatingStartHour(),
                    request.getOperatingEndHour()
            );

            // 카페 생성 (카페 + 운영시간 + 좌석 20개)
            Long cafeId = cafeDao.createCafe(cafe, operatingHours);

            return new CafeDto.CreateResponse(cafeId, "스터디 카페가 성공적으로 생성되었습니다.", true);

        } catch (IllegalArgumentException e) {
            return new CafeDto.CreateResponse(null, e.getMessage(), false);
        } catch (SQLException e) {
            e.printStackTrace();
            return new CafeDto.CreateResponse(null, "데이터베이스 오류가 발생했습니다: " + e.getMessage(), false);
        } catch (Exception e) {
            e.printStackTrace();
            return new CafeDto.CreateResponse(null, "예상치 못한 오류가 발생했습니다: " + e.getMessage(), false);
        }
    }

    /**
     * 카페 생성 요청 검증
     * @param request 생성 요청
     * @throws IllegalArgumentException 검증 실패 시
     */
    private void validateCreateRequest(CafeDto.CreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("카페 생성 정보가 없습니다.");
        }

        // 카페 이름 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("카페 이름을 입력해주세요.");
        }
        if (request.getName().trim().length() > 50) {
            throw new IllegalArgumentException("카페 이름은 50자 이하로 입력해주세요.");
        }

        // 주소 검증
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("카페 주소를 입력해주세요.");
        }
        if (request.getAddress().trim().length() > 100) {
            throw new IllegalArgumentException("주소는 100자 이하로 입력해주세요.");
        }

        // 시간당 가격 검증
        if (request.getPricePerHour() == null || request.getPricePerHour() < 1000) {
            throw new IllegalArgumentException("시간당 가격은 1000원 이상이어야 합니다.");
        }

        // 설명 검증
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("카페 설명을 입력해주세요.");
        }
        if (request.getDescription().trim().length() > 100) {
            throw new IllegalArgumentException("설명은 100자 이하로 입력해주세요.");
        }

        // 전화번호 검증
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("전화번호를 입력해주세요.");
        }
        if (!isValidPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("올바른 전화번호 형식이 아닙니다. (예: 02-123-4567, 010-1234-5678)");
        }

        // 영업일 검증
        if (request.getOperatingDays() == null || request.getOperatingDays().isEmpty()) {
            throw new IllegalArgumentException("영업일을 선택해주세요.");
        }

        // 운영시간 검증
        if (request.getOperatingStartHour() == null || request.getOperatingEndHour() == null) {
            throw new IllegalArgumentException("운영시간을 설정해주세요.");
        }
        if (request.getOperatingStartHour() < 0 || request.getOperatingStartHour() > 23) {
            throw new IllegalArgumentException("시작 시간은 0~23시 사이여야 합니다.");
        }
        if (request.getOperatingEndHour() < 0 || request.getOperatingEndHour() > 23) {
            throw new IllegalArgumentException("종료 시간은 0~23시 사이여야 합니다.");
        }
        if (request.getOperatingStartHour() >= request.getOperatingEndHour()) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }
    }

    /**
     * 전화번호 형식 검증
     * @param phoneNumber 전화번호
     * @return 유효성 여부
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;

        // 하이픈 제거 후 검증
        String cleanNumber = phoneNumber.replaceAll("-", "");

        // 010으로 시작하는 11자리 또는 02,031,032 등으로 시작하는 9-10자리
        return cleanNumber.matches("^(010\\d{8}|0[2-9]\\d{7,8})$");
    }

    /**
     * 운영시간 목록 생성
     * @param operatingDays 영업일 목록 (한글)
     * @param startHour 시작 시간
     * @param endHour 종료 시간
     * @return 운영시간 DTO 목록
     */
    private List<CafeDto.OperatingHours> createOperatingHours(List<String> operatingDays,
                                                              Integer startHour, Integer endHour) {
        List<CafeDto.OperatingHours> operatingHours = new ArrayList<>();

        for (String day : operatingDays) {
            String dbDay = CafeDao.convertDayToDbFormat(day.trim());
            if (!dbDay.isEmpty()) {
                operatingHours.add(new CafeDto.OperatingHours(dbDay, startHour, endHour));
            }
        }

        return operatingHours;
    }

    /**
     * 더미 사용자 ID 반환 (사용자 모듈 완성 전까지 사용)
     * CAFE_OWNER 권한을 가진 더미 사용자
     * @return 더미 사용자 ID
     */
    public Long getDummyOwnerId() {
        // TODO: 실제 사용자 모듈 완성 후 제거
        return 1L; // 더미 카페 오너 ID
    }

    /**
     * 카페 오너 권한 검증 (추후 구현)
     * @param userId 사용자 ID
     * @throws IllegalArgumentException 권한이 없는 경우
     */
    private void validateCafeOwnerPermission(Long userId) {
        // TODO: 사용자 서비스와 연동하여 CAFE_OWNER 권한 확인
        // UserService를 통해 사용자 역할 확인
        // if (!userService.hasRole(userId, "CAFE_OWNER")) {
        //     throw new IllegalArgumentException("카페 생성 권한이 없습니다. CAFE_OWNER 권한이 필요합니다.");
        // }
    }

    /**
     * 카페 ID로 단건 조회
     * @param cafeId 조회할 카페 ID
     * @return CafeModel (없으면 null)
     */
    public CafeDto.DetailResponse getCafeDetail(Long cafeId) {
        try {
            return cafeDao.findById(cafeId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("카페 상세 정보 조회 중 오류 발생: " + e.getMessage());
        }
    }

}