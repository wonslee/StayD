package org.example.stayd.domain.discount.dao;

import javafx.scene.control.Alert;
import javafx.stage.Window;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.common.FXUtils;
import org.example.stayd.common.SessionManager;
import org.example.stayd.domain.discount.dto.DiscountDTO;

import java.sql.*;

public class DiscountDAO {
    private Connection connection;

    public DiscountDAO() {
        this.connection = new DatabaseConnection().getConnection();
    }

    /**
     * 할인 설정 정보를 DB에 저장하는 메서드
     *
     * @param discountDTO 할인 설정 정보 DTO
     * @return 성공 여부
     */
    public boolean saveDiscountSettings(DiscountDTO discountDTO) {
        String query = "INSERT INTO discount_hours (cafe_id, day_of_week, discount_start, discount_end, discount_rate) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int cafeId = getCafeIdByUserId(SessionManager.getInstance().getLoggedInUser().getUser_id());
            String dayOfWeek = discountDTO.getDayOfWeek(); // MON, TUE 등

            stmt.setInt(1, cafeId);
            stmt.setString(2, dayOfWeek);
            stmt.setInt(3, discountDTO.getDiscountStart());
            stmt.setInt(4, discountDTO.getDiscountEnd());
            stmt.setDouble(5, discountDTO.getDiscountRate());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // 성공하면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }

    /**
     * 카페 ID를 가져오는 메서드
     *
     * @param userId 사용자 ID
     * @return 해당 유저의 카페 ID
     */
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

    /**
     * Alert 창을 공용으로 띄우는 메서드
     *
     * @param owner   알림창의 소유자 (null일 경우 현재 화면에서 알림을 띄움)
     * @param type    알림창의 타입 (예: 정보, 오류, 경고)
     * @param title   알림창의 제목
     * @param message 알림창에 표시될 내용
     */
    private void showAlert(Window owner, Alert.AlertType type, String title, String message) {
        showAlert(owner, type, title, message);  // 공통 유틸리티로 알림 표시
    }
}