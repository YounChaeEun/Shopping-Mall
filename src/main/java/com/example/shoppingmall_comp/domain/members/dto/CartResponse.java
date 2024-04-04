package com.example.shoppingmall_comp.domain.members.dto;

import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "장바구니 응답 DTO")
public record CartResponse(

        @Schema(description = "장바구니 id", example = "1")
        Long cartId,

        @Schema(description = "장바구니의 상품 id", example = "1")
        Long itemId,

        @Schema(description = "장바구니의 상품명", example = "니트")
        String itemName,

        @Schema(description = "장바구니의 상품 가격", example = "38000")
        int itemPrice,

        @Schema(description = "장바구니에 담긴 상품 수량", example = "1")
        int itemCount,

        @Schema(description = "장바구니의 상품 판매 상태", example = "판매중")
        ItemState itemState,

        @Schema(description = "장바구니의 상품 옵션", example = "{색상: WHITE}")
        List<Option> optionValue
) {
    public record Option (
            String key,
            String value
    ) {
    }
}
