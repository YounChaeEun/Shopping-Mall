package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Schema(description = "이메일 변경 요청 DTO")
public record UpdateMemberEmailRequest(
        @NotBlank
        @Email
        @Schema(description = "변경하고 싶은 새 이메일", example = "amy1234@naver.com")
        String newEmail
) {
}
