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

    @Column(name = "member_id")
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
    private Order order;

    @Builder
    public Pay(String cardCompany, String cardNum, int payPrice, Order order) {
        this.cardCompany = cardCompany;
        this.cardNum = cardNum;
        this.payPrice = payPrice;
        this.order = order; //order에 memberId 있기 때문에 memberId 추가할 필요 없음
        this.payState = PayState.COMPLETE; //파라미터로 안 들어와도 되는지
    }

    //결제 상태 변경
    public void PayStateToCancel() {
        this.payState = PayState.CANCEL;
    }
}