package com.example.shoppingmall_comp.domain.members.dto;

import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "로그인 요청 DTO")
public record MemberSignUpRequest(
        @NotBlank
        @Email(message = "email 형식이 올바르지 않습니다.")
        @Schema(description = "사용자 이메일", example = "amy@naver.com")
        String email,

        @NotBlank
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(description = "사용자 비밀번호", example = "Amy1234!")
        String password,

        @NotNull
        @Schema(description = "사용자 권한", example = "USER")
        RoleName roleName
        // @NotBlank는 문자열에만 가능하다. 이넘 타입에는 사용할 수 없다. 그래서 이넘 타입을 위한 validation을 커스텀해서 만들기도 한다.
) {
}
