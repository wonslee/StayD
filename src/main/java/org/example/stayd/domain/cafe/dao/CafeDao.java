package org.example.stayd.domain.cafe.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.DayOfWeekConverter;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.model.CafeModel;
import org.example.stayd.domain.cafe.model.DayOfWeek;
import org.example.stayd.domain.cafe.model.DiscountHours;
import org.example.stayd.domain.cafe.model.OperationHours;

/**
 * 스터디 카페 데이터 접근 객체
 */
public class CafeDao {

    private final DatabaseConnection databaseConnection;

    public CafeDao() {
        this.databaseConnection = new DatabaseConnection();
    }

    /**
     * 스터디 카페 생성 (PL/SQL 프로시저 사용 - 수정 버전)
     *
     * @param cafe           생성할 카페 정보
     * @param operatingHours 운영시간 목록
     * @return 생성된 카페 ID
     * @throws SQLException SQL 예외
     */
    public Long createCafe(CafeModel cafe, List<CafeDto.OperatingHours> operatingHours) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try {
            System.out.println(" PL/SQL Start creating a cafe with a procedure");

            // PL/SQL 프로시저 호출 (파라미터 11개)
            String sql = "{ call create_study_cafe(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

            try (CallableStatement cstmt = connection.prepareCall(sql)) {

                // 입력 파라미터 설정
                cstmt.setLong(1, cafe.getOwnerId());
                cstmt.setString(2, cafe.getName());
                cstmt.setString(3, cafe.getAddress());
                cstmt.setInt(4, cafe.getPricePerHour());
                cstmt.setString(5, cafe.getDescription());
                cstmt.setString(6, cafe.getPhoneNumber());
                cstmt.setString(7, cafe.getImageUrl());

                //  운영일을 문자열로 변환 ("월,화,수,목,금")
                String operatingDaysString = createOperatingDaysString(operatingHours);
                cstmt.setString(8, operatingDaysString);
                System.out.println("🗓 operatingDaysString: " + operatingDaysString);

                // 운영시간 설정 (첫 번째 운영시간 사용)
                if (!operatingHours.isEmpty()) {
                    cstmt.setInt(9, operatingHours.get(0).getOperationStart());
                    cstmt.setInt(10, operatingHours.get(0).getOperationEnd());
                    System.out.println("operatingHours: " + operatingHours.get(0).getOperationStart() + ":00 - "
                            + operatingHours.get(0).getOperationEnd() + ":00");
                } else {
                    throw new SQLException("Operating time is not set.");
                }

                // 출력 파라미터 설정
                cstmt.registerOutParameter(11, Types.NUMERIC); // p_cafe_id
                cstmt.registerOutParameter(12, Types.NUMERIC); // p_result

                // 프로시저 실행 및 성능 측정
                long startTime = System.currentTimeMillis();
                cstmt.execute();
                long endTime = System.currentTimeMillis();

                // 결과 확인
                int result = cstmt.getInt(12);
                if (result == 0) {
                    // 성공
                    Long cafeId = cstmt.getLong(11);
                    return cafeId;
                } else {
                    // 실패
                    System.out.println(" PL/SQL fail: " + result + ")");
                    throw new SQLException("create cafe failed.");
                }
            }

        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 운영시간 목록을 영어 요일 문자열로 변환 ("MON,TUE,WED,THU,FRI")
     *
     * @param operatingHours 운영시간 목록 (한글 요일 포함)
     * @return 쉼표로 구분된 영어 요일 문자열
     */
    private String createOperatingDaysString(List<CafeDto.OperatingHours> operatingHours) {
        if (operatingHours == null || operatingHours.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operatingHours.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }

            // 한글 요일을 영어 요일로 변환
            String koreanDay = operatingHours.get(i).getDayOfWeek();
            String englishDay = DayOfWeekConverter.toEnglish(koreanDay);
            sb.append(englishDay);

            System.out.println("요일 변환: " + koreanDay + " → " + englishDay);
        }

        String result = sb.toString();
        System.out.println("영어 운영일 문자열 생성: " + result);
        return result;
    }


    /**
     * 운영시간 등록
     *
     * @param connection     DB 연결
     * @param cafeId         카페 ID
     * @param operatingHours 운영시간 목록
     * @throws SQLException SQL 예외
     */
    private void insertOperatingHours(Connection connection, Long cafeId, List<CafeDto.OperatingHours> operatingHours)
            throws SQLException {
        String sql = "INSERT INTO OPERATION_HOURS (cafe_id, day_of_week, operation_start, operation_end, created_at) VALUES (?, ?, ?, ?, ?)";
        System.out.println(">>> 연결된 사용자: " + connection.getMetaData().getUserName());
        System.out.println(">>> INSERT SQL: " + sql);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            for (CafeDto.OperatingHours hours : operatingHours) {
                pstmt.setLong(1, cafeId);
                pstmt.setString(2, hours.getDayOfWeek());
                pstmt.setInt(3, hours.getOperationStart());
                pstmt.setInt(4, hours.getOperationEnd());
                pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }

    /**
     * 요일 문자열을 DB 형식으로 변환 (한글 그대로 저장)
     *
     * @param dayInKorean 한글 요일 (월, 화, 수, 목, 금, 토, 일)
     * @return 한글 요일 그대로 반환
     */
    public static String convertDayToDbFormat(String dayInKorean) {
        // 한글 그대로 반환 (변환하지 않음)
        return dayInKorean;
    }

    /**
     * 카페 ID로 카페 단건 조회 (요일 순서 정렬 버전)
     */
    public CafeDto.DetailResponse findById(Long cafeId) throws SQLException {
        String cafeSql = "SELECT * FROM cafe WHERE cafe_id = ?";

        // 요일 순서를 보장하는 SQL (월요일부터 일요일 순서)
        String opSql = """
                SELECT * FROM operation_hours 
                WHERE cafe_id = ? 
                ORDER BY 
                    CASE day_of_week 
                        WHEN 'MON' THEN 1
                        WHEN 'TUE' THEN 2  
                        WHEN 'WED' THEN 3
                        WHEN 'THU' THEN 4
                        WHEN 'FRI' THEN 5
                        WHEN 'SAT' THEN 6
                        WHEN 'SUN' THEN 7
                    END
                """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement cafeStmt = conn.prepareStatement(cafeSql);
             PreparedStatement opStmt = conn.prepareStatement(opSql)) {

            cafeStmt.setLong(1, cafeId);
            ResultSet cafeRs = cafeStmt.executeQuery();

            System.out.println("SQL query: " + cafeSql);
            System.out.println("Parameter: " + cafeId);

            if (!cafeRs.next()) {
                System.out.println("No result");
                throw new SQLException("Not find cafe with id: " + cafeId);
            }

            // 카페 기본 정보 추출
            String name = cafeRs.getString("name");
            String address = cafeRs.getString("address");
            Integer pricePerHour = cafeRs.getInt("price_per_hour");
            String description = cafeRs.getString("description");
            String phone = cafeRs.getString("phone_number");
            String imageUrl = cafeRs.getString("image_url");

            // 운영시간 추출 (영어 → 한글 변환, 순서 보장됨)
            opStmt.setLong(1, cafeId);
            ResultSet opRs = opStmt.executeQuery();

            List<String> koreanDays = new ArrayList<>();
            Integer start = null;
            Integer end = null;

            while (opRs.next()) {
                String englishDay = opRs.getString("day_of_week");
                String koreanDay = DayOfWeekConverter.toKorean(englishDay);
                koreanDays.add(koreanDay);

                System.out.println("DB에서 조회된 요일 변환 (순서대로): " + englishDay + " → " + koreanDay);

                if (start == null) {
                    start = opRs.getInt("operation_start");
                }
                if (end == null) {
                    end = opRs.getInt("operation_end");
                }
            }

            return new CafeDto.DetailResponse(cafeId, name, address, pricePerHour, description, phone, imageUrl,
                    koreanDays, start, end);
        }
    }


    /**
     * 카페 ID로 상세 정보 조회 (운영시간 포함)
     */
    public CafeDto.SimpleCafeDto findCafeById(int cafeId) throws SQLException {
        String sql = """
                SELECT c.cafe_id, c.name, c.address, c.price_per_hour, c.description, 
                       c.phone_number, c.image_url,
                       0.0 as avg_rating,
                       0 as review_count
                FROM cafe c
                WHERE c.cafe_id = ?
                """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cafeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CafeDto.SimpleCafeDto cafe = new CafeDto.SimpleCafeDto();
                    cafe.setId(rs.getInt("cafe_id"));
                    cafe.setName(rs.getString("name"));
                    cafe.setAddress(rs.getString("address"));
                    cafe.setRating(rs.getDouble("avg_rating"));
                    cafe.setReviewCount(rs.getInt("review_count"));
                    cafe.setImageUrl(rs.getString("image_url"));
                    cafe.setPricePerHour(rs.getInt("price_per_hour"));
                    cafe.setDescription(rs.getString("description"));
                    cafe.setPhoneNumber(rs.getString("phone_number"));
                    cafe.setFavorite(false); // 기본값

                    // 운영시간 조회
                    String operatingInfo = getOperatingHoursString(cafeId);
                    cafe.setOperatingDays(extractOperatingDays(operatingInfo));
                    cafe.setOperatingHours(extractOperatingHours(operatingInfo));

                    return cafe;
                }
            }
        }

        return null;
    }

    /**
     * 카페 검색 (PL/SQL 함수 사용)
     *
     * @param keyword      검색 키워드 (null이면 전체 조회)
     * @param sortByRating true: 평점순, false: 최신순
     * @param pageNum      페이지 번호 (1부터 시작)
     * @param pageSize     페이지 크기 (기본 8개)
     * @return 검색된 카페 목록
     * @throws SQLException SQL 예외
     */
    public List<CafeDto.SimpleCafeDto> searchCafesWithPLSQL(String keyword, boolean sortByRating,
                                                            int pageNum, int pageSize) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        List<CafeDto.SimpleCafeDto> cafes = new ArrayList<>();

        try {

            // PL/SQL 함수 호출
            String sql = "{ ? = call search_cafes_advanced(?, ?, ?, ?) }";

            try (CallableStatement cstmt = connection.prepareCall(sql)) {

                // 출력 파라미터 (커서)
                cstmt.registerOutParameter(1, Types.REF_CURSOR);

                // 입력 파라미터 설정
                cstmt.setString(2, keyword); // null 허용
                cstmt.setString(3, sortByRating ? "RATING" : "LATEST");
                cstmt.setInt(4, pageNum);
                cstmt.setInt(5, pageSize);

                // 함수 실행 및 성능 측정
                long startTime = System.currentTimeMillis();
                cstmt.execute();
                long endTime = System.currentTimeMillis();

                // 커서에서 결과 읽기
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) {
                        CafeDto.SimpleCafeDto cafe = new CafeDto.SimpleCafeDto();

                        // 기본 정보 설정
                        cafe.setId(rs.getInt("cafe_id"));
                        cafe.setName(rs.getString("name"));
                        cafe.setAddress(rs.getString("address"));
                        cafe.setPricePerHour(rs.getInt("price_per_hour"));
                        cafe.setDescription(rs.getString("description"));
                        cafe.setPhoneNumber(rs.getString("phone_number"));
                        cafe.setImageUrl(rs.getString("image_url"));

                        // 평점 정보 설정
                        cafe.setRating(rs.getDouble("avg_rating"));
                        cafe.setReviewCount(rs.getInt("review_count"));

                        // 기본값 설정
                        cafe.setFavorite(false); // TODO: 찜하기 기능 연동 시 수정

                        cafes.add(cafe);
                    }
                }
            }

        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return cafes;
    }

    /**
     * 전체 카페 조회 (PL/SQL 함수 사용)
     *
     * @param sortByRating true: 평점순, false: 최신순
     * @return 카페 목록
     * @throws SQLException SQL 예외
     */
    public List<CafeDto.SimpleCafeDto> findAllCafesWithPLSQL(boolean sortByRating) throws SQLException {
        // 첫 번째 페이지의 큰 사이즈로 전체 조회
        return searchCafesWithPLSQL(null, sortByRating, 1, 100);
    }

    /**
     * 카페 이름으로 검색 (PL/SQL 함수 사용)
     *
     * @param keyword      검색 키워드
     * @param sortByRating true: 평점순, false: 최신순
     * @return 검색된 카페 목록
     * @throws SQLException SQL 예외
     */
    public List<CafeDto.SimpleCafeDto> searchCafesByNameWithPLSQL(String keyword, boolean sortByRating)
            throws SQLException {
        // 첫 번째 페이지의 큰 사이즈로 검색 (실제로는 페이징 처리 권장)
        return searchCafesWithPLSQL(keyword, sortByRating, 1, 100);
    }

    /**
     * 운영시간 문자열 조회 (요일 순서 정렬)
     */
    private String getOperatingHoursString(int cafeId) throws SQLException {
        String sql = """
                SELECT day_of_week, operation_start, operation_end 
                FROM operation_hours 
                WHERE cafe_id = ? 
                ORDER BY 
                    CASE day_of_week 
                        WHEN 'MON' THEN 1
                        WHEN 'TUE' THEN 2  
                        WHEN 'WED' THEN 3
                        WHEN 'THU' THEN 4
                        WHEN 'FRI' THEN 5
                        WHEN 'SAT' THEN 6
                        WHEN 'SUN' THEN 7
                    END
                """;

        StringBuilder sb = new StringBuilder();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cafeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(rs.getString("day_of_week"))
                            .append(":")
                            .append(String.format("%02d:00-%02d:00",
                                    rs.getInt("operation_start"),
                                    rs.getInt("operation_end")));
                }
            }
        }

        return sb.toString();
    }

    // extractOperatingDays 메서드도 수정

    /**
     * 운영일 추출 (영어 → 한글 변환)
     */
    private String extractOperatingDays(String operatingInfo) {
        if (operatingInfo == null || operatingInfo.isEmpty()) {
            return "";
        }

        StringBuilder koreanDays = new StringBuilder();
        String[] parts = operatingInfo.split(",");

        for (String part : parts) {
            String[] dayHour = part.split(":");
            if (dayHour.length >= 1) {
                if (koreanDays.length() > 0) {
                    koreanDays.append(",");
                }

                String englishDay = dayHour[0].trim();
                String koreanDay = DayOfWeekConverter.toKorean(englishDay);
                koreanDays.append(koreanDay);
            }
        }

        return koreanDays.toString();
    }

    /**
     * 운영시간 추출
     */
    private String extractOperatingHours(String operatingInfo) {
        System.out.println("=== extractOperatingHours 수정 버전 ===");
        System.out.println("입력된 operatingInfo: " + operatingInfo);

        if (operatingInfo == null || operatingInfo.isEmpty()) {
            System.out.println("operatingInfo가 null이거나 비어있음");
            return "";
        }

        String[] parts = operatingInfo.split(",");
        System.out.println("쉼표로 분리된 parts 개수: " + parts.length);

        if (parts.length > 0) {
            String firstPart = parts[0].trim(); // "FRI:05:00-20:00"
            System.out.println("첫 번째 part: " + firstPart);

            // 첫 번째 콜론의 위치를 찾아서 요일 부분을 제거
            int colonIndex = firstPart.indexOf(":");
            if (colonIndex > 0 && colonIndex < firstPart.length() - 1) {
                String timeRange = firstPart.substring(colonIndex + 1); // "05:00-20:00"
                System.out.println("추출된 운영시간: " + timeRange);
                System.out.println("=== extractOperatingHours 완료 ===");
                return timeRange;
            } else {
                System.out.println("콜론을 찾을 수 없거나 잘못된 형식");
            }
        } else {
            System.out.println("parts 배열이 비어있음");
        }

        System.out.println("=== extractOperatingHours 실패 ===");
        return "";
    }

    /**
     * 카페 정보 수정
     *
     * @param request        수정 요청 정보
     * @param operatingHours 운영시간 목록
     * @throws SQLException SQL 예외
     */
    public void updateCafe(CafeDto.UpdateRequest request, List<CafeDto.OperatingHours> operatingHours)
            throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try {
            // 트랜잭션 시작
            connection.setAutoCommit(false);

            // 1. 카페 정보 수정
            updateCafeInfo(connection, request);

            // 2. 기존 운영시간 삭제
            deleteOperatingHours(connection, request.getCafeId());

            // 3. 새로운 운영시간 등록
            insertOperatingHours(connection, request.getCafeId(), operatingHours);

            // 트랜잭션 커밋
            connection.commit();

        } catch (SQLException e) {
            // 롤백
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 카페 정보만 수정
     *
     * @param connection DB 연결
     * @param request    수정 요청
     * @throws SQLException SQL 예외
     */
    private void updateCafeInfo(Connection connection, CafeDto.UpdateRequest request) throws SQLException {
        String sql = """
                UPDATE cafe 
                SET name = ?, address = ?, price_per_hour = ?, description = ?, 
                    phone_number = ?, image_url = ?
                WHERE cafe_id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, request.getName().trim());
            pstmt.setString(2, request.getAddress().trim());
            pstmt.setInt(3, request.getPricePerHour());
            pstmt.setString(4, request.getDescription().trim());
            pstmt.setString(5, request.getPhoneNumber().trim());
            pstmt.setString(6, request.getImageUrl() != null ? request.getImageUrl().trim() : null);
            pstmt.setLong(7, request.getCafeId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("카페 정보 수정에 실패했습니다.");
            }
        }
    }


    /**
     * 기존 운영시간 삭제
     *
     * @param connection DB 연결
     * @param cafeId     카페 ID
     * @throws SQLException SQL 예외
     */
    private void deleteOperatingHours(Connection connection, Long cafeId) throws SQLException {
        String sql = "DELETE FROM operation_hours WHERE cafe_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, cafeId);
            pstmt.executeUpdate();
        }
    }

    /**
     * 카페 삭제 (PL/SQL 프로시저 사용)
     *
     * @param cafeId  삭제할 카페 ID
     * @param ownerId 소유자 ID
     * @throws SQLException SQL 예외
     */
    public void deleteCafe(Long cafeId, Long ownerId) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try {
            System.out.println(" PL/SQL deleteCafe Start");
            System.out.println("   CAFE ID: " + cafeId + ", OWNER ID: " + ownerId);

            // PL/SQL 프로시저 호출
            String sql = "{ call delete_study_cafe(?, ?, ?) }";

            try (CallableStatement cstmt = connection.prepareCall(sql)) {

                // 입력 파라미터 설정
                cstmt.setLong(1, cafeId);
                cstmt.setLong(2, ownerId);

                // 출력 파라미터 설정
                cstmt.registerOutParameter(3, Types.NUMERIC); // p_result

                // 프로시저 실행 및 성능 측정
                long startTime = System.currentTimeMillis();
                cstmt.execute();
                long endTime = System.currentTimeMillis();

                // 결과 확인
                int result = cstmt.getInt(3);

                System.out.println(" PL/SQL 프로시저 실행 시간: " + (endTime - startTime) + "ms");

                switch (result) {
                    case 0:
                        // 성공
                        System.out.println(" PL/SQL Cafe delete!");
                        break;

                    case 1:
                        // 권한 없음
                        System.out.println(" No Owner");
                        throw new SQLException("No Owner");

                    case 3:
                        // 카페 없음
                        System.out.println(" Not Exist");
                        throw new SQLException("Not Exist");

                    case 2:
                    default:
                        // 일반 실패
                        System.out.println(" PL/SQL fail: " + result + ")");
                        throw new SQLException("cafe delete fail");
                }
            }

        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 카페 소유자 확인
     *
     * @param cafeId 카페 ID
     * @param userId 사용자 ID
     * @return 소유자 여부
     * @throws SQLException SQL 예외
     */
    public boolean isOwnerOfCafe(Long cafeId, Long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cafe WHERE cafe_id = ? AND owner_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cafeId);
            pstmt.setLong(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public List<OperationHours> getOperationHours(long cafeId) throws SQLException {
        String sql = "SELECT day_of_week, operation_start, operation_end FROM operation_hours WHERE cafe_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, cafeId);
            ResultSet rs = pstmt.executeQuery();
            List<OperationHours> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new OperationHours(
                        DayOfWeek.from(rs.getString("day_of_week")),
                        rs.getInt("operation_start"),
                        rs.getInt("operation_end")
                ));
            }
            return result;
        }
    }

    public List<DiscountHours> getDiscountHours(long cafeId) throws SQLException {
        String sql = "SELECT day_of_week, discount_start, discount_end, discount_rate FROM discount_hours WHERE cafe_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, cafeId);
            ResultSet rs = pstmt.executeQuery();
            List<DiscountHours> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new DiscountHours(
                        DayOfWeek.from(rs.getString("day_of_week")),
                        rs.getInt("discount_start"),
                        rs.getInt("discount_end"),
                        rs.getInt("discount_rate")
                ));
            }
            return result;
        }
    }
}