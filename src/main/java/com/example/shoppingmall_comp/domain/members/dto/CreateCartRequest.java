package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "장바구니 생성 요청 DTO")
public record CreateCartRequest(

        @NotNull
        @Schema(description = "상품 id", example = "1")
        Long itemId,

        @NotBlank
        @Schema(description = "상품명", example = "니트")
        String itemName,

        @NotNull
        @Schema(description = "상품 수량", example = "1")
        int count,

        @NotNull
        @Schema(description = "상품 가격", example = "38000")
        int itemPrice,

        @Schema(description = "상품 옵션", example = "{색상: WHITE}")
        List<Option> optionValue

) {
    public record Option (
            String key,
            String value
    ) {
    }
}