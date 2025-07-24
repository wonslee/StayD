package org.example.stayd.domain.user.dao;

import org.example.stayd.common.DatabaseConnection;
import org.example.stayd.domain.user.dto.UserDTO;

import java.sql.*;
import java.util.Optional;

public class UserDAO {

    private Connection connection;

    public UserDAO() {
        this.connection = new DatabaseConnection().getConnection();
    }

    /**
     * 아이디 중복 확인
     * @param loginId 확인할 아이디
     * @return 아이디가 존재하면 true, 그렇지 않으면 false 반환
     * @throws SQLException SQL 예외 발생 시
     */
    public boolean existsById(String loginId) throws SQLException {
        String query = "{call check_id_exists(?, ?)}";  // PL/SQL 프로시저 호출

        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, loginId);  // 아이디
            stmt.registerOutParameter(2, Types.INTEGER);  // 출력 값은 존재 여부 (1 또는 0)

            stmt.execute();

            return stmt.getInt(2) > 0;  // 존재하면 true 반환
        }
    }

    /**
     * 이메일 중복 확인
     * @param email 확인할 이메일
     * @return 이메일이 존재하면 true, 그렇지 않으면 false 반환
     * @throws SQLException SQL 예외 발생 시
     */
    public boolean existsByEmail(String email) throws SQLException {
        String query = "{call check_email_exists(?, ?)}";  // PL/SQL 프로시저 호출

        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, email);  // 이메일
            stmt.registerOutParameter(2, Types.INTEGER);  // 출력 값은 존재 여부 (1 또는 0)

            stmt.execute();

            return stmt.getInt(2) > 0;  // 존재하면 true 반환
        }
    }

    /**
     * 새 사용자 등록
     * @param user UserDTO 객체 (사용자 정보)
     * @throws SQLException SQL 예외 발생 시
     */
    public void insertUser(UserDTO user) throws SQLException {
        String query = "{call insert_user(?, ?, ?, ?)}";  // PL/SQL 프로시저 호출

        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, user.getLogin_id());  // 아이디
            stmt.setString(2, user.getEmail());     // 이메일
            stmt.setString(3, user.getPassword());  // 비밀번호
            stmt.setString(4, user.getRole());      // 사용자 역할

            stmt.executeUpdate();  // 실행
        }
    }

    /**
     * 로그인 아이디로 사용자 정보 조회
     * @param loginId 검색할 아이디
     * @return 아이디에 해당하는 UserDTO (없으면 Optional.empty() 반환)
     * @throws SQLException SQL 예외 발생 시
     */
    public Optional<UserDTO> findByLoginId(String loginId) throws SQLException {
        String query = "{call find_user_by_login_id(?, ?, ?, ?, ?, ?)}";  // PL/SQL 프로시저 호출

        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, loginId);  // 아이디
            stmt.registerOutParameter(2, Types.INTEGER);  // user_id
            stmt.registerOutParameter(3, Types.VARCHAR);  // login_id
            stmt.registerOutParameter(4, Types.VARCHAR);  // email
            stmt.registerOutParameter(5, Types.VARCHAR);  // password
            stmt.registerOutParameter(6, Types.VARCHAR);  // role

            stmt.execute();

            // 프로시저에서 반환된 값들을 UserDTO에 설정
            int userId = stmt.getInt(2);
            if (userId == 0) {
                return Optional.empty();  // 사용자가 없으면 빈 Optional 반환
            }

            UserDTO user = new UserDTO();
            user.setUser_id(userId);
            user.setLogin_id(stmt.getString(3));
            user.setEmail(stmt.getString(4));
            user.setPassword(stmt.getString(5));
            user.setRole(stmt.getString(6));

            return Optional.of(user);  // 존재하는 사용자 반환
        }
    }

    /**
     * 이메일로 로그인 아이디 조회
     * @param email 이메일
     * @return 해당 이메일에 등록된 login_id, 없으면 null 반환
     * @throws SQLException SQL 예외 발생 시
     */
    public String findLoginIdByEmail(String email) throws SQLException {
        String query = "{call find_login_id_by_email(?, ?)}";  // PL/SQL 프로시저 호출

        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, email);  // 이메일
            stmt.registerOutParameter(2, Types.VARCHAR);  // login_id

            stmt.execute();

            return stmt.getString(2);  // login_id 반환
        }
    }

    /**
     * 비밀번호 업데이트
     * @param loginId 사용자 아이디
     * @param hashedPassword 새 비밀번호
     * @throws SQLException SQL 예외 발생 시
     */
    public void updatePassword(String loginId, String hashedPassword) throws SQLException {
        String query = "{call update_user_password(?, ?)}";  // PL/SQL 프로시저 호출

        try (CallableStatement stmt = connection.prepareCall(query)) {
            stmt.setString(1, loginId);  // 아이디
            stmt.setString(2, hashedPassword);  // 새로운 비밀번호

            stmt.executeUpdate();  // 업데이트 실행
        }
    }
}