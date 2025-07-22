package org.example.stayd.domain.user.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.stayd.common.FXUtils;
import org.example.stayd.common.SessionManager;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.dao.UserDAO;
import org.example.stayd.domain.user.dto.PasswordResetDTO;
import org.example.stayd.domain.user.dto.UserDTO;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class UserService {
    private final Validator validator;
    private final UserDAO userDao = new UserDAO();

    public UserService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private final SessionManager sessionManager = SessionManager.getInstance();

    // 로그인 상태 확인 메서드
    public boolean isUserLoggedIn() {
        return sessionManager.isUserLoggedIn();  // 세션에서 로그인 정보 확인
    }

    /**
     * 회원가입 처리
     * - Bean Validation
     * - 비밀번호 일치 확인
     * - 아이디/이메일 중복 검사
     * - 비밀번호 암호화
     * - DB 저장
     */
    public void register(UserDTO dto) throws ValidationException {
        // Bean Validation
        Set<ConstraintViolation<UserDTO>> errs = validator.validate(dto);
        if (!errs.isEmpty()) {
            String msg = errs.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .reduce((a,b) -> a + "; " + b)
                    .orElse("검증 오류 발생");
            throw new ValidationException(msg);
        }

        // 비밀번호 일치 검사
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.");
        }

        // 역할 기본값
        String role = dto.getRole();
        if (role == null || role.isBlank()) {
            role = "USER";  // 기본값
        }
        if (!Set.of("USER", "CAFE_OWNER", "ADMIN").contains(role)) {
            throw new ValidationException("유효하지 않은 역할 값입니다.");
        }
        dto.setRole(role);

        try {
            // 중복 검사
            if (userDao.existsById(dto.getLogin_id())) {
                throw new ValidationException("이미 사용 중인 아이디입니다.");
            }
            if (userDao.existsByEmail(dto.getEmail())) {
                throw new ValidationException("이미 사용 중인 이메일입니다.");
            }

            // 비밀번호 해시 암호화
            String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
            dto.setPassword(hashed);

            // DB 저장
            userDao.insertUser(dto);

        } catch (SQLException e) {
            throw new ValidationException("DB 오류 발생");
        }
    }

    /**
     * 아이디 사용 가능 여부 확인
     * @param loginId 검사할 아이디
     * @return true = 사용 가능, false = 이미 존재
     */
    public boolean isIdAvailable(String loginId) throws ValidationException {
        try {
            return !userDao.existsById(loginId);
        } catch (SQLException e) {
            throw new ValidationException("아이디 중복 확인 오류 발생");
        }
    }

    /**
     * 이메일 사용 가능 여부 확인
     * @param email 검사할 아이디
     * @return true = 사용 가능, false = 이미 존재
     */
    public boolean isEmailAvailable(String email) throws ValidationException {
        try {
            return !userDao.existsByEmail(email);
        } catch (SQLException e) {
            throw new ValidationException("이메일 중복 확인 오류 발생");
        }
    }

    /** 유효성 검사 실패 예외 */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * 로그인 인증 처리
     *
     * @param loginId    아이디
     * @param rawPassword 평문 비밀번호
     * @return 인증된 UserDTO (비밀번호 해시 제거 후 반환)
     * @throws AuthenticationException 인증 실패 시
     */
    public UserDTO authenticate(String loginId, String rawPassword) throws AuthenticationException {
        try {
            Optional<UserDTO> opt = userDao.findByLoginId(loginId);
            if (opt.isEmpty()) {
                throw new AuthenticationException("아이디 또는 비밀번호가 틀립니다.");
            }

            UserDTO user = opt.get();
            String hash = user.getPassword();

            // BCrypt 검증
            if (!BCrypt.checkpw(rawPassword, hash)) {
                throw new AuthenticationException("아이디 또는 비밀번호가 틀립니다.");
            }

            // 로그인 성공 시 세션에 사용자 정보 저장
            SessionManager.getInstance().setLoggedInUser(user);

            // 인증 성공
            user.setPassword(null);
            user.setPasswordCheck(null);
            return user;
        } catch (SQLException e) {
            throw new AuthenticationException("로그인 처리 중 DB 오류가 발생했습니다.");
        }
    }

    /**
     * 회원가입 전 이메일 인증 전 단계의 검증
     * Bean Validation, 비밀번호 일치, 아이디/이메일 중복 검사 수행
     */
    public void validateForPending(UserDTO dto) throws ValidationException {
        // Bean Validation
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("검증 오류 발생");
            throw new ValidationException(msg);
        }

        // 비밀번호 일치 확인
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.");
        }

        // 아이디/이메일 중복 검사
        try {
            if (userDao.existsById(dto.getLogin_id())) {
                throw new ValidationException("이미 사용 중인 아이디입니다.");
            }
            if (userDao.existsByEmail(dto.getEmail())) {
                throw new ValidationException("이미 가입된 이메일입니다.");
            }
        } catch (SQLException e) {
            throw new ValidationException("중복 확인 중 오류가 발생했습니다.");
        }
    }

    /**
     * 아이디 찾기
     * @param email 조회할 이메일
     * @return loginId (없으면 null)
     * @throws ValidationException 조회 오류 시
     */
    public String findLoginIdByEmail(String email) throws ValidationException {
        try {
            return userDao.findLoginIdByEmail(email);
        } catch (SQLException e) {
            throw new ValidationException("아이디 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 아이디 이메일 계정 일치 확인
     */
    public boolean checkUserEmail(String loginId, String email) throws ValidationException {
        try {
            Optional<UserDTO> userOpt = userDao.findByLoginId(loginId);
            if (userOpt.isEmpty()) {
                throw new ValidationException("아이디가 존재하지 않습니다.");
            }

            UserDTO user = userOpt.get();

            // null 체크 후 비교
            if (user.getEmail() == null || !user.getEmail().equals(email)) {
                throw new ValidationException("아이디와 이메일이 일치하지 않습니다.");
            }

            return true; // 이메일이 일치함
        } catch (SQLException e) {
            throw new ValidationException("DB 오류 발생");
        }
    }

    /**
     * 새 비밀번호 설정
     */
    public void updatePassword(String loginId, String newPassword) throws ValidationException {
        try {
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            userDao.updatePassword(loginId, hashedPassword);
        } catch (SQLException e) {
            throw new ValidationException("비밀번호 업데이트 중 오류 발생");
        }
    }

    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}