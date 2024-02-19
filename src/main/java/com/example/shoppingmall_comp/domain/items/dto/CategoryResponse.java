package com.example.shoppingmall_comp.domain.items.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 응답 DTO")
public record CategoryResponse(
        @Schema(description = "카테고리 id", example = "1")
        Long categoryId,

        @Schema(description = "카테고리명", example = "전자제품")
        String categoryName
) {}
