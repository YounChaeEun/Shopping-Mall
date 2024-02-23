package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "장바구니 삭제 요청 DTO")
public record DeleteCartRequest(
        @NotNull
        @Schema(description = "장바구니 id 리스트", example = "[1, 2, 3]")
        List<Long> cartIdList
) {
}
