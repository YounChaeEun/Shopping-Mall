package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 응답 DTO")
public record ReviewResponse(

        @Schema(description = "리뷰 ID", example = "1")
        Long reviewId,

        @Schema(description = "리뷰 제목", example = "상품1에 대한 리뷰")
        String title,

        @Schema(description = "리뷰 내용", example = "이 제품 너무 좋아요.")
        String content,

        @Schema(description = "리뷰 별점", example = "3")
        int star
) {
}