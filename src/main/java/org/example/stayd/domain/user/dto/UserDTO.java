package org.example.stayd.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 DTO
 * <p>
 * 이 DTO는 사용자 등록 및 정보 관리를 위한 객체로, 유효성 검사를 포함합니다.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * 사용자 ID
     * 사용자 정보의 고유 식별자입니다.
     */
    private int user_id;

    /**
     * 로그인 아이디
     * 아이디는 비어 있을 수 없으며, 유효한 아이디 형식이 필요합니다.
     */
    @NotBlank(message = "아이디를 입력해주세요.")
    private String login_id;

    /**
     * 이메일
     * 이메일은 비어 있을 수 없으며, 유효한 이메일 형식이여야 합니다.
     */
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    /**
     * 비밀번호
     * 비밀번호는 최소 8자 이상이어야 하며, 영문, 숫자, 특수문자를 포함해야 합니다.
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*\\W).+$",  // 영문, 숫자, 특수문자 포함 정규식
            message = "영문·숫자·특수문자를 모두 포함해야 합니다."
    )
    private String password;

    /**
     * 비밀번호 확인
     * 비밀번호를 재입력받고, 일치 여부를 확인합니다.
     */
    @NotBlank(message = "동일한 비밀번호를 입력해주세요.")
    private String passwordCheck;

    /**
     * 사용자 역할
     * 사용자에게 부여된 역할을 나타냅니다. 예: "USER", "CAFE_OWNER" 등
     */
    @NotBlank(message="역할을 입력해주세요.")
    private String role;

    /**
     * 사용자 DTO 생성자
     * @param login_id  로그인 아이디
     * @param email     이메일
     * @param password  비밀번호
     * @param passwordCheck 비밀번호 확인
     * @param role      사용자 역할
     */
    public UserDTO(String login_id, String email, String password, String passwordCheck, String role) {
        this.login_id = login_id;
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.role = role;
    }
    // 명시적으로 user_id getter 추가
    public int getUserId() {
        return user_id;
    }
}
