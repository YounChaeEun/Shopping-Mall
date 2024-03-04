package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Schema(description = "비밀번호 변경 요청 DTO")
public record UpdateMemberPaswordRequest(
        @NotBlank
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(description = "현재 비밀번호", example = "Amy4021*")
        String oldPassword,

        @NotBlank
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(description = "변경하고 싶은 새 비밀번호", example = "Amy4021!")
        String newPassword
) {
}
