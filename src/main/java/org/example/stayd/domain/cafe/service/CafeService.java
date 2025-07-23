package org.example.stayd.domain.cafe.service;

import org.example.stayd.domain.cafe.dao.CafeDao;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.model.CafeModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// CafeService.java 파일 상단에 import 추가
import org.example.stayd.common.PerformanceMonitor;

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

            return new CafeDto.CreateResponse(cafeId, "Study cafe created successfully.", true);

        } catch (IllegalArgumentException e) {
            return new CafeDto.CreateResponse(null, e.getMessage(), false);
        } catch (SQLException e) {
            e.printStackTrace();
            return new CafeDto.CreateResponse(null, "database error: " + e.getMessage(), false);
        } catch (Exception e) {
            e.printStackTrace();
            return new CafeDto.CreateResponse(null, "An unexpected error: " + e.getMessage(), false);
        }
    }

    // CafeService.java에서 기존 validateCreateRequest와 validateUpdateRequest를
// 다음 코드로 교체하세요

    /**
     * 카페 생성 요청 검증
     * @param request 생성 요청
     * @throws IllegalArgumentException 검증 실패 시
     */
    private void validateCreateRequest(CafeDto.CreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("no cafe creation information.");
        }

        // 공통 검증 로직 호출
        validateCafeBasicInfo(
                request.getName(),
                request.getAddress(),
                request.getPricePerHour(),
                request.getDescription(),
                request.getPhoneNumber(),
                request.getOperatingDays(),
                request.getOperatingStartHour(),
                request.getOperatingEndHour(),
                100 // 생성 시 설명 최대 길이
        );
    }

    /**
     * 카페 수정 요청 검증
     * @param request 수정 요청
     * @throws IllegalArgumentException 검증 실패 시
     */
    private void validateUpdateRequest(CafeDto.UpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("No cafe modification information");
        }

        if (request.getCafeId() == null) {
            throw new IllegalArgumentException("No cafe id information.");
        }

        // 공통 검증 로직 호출
        validateCafeBasicInfo(
                request.getName(),
                request.getAddress(),
                request.getPricePerHour(),
                request.getDescription(),
                request.getPhoneNumber(),
                request.getOperatingDays(),
                request.getOperatingStartHour(),
                request.getOperatingEndHour(),
                200 // 수정 시 설명 최대 길이
        );
    }

    /**
     * 카페 기본 정보 공통 검증
     * @throws IllegalArgumentException 검증 실패 시
     */
    private void validateCafeBasicInfo(String name, String address, Integer pricePerHour,
                                       String description, String phoneNumber,
                                       List<String> operatingDays, Integer startHour,
                                       Integer endHour, int descriptionMaxLength) {

        validateCafeName(name);
        validateCafeAddress(address);
        validatePricePerHour(pricePerHour);
        validateDescription(description, descriptionMaxLength);
        validatePhoneNumber(phoneNumber);
        validateOperatingDays(operatingDays);
        validateOperatingHours(startHour, endHour);
    }

    private void validateCafeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("cafe name cannot be empty.");
        }
        if (name.trim().length() > 50) {
            throw new IllegalArgumentException("cafe name cannot exceed 50 characters.");
        }
    }
    private void validateCafeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("cafe address cannot be empty.");
        }
        if (address.trim().length() > 100) {
            throw new IllegalArgumentException("address cannot exceed 100 characters.");
        }
    }
    private void validatePricePerHour(Integer pricePerHour) {
        if (pricePerHour == null || pricePerHour < 1000) {
            throw new IllegalArgumentException("pricePerHour cannot be less than 1000.");
        }
    }
    private void validateDescription(String description, int maxLength) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("description cannot be empty.");
        }
        if (description.trim().length() > maxLength) {
            throw new IllegalArgumentException("description " + maxLength);
        }
    }
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number format. (ex: 02-123-4567, 010-1234-5678)");
        }
    }
    private void validateOperatingDays(List<String> operatingDays) {
        if (operatingDays == null || operatingDays.isEmpty()) {
            throw new IllegalArgumentException("Operating days cannot be empty.");
        }
    }
    private void validateOperatingHours(Integer startHour, Integer endHour) {
        if (startHour == null || endHour == null) {
            throw new IllegalArgumentException("Operating hours cannot be empty.");
        }
        if (startHour < 0 || startHour > 23) {
            throw new IllegalArgumentException("startHour must be between 0 and 23");
        }
        if (endHour < 0 || endHour > 23) {
            throw new IllegalArgumentException("endHour must be between 0 and 23");
        }
        if (startHour >= endHour) {
            throw new IllegalArgumentException("endHour cannot be greater than startHour");
        }
    }
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
            if (!day.isEmpty()) {
                operatingHours.add(new CafeDto.OperatingHours(day, startHour, endHour));
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
        return 5L; // 더미 카페 오너 ID
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
            throw new RuntimeException("Error checking cafe details: " + e.getMessage());
        }
    }


    /**
     * 모든 카페 목록 조회 (PL/SQL 함수 사용)
     * @param sortByRating true: 평점순, false: 최신순
     * @return 카페 DTO 목록
     */
    public List<CafeDto.SimpleCafeDto> getAllCafes(boolean sortByRating) {
        try {

            long totalStartTime = System.currentTimeMillis();

            // PL/SQL 함수로 전체 카페 조회
            List<CafeDto.SimpleCafeDto> results = cafeDao.findAllCafesWithPLSQL(sortByRating);

            long totalEndTime = System.currentTimeMillis();

            return results;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("PL/SQL Full Cafe Inquiry Failed: " + e.getMessage());
            throw new RuntimeException("Error cafe list: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error: : " + e.getMessage());
            throw new RuntimeException("Error cafe list: " + e.getMessage());
        }
    }

    /**
     * 카페 이름으로 검색 (PL/SQL 함수 사용)
     * @param keyword 검색 키워드
     * @param sortByRating true: 평점순, false: 최신순
     * @return 검색된 카페 DTO 목록
     */
    public List<CafeDto.SimpleCafeDto> searchCafesByName(String keyword, boolean sortByRating) {
        try {
            System.out.println("Start PL/SQL Cafe Search");
            System.out.println("search word: " + (keyword != null && !keyword.trim().isEmpty() ? keyword : "all"));
            System.out.println("sort: " + (sortByRating ? "평점" : "최신순"));

            long totalStartTime = System.currentTimeMillis();

            List<CafeDto.SimpleCafeDto> results;

            if (keyword == null || keyword.trim().isEmpty()) {
                // 전체 조회 (PL/SQL 함수)
                results = cafeDao.findAllCafesWithPLSQL(sortByRating);
            } else {
                // 키워드 검색 (PL/SQL 함수)
                results = cafeDao.searchCafesByNameWithPLSQL(keyword.trim(), sortByRating);
            }

            long totalEndTime = System.currentTimeMillis();

            return results;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("PL/SQL cafe search fail: " + e.getMessage());
            throw new RuntimeException("cafe search error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("unexpected error: " + e.getMessage());
            throw new RuntimeException("cafe seartch upexpected error: " + e.getMessage());
        }
    }

    /**
     * 카페 ID로 상세 정보 조회 (상세 페이지용)
     * @param cafeId 카페 ID
     * @return 카페 DTO (없으면 null)
     */
    public CafeDto.SimpleCafeDto getCafeById(int cafeId) {
//        return PerformanceMonitor.measureTimeWithResult("DB - Get Cafe By ID: " + cafeId, () -> {
            try {
                return cafeDao.findCafeById(cafeId);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error loading cafe detail: " + e.getMessage());
            }
//        });
    }

    /**
     * 찜하기 상태 업데이트 (나중에 구현)
     * @param cafeId 카페 ID
     * @param isFavorite 찜하기 상태
     */
    public void updateFavoriteStatus(int cafeId, boolean isFavorite) {
        // TODO: 찜하기 기능 구현 시 추가
        System.out.println("찜하기 기능은 추후 구현 예정 - 카페 ID: " + cafeId + ", 상태: " + isFavorite);
    }


    /**
     * 카페 정보 수정
     * @param request 수정 요청 정보
     * @param ownerId 카페 소유자 ID
     * @return 수정 결과
     */
    public CafeDto.UpdateResponse updateCafe(CafeDto.UpdateRequest request, Long ownerId) {
        try {
            // 입력 검증
            validateUpdateRequest(request);

            // 카페 존재 여부 및 소유자 확인
            if (!isOwnerOfCafe(request.getCafeId(), ownerId)) {
                return new CafeDto.UpdateResponse(false, "You can only modify the cafes you own.");
            }

            // 운영시간 생성
            List<CafeDto.OperatingHours> operatingHours = createOperatingHours(
                    request.getOperatingDays(),
                    request.getOperatingStartHour(),
                    request.getOperatingEndHour()
            );

            // 카페 정보 수정
            cafeDao.updateCafe(request, operatingHours);

            return new CafeDto.UpdateResponse(true, "cafe information has been successfully modified.");

        } catch (IllegalArgumentException e) {
            return new CafeDto.UpdateResponse(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new CafeDto.UpdateResponse(false, "error occurred while modifying the cafe: " + e.getMessage());
        }
    }

    /**
     * 카페 삭제
     * @param cafeId 삭제할 카페 ID
     * @param ownerId 카페 소유자 ID
     * @return 삭제 결과
     */
    public CafeDto.DeleteResponse deleteCafe(Long cafeId, Long ownerId) {
        try {
            // 카페 존재 여부 및 소유자 확인
            if (!isOwnerOfCafe(cafeId, ownerId)) {
                return new CafeDto.DeleteResponse(false, "You can only delete the cafes you own.");
            }

            // 카페 삭제 (관련 데이터도 함께 삭제)
            cafeDao.deleteCafe(cafeId, ownerId);

            return new CafeDto.DeleteResponse(true, "The cafe has been deleted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return new CafeDto.DeleteResponse(false, "Error deleting cafe: " + e.getMessage());
        }
    }

    /**
     * 카페 소유자 확인
     * @param cafeId 카페 ID
     * @param ownerId 소유자 ID
     * @return 소유자 여부
     */
    private boolean isOwnerOfCafe(Long cafeId, Long ownerId) {
        try {
            return cafeDao.isOwnerOfCafe(cafeId, ownerId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}