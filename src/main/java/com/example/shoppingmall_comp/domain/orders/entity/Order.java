package com.example.shoppingmall_comp.domain.orders.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.orders.dto.OrderRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_num", nullable = false)
    private String receiverPhone;

    @Column(name = "zipcode", nullable = false)
    private String zipcode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "request_message")
    private String requestMessage;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_state", nullable = false)
    private OrderState orderState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //결제
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId; //주문번호 예) ORD20301948-0000000

    @Builder
    public Order(Member member, String receiverName, String receiverPhone, String zipcode, String address, String requestMessage, OrderState orderState, int totalPrice, UUID merchantId) {
        this.member = member;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.zipcode = zipcode;
        this.address = address;
        this.requestMessage = requestMessage;
        this.orderState = OrderState.COMPLETE;
        this.totalPrice = totalPrice;
        this.merchantId = merchantId;
    }

    //주문 생성
    public static Order toOrder(OrderRequest orderRequest, Member member) {
        int totalPrice = 0;

        //총 주문 금액 계산
        for(OrderRequest.OrderedItem OrderedItem : orderRequest.orderedItems()) {
            totalPrice += OrderedItem.price() * OrderedItem.count();
        }

        return Order.builder()
                .member(member)
                .receiverName(orderRequest.name())
                .receiverPhone(orderRequest.phone())
                .zipcode(orderRequest.zipcode())
                .address(orderRequest.address())
                .requestMessage(orderRequest.requestMessage())
                .orderState(OrderState.COMPLETE)
                .totalPrice(totalPrice)
                .merchantId(UUID.randomUUID())
                .build();
    }

    //주문 상태 취소로 변경
    public void orderStateToCancel() {
        this.orderState = OrderState.CANCEL;
    }

    public void changeMemberToNull() {
        this.member = null;
    }
}