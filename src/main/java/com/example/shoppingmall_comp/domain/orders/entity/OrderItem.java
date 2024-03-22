package com.example.shoppingmall_comp.domain.orders.entity;
import com.example.shoppingmall_comp.domain.BaseEntity;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "member_id", nullable = false)
    private Long memberId; // 회원 id

    @Column(name = "order_item_name", nullable = false)
    private String orderItemName;

    @Column(name = "order_item_price", nullable = false)
    private int orderItemPrice;

    @Column(name = "order_item_count", nullable = false)
    private int orderItemCount;

    //주문 상품 옵션 값
    @Column(name = "order_item_option", columnDefinition = "longtext")
    @Type(type = "json")
    private List<Option> optionValues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Builder
    public OrderItem(Long memberId, Item item, String orderItemName, int orderItemPrice, int orderItemCount, Order order, List<Option> optionValues) {
        this.memberId = memberId;
        this.item = item;
        this.orderItemName = orderItemName;
        this.orderItemPrice = orderItemPrice;
        this.order = order;
        this.orderItemCount = orderItemCount;
        this.optionValues = optionValues;
    }

    // 주문 상품 생성(주문 상품 DB에)
    public static OrderItem createOrderItem(Member member, Item item, String orderItemName, int orderItemPrice, int orderItemCount, Order order, List<Option> optionValues) {
        return OrderItem.builder()
                .memberId(member.getMemberId())
                .orderItemName(orderItemName)
                .orderItemPrice(orderItemPrice) // 주문 상품 가격
                .orderItemCount(orderItemCount) // 주문 수량
                .order(order)
                .item(item)
                .optionValues(optionValues)
                .build();
    }

    public record Option (
            String key,
            String value
    ) {
    }
}