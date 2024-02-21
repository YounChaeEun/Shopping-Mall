package com.example.shoppingmall_comp.domain.members.dto;

import com.example.shoppingmall_comp.domain.items.entity.SoldOutState;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "장바구니 요청 DTO")
public record CartRequest(

        @NotNull
        @Schema(description = "장바구니 id", example = "1")
        Long cartId,

        @NotNull
        @Schema(description = "장바구니 수량", example = "1")
        int count,

        @NotNull
        @Schema(description = "상품 id", example = "1")
        Long itemId, //장바구니에 추가할 상품의 식별자

        @NotBlank
        @Schema(description = "상품명", example = "니트")
        String itemName,

        @NotNull
        @Schema(description = "상품 가격", example = "38000")
        int itemPrice,

        @Schema(description = "상품 품절상태", example = "ON_SALE")
        SoldOutState itemSoldOutState,

        @Schema(description = "상품 옵션", example = "색상: WHITE")
        List<Option> optionValue

) {
    public record Option (
            String key,
            String value
    ) {
    }
}