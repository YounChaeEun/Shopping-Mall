package com.example.shoppingmall_comp.domain.members.entity;


import com.example.shoppingmall_comp.domain.BaseEntity;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(name = "json", typeClass = JsonType.class)
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "count", nullable = false)
    private int count; //장바구니에 담긴 상품 수량 예)노트북 2개 담음

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "sold_out_state")
    private ItemState itemState;

    @Column(name = "option_values", columnDefinition = "longtext", nullable = false)
    @Type(type = "json")
    private List<Option> optionValues;

    @Builder
    public Cart(int count, Item item, Member member, ItemState itemState, List<Option> optionValues) {
        this.count = count;
        this.item = item;
        this.member = member;
        this.itemState = itemState;
        this.optionValues = optionValues;
    }
    public void updateCart(int count) {
        this.count = count;
    }

    public record Option (
            String key,
            String value
    ) {
    }

}
