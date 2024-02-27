package com.example.shoppingmall_comp.domain.orders.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "pay_cancel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayCancel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payCancelId;

    @ManyToOne(fetch = FetchType.LAZY) //한 주문에 여러개의 결제 취소 있을 수 있음
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private UUID merchantId;   //주문번호

    @Column(nullable = false)
    private String cancelReason; //주문 취소 사유

    @Column(nullable = false)
    private int cancelPrice;     //주문가격

    @Column(nullable = false)
    private String cardCompany;     //카드사

    @Column(nullable = false)
    private String cardNum;      //카드일련번호

    @Builder
    public PayCancel(Order order, UUID merchantId, String cancelReason, int cancelPrice, String cardCompany, String cardNum) {
        this.order = order;
        this.merchantId = merchantId;
        this.cancelReason = cancelReason;
        this.cancelPrice = cancelPrice;
        this.cardCompany = cardCompany;
        this.cardNum = cardNum;
    }
}

