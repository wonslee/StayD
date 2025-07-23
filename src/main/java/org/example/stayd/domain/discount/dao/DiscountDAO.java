package org.example.stayd.domain.discount.dao;

import org.example.stayd.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DiscountDAO {
    private Connection connection;

    public DiscountDAO() {
        this.connection = new DatabaseConnection().getConnection();
    }

    // 할인 설정 정보를 DB에 저장
    public boolean saveDiscountSettings(Date selectedDate, int startTime, int endTime, double discountRate) {
        String query = "INSERT INTO discount_hours (cafe_id, day_of_week, discount_start, discount_end, discount_rate) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // cafe_id, day_of_week은 다른 방식으로 받아야 할 수 있습니다.
            int cafeId = 1;  // 임시로 cafe_id를 1로 설정
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
}