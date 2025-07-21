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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*\\W).+$",
            message = "영문·숫자·특수문자를 모두 포함해야 합니다."
    )
    private String newPassword;

    public PasswordResetDTO(String loginId, String email) {
        this.loginId  = loginId;
        this.email    = email;
    }
}
