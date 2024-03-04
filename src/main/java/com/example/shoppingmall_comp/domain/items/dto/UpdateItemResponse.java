package com.example.shoppingmall_comp.domain.items.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "상품 수정 응답 DTO")
public record UpdateItemResponse(
        @Schema(description = "상품 이미지 ID", example = "1")
        List<Long> itemImageId,

        @Schema(description = "상품 이미지 경로 리스트", example = "[\"https://dachaebucket.s3.ap-northeast-2.amazonaws.com/123.jpg\"]")
        List<String> itemUrls
) {
}
