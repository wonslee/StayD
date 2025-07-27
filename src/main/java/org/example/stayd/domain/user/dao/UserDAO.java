// 작성자 : 방대혁
package org.example.stayd.domain.user.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.user.dto.UserDTO;

public class UserDAO {

    private final Connection connection;

    public UserDAO() {
        this.connection = new DatabaseConnection().getConnection();
    }

    /**
     * 아이디 중복 확인
     *
     * @param loginId 확인할 아이디
     * @return 아이디가 존재하면 true, 그렇지 않으면 false 반환
     * @throws SQLException SQL 예외 발생 시
     */
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
                rs.next();  // 결과 이동
                return rs.getInt(1) > 0;  // 아이디가 있으면 true 반환
            }
        }
    }

    /**
     * 이메일 중복 확인
     *
     * @param email 확인할 이메일
     * @return 이메일이 존재하면 true, 그렇지 않으면 false 반환
     * @throws SQLException SQL 예외 발생 시
     */
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
                rs.next();  // 결과 이동
                return rs.getInt(1) > 0;  // 이메일이 있으면 true 반환
            }
        }
    }

    /**
     * 새 사용자 등록
     *
     * @param user UserDTO 객체 (사용자 정보)
     * @throws SQLException SQL 예외 발생 시
     */
    public void insertUser(UserDTO user) throws SQLException {
        String sql = """
                INSERT INTO 
                users(login_id, email, password, role) 
                VALUES(?, ?, ?, ?)
                """;
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getLogin_id());  // 아이디
            ps.setString(2, user.getEmail());     // 이메일
            ps.setString(3, user.getPassword());  // 비밀번호
            ps.setString(4, user.getRole());      // 사용자 역할
            ps.executeUpdate();  // 실행
        }
    }

    /**
     * 로그인 아이디로 사용자 정보 조회
     *
     * @param loginId 검색할 아이디
     * @return 아이디에 해당하는 UserDTO (없으면 Optional.empty() 반환)
     * @throws SQLException SQL 예외 발생 시
     */
    public Optional<UserDTO> findByLoginId(String loginId) throws SQLException {
        String sql = """
                SELECT user_id,
                       login_id,
                       email,
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
                    user.setUser_id(rs.getInt("user_id"));
                    user.setLogin_id(rs.getString("login_id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password")); // 해시된 비밀번호
                    user.setRole(rs.getString("role"));
                    return Optional.of(user);  // 결과 반환
                }
                return Optional.empty();  // 없으면 빈 Optional 반환
            }
        }
    }

    /**
     * 이메일로 로그인 아이디 조회
     *
     * @param email 이메일
     * @return 해당 이메일에 등록된 login_id, 없으면 null 반환
     * @throws SQLException SQL 예외 발생 시
     */
    public String findLoginIdByEmail(String email) throws SQLException {
        String sql = """
                    SELECT login_id
                      FROM users
                     WHERE email = ?
                """;
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("login_id");  // login_id 반환
                }
                return null;  // 이메일이 없으면 null 반환
            }
        }
    }

    /**
     * 비밀번호 업데이트
     *
     * @param loginId        사용자 아이디
     * @param hashedPassword 새 비밀번호
     * @throws SQLException SQL 예외 발생 시
     */
    public void updatePassword(String loginId, String hashedPassword) throws SQLException {
        String sql = """
                UPDATE users
                SET password = ?
                WHERE login_id = ?
                """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);  // 새로운 비밀번호
            ps.setString(2, loginId);  // 사용자 아이디
            ps.executeUpdate();  // 업데이트 실행
        }
    }

    /**
     * PL/SQL register_user 프로시저 호출
     *
     * @return 상태 코드(0:성공,1:ID중복,2:Email중복,-1:오류)
     */
    public int registerUser(UserDTO user) throws SQLException {
        String call = "{call register_user(?, ?, ?, ?, ?)}";
        try (Connection conn = new DatabaseConnection().getConnection();
             CallableStatement cs = conn.prepareCall(call)) {
            cs.setString(1, user.getLogin_id());
            cs.setString(2, user.getEmail());
            cs.setString(3, user.getPassword());
            cs.setString(4, user.getRole());
            cs.registerOutParameter(5, Types.NUMERIC);
            cs.execute();
            return cs.getInt(5);
        }
    }
}
