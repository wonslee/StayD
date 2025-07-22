package org.example.stayd.domain.user.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.user.dto.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDAO {

    // ID 중복 확인
    public boolean existsById(String loginId) throws SQLException {
        String sql = """
                SELECT COUNT(*) 
                FROM users
                 WHERE login_id = ?
                """;
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // 이메일 중복 확인
    public boolean existsByEmail(String email) throws SQLException {
        String sql = """
                SELECT COUNT(*) 
                FROM users 
                WHERE email = ?
                """;
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // User 저장
    public void insertUser(UserDTO user) throws SQLException {
        String sql = """
                INSERT INTO 
                users(login_id, email, password, role) 
                VALUES(?, ?, ?, ?)
                """;
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getLogin_id());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.executeUpdate();
        }
    }

    /**
     * loginId로 사용자 정보 조회
     *
     * @param loginId 검색할 아이디
     * @return UserDTO에 (login_id, password 해시, role) 담아서 Optional로 반환
     */
    public Optional<UserDTO> findByLoginId(String loginId) throws SQLException {
        String sql = """
                SELECT login_id,
                       password,
                       role
                  FROM users
                 WHERE login_id = ?
                """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setLogin_id(rs.getString("login_id"));
                    user.setPassword(rs.getString("password")); // 해시
                    user.setRole(rs.getString("role"));
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        }
    }
}