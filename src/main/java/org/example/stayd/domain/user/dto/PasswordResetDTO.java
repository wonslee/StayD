// 작성자 : 방대혁
package org.example.stayd.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 재설정 전용 DTO
 * <p>
 * 이 DTO는 비밀번호를 재설정할 때 클라이언트로부터 받은 데이터를 캡슐화하여 처리하는 객체입니다.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {

    /**
     * 로그인 아이디 아이디는 비어 있으면 안되며, 유효한 아이디가 입력되어야 합니다.
     */
    private int userId;

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    /**
     * 이메일 이메일은 비어 있을 수 없으며, 유효한 이메일 형식이여야 합니다.
     */
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    /**
     * 새 비밀번호 새 비밀번호는 최소 8자 이상이어야 하며, 영문, 숫자, 특수문자를 포함해야 합니다.
     */
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*\\W).+$",  // 영문, 숫자, 특수문자 포함 정규식
            message = "영문·숫자·특수문자를 모두 포함해야 합니다."
    )
    private String newPassword;

    /**
     * 생성자: 아이디와 이메일로 PasswordResetDTO 객체 생성
     *
     * @param loginId 사용자 아이디
     * @param email   사용자 이메일
     */
    public PasswordResetDTO(String loginId, String email) {
        this.loginId = loginId;
        this.email = email;
    }
}
