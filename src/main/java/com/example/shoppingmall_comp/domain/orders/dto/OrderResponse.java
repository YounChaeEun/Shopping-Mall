package com.example.shoppingmall_comp.domain.orders.dto;

import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import com.example.shoppingmall_comp.domain.orders.entity.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "주문 응답 DTO")
public record OrderResponse(

        @Schema(description = "주문 id", example = "1")
        Long orderId,

        @Schema(description = "수취인 이름", example = "홍길동")
        String name,

        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
        String phone,

        @Schema(description = "우편번호", example = "02543")
        String zipcode,

        @Schema(description = "상세주소", example = "789 Main Street")
        String address,

        @Schema(description = "배송 요청 메세지", example = "배송 전에 미리 연락 부탁드립니다.")
        String requestMessage,

        @Schema(description = "주문 가격", example = "1,200,000")
        int totalPrice,

        @Schema(description = "주문번호", example = "ORD20301948-0000000")
        UUID merchantId,

        @Schema(description = "주문상태", example = "주문 완료")
        OrderState orderState,

        @Schema(description = "카드사", example = "카드사 이름")
        String cardCompany,

        @Schema(description = "카드 번호", example = "1234 5678 9012 3456")
        String cardNum,

        @Schema(description = "주문 상품 정보")
        List<OrderedItem> orderedItems
) {
        @Schema(description = "주문할 상품 정보")
        public record OrderedItem (

                @Schema(description = "상품 id", example = "1")
                Long itemId,

                @Schema(description = "상품명", example = "노트북")
                String name,

                @Schema(description = "상품 수량", example = "1")
                int count,

                @Schema(description = "상품 가격", example = "89700")
                int price,

                @Schema(description = "상품 옵션", example = "{색상: WHITE}")
                List<OrderItem.Option> optionValues
        ) {}
}
