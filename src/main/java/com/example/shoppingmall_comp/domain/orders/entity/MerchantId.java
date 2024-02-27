package com.example.shoppingmall_comp.domain.orders.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantId { //주문번호들을 관리하는 엔티티
    private UUID merchantId;
}
