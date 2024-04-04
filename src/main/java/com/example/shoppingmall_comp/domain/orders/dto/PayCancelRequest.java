package com.example.shoppingmall_comp.domain.orders.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Schema(description = "결제 취소 요청 DTO")
public record PayCancelRequest(
        @NotNull
        @Schema(description = "주문번호", example = "ORD20301948-0000000")
        UUID merchantId,

        @NotNull
        @Schema(description = "주문 id", example = "1")
        Long orderId,

        @NotNull
        @Schema(description = "결제 취소 사유", example = "구매를 원하지 않음")
        String cancelReason
) {
}
