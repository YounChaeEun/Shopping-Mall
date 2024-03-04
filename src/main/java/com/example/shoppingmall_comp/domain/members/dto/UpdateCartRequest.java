package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;

@Schema(description = "장바구니 수정 요청 DTO")
public record UpdateCartRequest(

        @NotNull
        @Schema(description = "상품 id", example = "1")
        Long itemId,

        @NotNull
        @Schema(description = "상품 수량", example = "1")
        int count

) {
}
