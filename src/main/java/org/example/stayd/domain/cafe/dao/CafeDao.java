package org.example.stayd.domain.cafe.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.model.CafeModel;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 스터디 카페 데이터 접근 객체
 */
public class CafeDao {

    private final DatabaseConnection databaseConnection;

    public CafeDao() {
        this.databaseConnection = new DatabaseConnection();
    }

    /**
     * 스터디 카페 생성 (트랜잭션으로 카페, 운영시간, 좌석 20개 모두 생성)
     * @param cafe 생성할 카페 정보
     * @param operatingHours 운영시간 목록
     * @return 생성된 카페 ID
     * @throws SQLException SQL 예외
     */
    public Long createCafe(CafeModel cafe, List<CafeDto.OperatingHours> operatingHours) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try {
            // 트랜잭션 시작
            connection.setAutoCommit(false);

            // 1. 카페 등록
            Long cafeId = insertCafe(connection, cafe);

            // 2. 운영시간 등록
            insertOperatingHours(connection, cafeId, operatingHours);

            // 3. 좌석 20개 생성 (A1~A10, B1~B10)
            createSeats(connection, cafeId);

            // 트랜잭션 커밋
            connection.commit();

            return cafeId;

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
     * 스터디 카페 등록
     * @param connection DB 연결
     * @param cafe 등록할 카페 정보
     * @return 등록된 카페 ID
     * @throws SQLException SQL 예외
     */
    public Long insertCafe(Connection connection, CafeModel cafe) throws SQLException {
        String sql = "INSERT INTO cafe (owner_id, name, address, price_per_hour, description, phone_number, image_url, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"CAFE_ID"})) {

            pstmt.setLong(1, cafe.getOwnerId());
            pstmt.setString(2, cafe.getName());
            pstmt.setString(3, cafe.getAddress());
            pstmt.setInt(4, cafe.getPricePerHour());
            pstmt.setString(5, cafe.getDescription());
            pstmt.setString(6, cafe.getPhoneNumber());
            pstmt.setString(7, cafe.getImageUrl());
            pstmt.setTimestamp(8, Timestamp.valueOf(cafe.getCreatedAt()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("카페 등록에 실패했습니다.");
            }

            // 생성된 키 가져오기
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("카페 ID를 가져올 수 없습니다.");
                }
            }
        }
    }

    /**
     * 운영시간 등록
     * @param connection DB 연결
     * @param cafeId 카페 ID
     * @param operatingHours 운영시간 목록
     * @throws SQLException SQL 예외
     */
    private void insertOperatingHours(Connection connection, Long cafeId, List<CafeDto.OperatingHours> operatingHours) throws SQLException {
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
     * 좌석 20개 생성 (A1~A10, B1~B10)
     * @param connection DB 연결
     * @param cafeId 카페 ID
     * @throws SQLException SQL 예외
     */
    private void createSeats(Connection connection, Long cafeId) throws SQLException {
        String sql = "INSERT INTO seat (cafe_id, seat_number, is_available, created_at) VALUES (?, ?, 'Y', ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();

            // 1~20 생성
            for (int i = 1; i <= 20; i++) {
                pstmt.setLong(1, cafeId);
                pstmt.setString(2, ""+i);
                pstmt.setTimestamp(3, Timestamp.valueOf(now));
                pstmt.addBatch();
            }


            pstmt.executeBatch();
        }
    }

    /**
     * 요일 문자열을 DB 형식으로 변환 (한글 그대로 저장)
     * @param dayInKorean 한글 요일 (월, 화, 수, 목, 금, 토, 일)
     * @return 한글 요일 그대로 반환
     */
    public static String convertDayToDbFormat(String dayInKorean) {
        // 한글 그대로 반환 (변환하지 않음)
        return dayInKorean;
    }

    /**
     * 카페 ID로 카페 단건 조회
     * @param cafeId 조회할 카페 ID
     * @return CafeModel 객체 (없으면 null)
     */
    public CafeDto.DetailResponse findById(Long cafeId) throws SQLException {
        // 🔍 디버깅 로그 추가
        System.out.println("=== DAO 디버깅 ===");
        System.out.println("조회할 카페 ID: " + cafeId);

        String cafeSql = "SELECT * FROM cafe WHERE cafe_id = ?";
        String opSql = "SELECT * FROM operation_hours WHERE cafe_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement cafeStmt = conn.prepareStatement(cafeSql);
             PreparedStatement opStmt = conn.prepareStatement(opSql)) {

            cafeStmt.setLong(1, cafeId);
            ResultSet cafeRs = cafeStmt.executeQuery();

            // 🔍 결과 확인
            System.out.println("SQL 쿼리 실행: " + cafeSql);
            System.out.println("파라미터: " + cafeId);

            if (!cafeRs.next()) {
                System.out.println("결과 없음");
                throw new SQLException("해당 ID의 카페를 찾을 수 없습니다.");
            }

            System.out.println("✅ 카페 찾음!");

            // 카페 기본 정보 추출
            String name = cafeRs.getString("name");
            String address = cafeRs.getString("address");
            Integer pricePerHour = cafeRs.getInt("price_per_hour");
            String description = cafeRs.getString("description");
            String phone = cafeRs.getString("phone_number");
            String imageUrl = cafeRs.getString("image_url");

            // 운영시간 추출
            opStmt.setLong(1, cafeId);
            ResultSet opRs = opStmt.executeQuery();

            List<String> days = new ArrayList<>();
            Integer start = null;
            Integer end = null;

            while (opRs.next()) {
                days.add(opRs.getString("day_of_week"));
                if (start == null) start = opRs.getInt("operation_start");
                if (end == null) end = opRs.getInt("operation_end");
            }

            return new CafeDto.DetailResponse(cafeId, name, address, pricePerHour, description, phone, imageUrl, days, start, end);
        }
    }


    // CafeDao.java 파일에 다음 메서드들을 추가하세요

    /**
     * 모든 카페 목록 조회 (리스트용)
     */
    public List<CafeDto.SimpleCafeDto> findAllCafes() throws SQLException {
        String sql = """
        SELECT c.cafe_id, c.name, c.address, c.price_per_hour, c.description, 
               c.phone_number, c.image_url,
               0.0 as avg_rating,
               0 as review_count
        FROM cafe c
        ORDER BY c.cafe_id DESC
        """;

        List<CafeDto.SimpleCafeDto> cafes = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
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
                cafe.setFavorite(false); // 기본값 (찜하기 기능 구현 시 수정)

                cafes.add(cafe);
            }
        }

        return cafes;
    }

    /**
     * 카페 이름으로 검색
     */
    public List<CafeDto.SimpleCafeDto> searchCafesByName(String keyword) throws SQLException {
        String sql = """
        SELECT c.cafe_id, c.name, c.address, c.price_per_hour, c.description, 
               c.phone_number, c.image_url,
               0.0 as avg_rating,
               0 as review_count
        FROM cafe c
        WHERE c.name LIKE ?
        ORDER BY c.cafe_id DESC
        """;

        List<CafeDto.SimpleCafeDto> cafes = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
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

                    cafes.add(cafe);
                }
            }
        }

        return cafes;
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
     * 운영시간 문자열 조회
     */
    private String getOperatingHoursString(int cafeId) throws SQLException {
        String sql = "SELECT day_of_week, operation_start, operation_end FROM operation_hours WHERE cafe_id = ? ORDER BY day_of_week";

        StringBuilder sb = new StringBuilder();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cafeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (sb.length() > 0) sb.append(",");
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

    /**
     * 운영일 추출 (월,화,수,목,금)
     */
    private String extractOperatingDays(String operatingInfo) {
        if (operatingInfo == null || operatingInfo.isEmpty()) return "";

        StringBuilder days = new StringBuilder();
        String[] parts = operatingInfo.split(",");

        for (String part : parts) {
            String[] dayHour = part.split(":");
            if (dayHour.length >= 1) {
                if (days.length() > 0) days.append(",");
                days.append(dayHour[0].trim()); // 한글 요일 그대로 사용
            }
        }

        return days.toString();
    }

    /**
     * 운영시간 추출 (09:00-18:00)
     */
    private String extractOperatingHours(String operatingInfo) {
        if (operatingInfo == null || operatingInfo.isEmpty()) return "";

        String[] parts = operatingInfo.split(",");
        if (parts.length > 0) {
            String[] dayHour = parts[0].split(":");
            if (dayHour.length >= 2) {
                return dayHour[1]; // 첫 번째 운영시간 반환
            }
        }

        return "";
    }

}