package com.example.shoppingmall_comp.domain.items.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_price", nullable = false)
    private int itemPrice;

    @Column(name = "item_detail", nullable = false)
    private String itemDetail;

    @Column(name = "count", nullable = false)
    private int count; //수량(재고)

    @Enumerated(EnumType.STRING)
    @Column(name = "sold_out_state", nullable = false)
    private ItemState itemState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "option_id")
    private ItemOption itemOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Item(ItemOption itemOption, String itemName, int itemPrice, String itemDetail, int count, Category category, Member member, ItemState itemState) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemDetail = itemDetail;
        this.count = count;
        this.category = category;
        this.itemOption = itemOption;
        this.member = member;
        this.itemState = itemState;
    }

    //상품 수정할 때 메소드
    public void updateItem(String itemName, int itemPrice, int count, String itemDetail, Category category, ItemState itemState) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.count = count;
        this.itemDetail = itemDetail;
        this.category = category;
        this.itemState = itemState;
    }

    //재고 수량 변경
    public void updateStock(int count) {
        this.count = count;
    }

    //상품 재고가 0이 되면 품절 상태로
    public void ToSoldOutState() {
        if(this.count == 0) {
            this.itemState = ItemState.SOLD_OUT;
        }
    }
}
