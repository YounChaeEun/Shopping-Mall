package com.example.shoppingmall_comp.domain.items.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "판매자 자신이 등록한 상품들 조회 응답 DTO")
public record SellerItemsResponse(
        @Schema(description = "상품 id", example = "1")
        Long itemId,

        @Schema(description = "상품 이름", example = "노트북")
        String itemName,

        @Schema(description = "상품 가격", example = "897000")
        int price,

        @Schema(description = "상품 수량", example = "2000")
        int count
) {
}
