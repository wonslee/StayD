package org.example.stayd.domain.reservation.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.reservation.dto.ReservationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDaoImpl extends ReservationWDAO {

    private final Connection conn = new DatabaseConnection().getConnection();

    @Override
    public List<ReservationDTO> findByUserId(int userId) {
        List<ReservationDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE user_id = ? AND is_canceled IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReservationDTO dto = ReservationDTO.builder()
                        .reservationId(rs.getLong("reservation_id"))
                        .userId(rs.getLong("user_id"))
                        .cafeId(rs.getLong("cafe_id"))
                        .cafeName(rs.getString("cafe_name"))
                        .usageStartedAt(rs.getInt("usage_started_at"))
                        .usageEndedAt(rs.getInt("usage_ended_at"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<ReservationDTO> findWritableReservationsByUserId(int userId) {
        List<ReservationDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE user_id = ? AND is_canceled IS NULL AND rating IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReservationDTO dto = ReservationDTO.builder()
                        .reservationId(rs.getLong("reservation_id"))
                        .userId(rs.getLong("user_id"))
                        .cafeId(rs.getLong("cafe_id"))
                        .cafeName(rs.getString("cafe_name"))
                        .usageStartedAt(rs.getInt("usage_started_at"))
                        .usageEndedAt(rs.getInt("usage_ended_at"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
