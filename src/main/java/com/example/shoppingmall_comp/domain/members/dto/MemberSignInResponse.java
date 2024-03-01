package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답 DTO")
public record MemberSignInResponse(
        @Schema(description = "사용자의 엑세스 토큰", example = "Uecjdjfldksjfdnsldkljs")
        String accessToken,

        @Schema(description = "사용자의 리프레시 토큰", example = "sdfUecjdjfldksjfdnsldkljs")
        String refreshToken
) {
}
