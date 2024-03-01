package com.example.shoppingmall_comp.domain.orders.dto;

import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import com.example.shoppingmall_comp.domain.orders.entity.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "주문내역 응답 DTO")
public record OrderPageResponse(
        @Schema(description = "총 페이지 수", example = "10")
        int totalPage,

        @Schema(description = "총 항목 수", example = "50")
        int totalCount,

        @Schema(description = "현재 페이지 번호", example = "1")
        int pageNumber,

        @Schema(description = "한 페이지당 크기", example = "5")
        int currentPageSize,

        @Schema(description = "장바구니 상품 리스트")
        List<OrderList> OrderList

) {
    public record OrderList(
            @Schema(description = "주문 id", example = "1")
            Long orderId,

            @Schema(description = "주문상태", example = "주문 완료")
            OrderState orderState,

            @Schema(description = "주문 시간", example = "2024.2.20")
            LocalDateTime orderTime, //주문 시간

            @Schema(description = "주문상품 정보")
            List<OrderResponse.OrderedItem> orderItemList
    ) {
    }
}
