package com.example.shoppingmall_comp.domain.orders.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "pay")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payId;

    @Column
    private Long memberId;

    @Column(name = "company", nullable = false)
    private String cardCompany;

    @Column(name = "card_num", nullable = false)
    private String cardNum;

    @Column(name = "pay_price", nullable = false)
    private int payPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_state", nullable = false)
    private PayState payState;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //원투원으로 하는게 맞을지?

    @Builder
    public Pay(String cardCompany, String cardNum, int payPrice, Order order) {
        this.memberId = order.getMember().getMemberId();
        this.cardCompany = cardCompany;
        this.cardNum = cardNum;
        this.payPrice = payPrice;
        this.order = order;
        this.payState = PayState.COMPLETE;
        //파라미터 안에 들어갈 것과 this.~에 들어갈 내용이 같아야 하나? -> 달라도 된다함
    }

    //결제 상태 변경
    public void PayStateToCancel() {
        this.payState = PayState.CANCEL;
    }
}