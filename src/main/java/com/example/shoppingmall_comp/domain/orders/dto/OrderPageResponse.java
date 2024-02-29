package com.example.shoppingmall_comp.domain.orders.dto;

import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import com.example.shoppingmall_comp.domain.orders.entity.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "주문내역 응답 DTO")
public record OrderPageResponse(
        @Schema(description = "주문 id", example = "1")
        Long orderId,

        @Schema(description = "주문상태", example = "주문 완료")
        OrderState orderState,

        @Schema(description = "주문 시간", example = "2024.2.20")
        LocalDateTime orderTime, //주문 시간

        @Schema(description = "주문상품 정보")
        List<OrderItemInfo> orderItemCreates
) {
    public record OrderItemInfo (
            @Schema(description = "주문상품 id", example = "1")
            Long itemId,

            @Schema(description = "주문상품명", example = "바지")
            String name,

            @Schema(description = "주문상품 수량", example = "1")
            int count,

            @Schema(description = "주문상품 가격", example = "43000")
            int orderPrice, //주문할 각 상품 주문 가격

            @Schema(description = "주문상품 옵션", example = "진청/L")
            List<OrderItem.Option> optionValues
    ) {}
}
