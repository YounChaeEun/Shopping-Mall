package com.example.shoppingmall_comp.domain.orders.dto;

import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.UUID;

@Schema(description = "주문 요청 DTO")
public record OrderRequest (

        @NotBlank
        @Schema(description = "수취인 이름", example = "홍길동")
        String name,

        @NotBlank
        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
        String phone,

        @NotBlank
        @Schema(description = "우편번호", example = "02543")
        String zipcode,

        @NotBlank
        @Schema(description = "상세주소", example = "789 Main Street")
        String address,

        @Nullable
        @Schema(description = "배송 요청 메세지", example = "배송 전에 미리 연락 부탁드립니다.")
        String requestMessage,

        @NotNull
        @Schema(description = "주문 가격", example = "1,200,000")
        int totalPrice,

        @NotBlank
        @Schema(description = "카드사", example = "카드사 이름")
        String cardCompany,

        @NotNull
        @Schema(description = "카드 번호", example = "1234 5678 9012 3456")
        String cardNum,

        @NotNull
        @Schema(description = "주문할 상품 리스트")
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
