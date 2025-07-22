package org.example.stayd.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private int user_id;

    @NotBlank(message = "아이디를 입력해주세요.")
    private String login_id;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*\\W).+$",
            message = "영문·숫자·특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "동일한 비밀번호를 입력해주세요.")
    private String passwordCheck;

    @NotBlank(message="역할")
    private String role;

    public UserDTO(String login_id, String email, String password, String passwordCheck, String role) {
        this.login_id = login_id;
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.role = role;
    }
}
