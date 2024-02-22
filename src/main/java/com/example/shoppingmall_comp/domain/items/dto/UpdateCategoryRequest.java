package com.example.shoppingmall_comp.domain.items.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "카테고리 수정시 사용하는 요청 DTO")
public record UpdateCategoryRequest(
        @NotNull
        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,

        @NotBlank
        @Schema(description = "카테고리명", example = "전자제품")
        String categoryName
) {
}
