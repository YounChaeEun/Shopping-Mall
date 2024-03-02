package com.example.shoppingmall_comp.domain.items.dto;

import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "상품 응답 DTO")
public record ItemResponse (

        @Schema(description = "상품 id", example = "1")
        Long itemId,

        @Schema(description = "상품 이름", example = "노트북")
        String itemName,

        @Schema(description = "카테고리 id", example = "1")
        Long categoryId,

        @Schema(description = "상품 가격", example = "879000")
        int price,

        @Schema(description = "상품 수량", example = "1000")
        int count,

        @Schema(description = "상품 옵션", example = "{색상: WHITE}")
        List<Option> optionValue,

        @Schema(description = "상품 판매 상태", example = "품절")
        ItemState itemState,

        @Schema(description = "상품 상세 설명", example = "가볍고 화질이 선명해요.")
        String description,

        @Schema(description = "상품 이미지 url들", example = "https://dachaebucket.s3.ap-northeast-2.amazonaws.com/123.jpg")
        List<String> imgUrls

) {
    public record Option (
            String key,
            String value
    ) {
    }
}