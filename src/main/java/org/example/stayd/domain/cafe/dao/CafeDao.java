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


}