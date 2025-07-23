package org.example.stayd.domain.discount.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.SessionManager;

import java.sql.*;

public class DiscountDAO {
    private Connection connection;

    public DiscountDAO() {
        this.connection = new DatabaseConnection().getConnection();
    }

    // 할인 설정 정보를 DB에 저장
    public boolean saveDiscountSettings(Date selectedDate, int startTime, int endTime, double discountRate) {
        String query = "INSERT INTO discount_hours (cafe_id, day_of_week, discount_start, discount_end, discount_rate) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 현재 로그인한 사용자의 cafe_id를 동적으로 가져오기
            int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();  // 로그인한 사용자의 user_id를 가져옴
            int cafeId = getCafeIdByUserId(userId);  // 해당 user_id로 카페 ID를 가져오는 메서드 호출

            if (cafeId == -1) {
                System.out.println("카페 ID를 찾을 수 없습니다.");
                return false;  // 카페 ID가 없으면 false 반환
            }
            String dayOfWeek = selectedDate.toLocalDate().getDayOfWeek().name().substring(0, 3);  // 예: MON, TUE 등

            stmt.setInt(1, cafeId);
            stmt.setString(2, dayOfWeek);
            stmt.setInt(3, startTime);
            stmt.setInt(4, endTime);
            stmt.setDouble(5, discountRate);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // 성공하면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 카페 ID를 가져오는 메서드
    private int getCafeIdByUserId(int userId) {
        String query = "SELECT cafe_id FROM cafe WHERE owner_id = ?";  // user_id로 카페 ID 찾기

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cafe_id");  // 카페 ID 반환
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;  // 카페 ID를 찾을 수 없으면 -1 반환
    }
}