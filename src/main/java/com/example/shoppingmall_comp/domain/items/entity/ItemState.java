package com.example.shoppingmall_comp.domain.items.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ItemState {
    @Schema(description = "품절 상태")
    SOLD_OUT,

    @Schema(description = "판매 중인 상태")
    ON_SALE,

    @Schema(description = "판매 중단")
    DISCONTINUED
}
