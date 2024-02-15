package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public record CreateAccessTokenRequest(
        @NotBlank
        @Schema(description = "사용자의 리프레시 토큰", example = "hfdhfskjhfskjhdkjshkdj")
        String refreshToken
) {
}
