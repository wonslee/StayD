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
        String procedureCall = "{call insert_discount_settings(?, ?, ?, ?, ?)}";  // PL/SQL 프로시저 호출

        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            // 필요한 파라미터 설정
            int userId = SessionManager.getInstance().getLoggedInUser().getUser_id();
            String dayOfWeek = discountDTO.getDayOfWeek();
            int discountStart = discountDTO.getDiscountStart();
            int discountEnd = discountDTO.getDiscountEnd();
            double discountRate = discountDTO.getDiscountRate();

            // 프로시저 파라미터 설정
            stmt.setInt(1, userId);
            stmt.setString(2, dayOfWeek);
            stmt.setInt(3, discountStart);
            stmt.setInt(4, discountEnd);
            stmt.setDouble(5, discountRate);

            // 프로시저 실행
            stmt.executeUpdate();
            return true; // 성공적으로 실행되었으면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }

    /**
     * 카페 ID를 가져오는 PL/SQL 프로시저 호출 메서드
     *
     * @param userId 사용자 ID
     * @return 해당 유저의 카페 ID
     */
    private int getCafeIdByUserId(int userId) {
        String procedureCall = "{call get_cafe_id_by_user_id(?, ?)}";  // PL/SQL 프로시저 호출
        int cafeId = -1; // 기본값 -1

        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            // 프로시저 파라미터 설정
            stmt.setInt(1, userId);
            stmt.registerOutParameter(2, Types.INTEGER);  // 출력 파라미터 등록

            // 프로시저 실행
            stmt.executeUpdate();

            // 출력 파라미터 값 읽기
            cafeId = stmt.getInt(2);  // 프로시저에서 반환된 cafe_id
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cafeId;  // 결과 반환
    }

}