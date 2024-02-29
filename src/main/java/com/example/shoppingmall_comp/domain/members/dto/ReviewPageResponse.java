package com.example.shoppingmall_comp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "리뷰 Page 응답 DTO")
public record ReviewPageResponse(
        @Schema(description = "총 페이지 수", example = "10")
        int totalPage,

        @Schema(description = "총 항목 수", example = "50")
        int totalCount,

        @Schema(description = "현재 페이지 번호", example = "1")
        int pageNumber,

        @Schema(description = "한 페이지당 크기", example = "5")
        int currentPageSize,

        @Schema(description = "리뷰 리스트")
        List<ReviewResponse> responseList
){
}
