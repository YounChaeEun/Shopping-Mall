package com.example.shoppingmall_comp.domain.orders.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

//    @Column(name = "delivery_fee", nullable = false)
//    private int deliveryFee;

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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; //todo: Member로 가져오는 것보다 memberId가 낫지 않나

    // todo: 결제
    //iamport에서 만들어준 값 두개
    @Column(name = "imp_uid",nullable = false)
    private String impUid; //아이엠포트 발급 예)imp_7388992718

    @Column(name = "merchant_id", nullable = false)
    private String merchantId; //주문번호 예) ORD20301948-0000000

    @Builder
    public Order(Member member, String receiverName, String receiverPhone, String zipcode, String address, String requestMessage, int totalPrice, String impUid, String merchantId) {
        this.member = member;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.zipcode = zipcode;
        this.address = address;
        this.requestMessage = requestMessage;
        this.orderState = OrderState.COMPLETE;
        this.totalPrice = totalPrice;

        this.impUid = impUid;
        this.merchantId = merchantId;
    }

    //주문 생성
//    public static Order toOrder(OrderRequest orderRequest, Member member) {
//        int totalPrice = 0;
//
//        //총 주문 금액 계산
//        //1. OrderRequest의  메소드(getOrderItemCreates())를 사용하여 주문에 포함된 모든 상품들을 가져온다.
//        //2. 그 가져온 각 상품의 가격 가져와서 totalPrice 변수에 더하기
//        for(OrderRequest.orderItemCreate orderItemCreate : orderRequest.orderItemCreates()) {
//            totalPrice += orderItemCreate.orderPrice() * orderItemCreate.count();
//        }
//
//        return Order.builder()
//                .member(member)
//                .receiverName(orderRequest.name())
//                .receiverPhone(orderRequest.phone())
//                .zipcode(orderRequest.zipcode())
//                .address(orderRequest.address())
//                .requestMessage(orderRequest.requestMessage())
//                .totalPrice(totalPrice) // 총 주문 금액 설정
//                .impUid(orderRequest.impUid())
//                .merchantId(orderRequest.merchantId())
//                .build();
//    }

    //주문 상태 취소로 변경
    public void orderStateToCancel() {
        this.orderState = OrderState.CANCEL;
    }
}