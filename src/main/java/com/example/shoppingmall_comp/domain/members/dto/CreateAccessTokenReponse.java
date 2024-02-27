package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateAccessTokenReponse(
        @Schema(description = "사용자의 새 엑세스 토큰", example = "hfdhfskjhfskjhdkjshkdj")
        String accessToken
) {
}
