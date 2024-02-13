package com.example.shoppingmall_comp.domain.items.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "itemImage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImage extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "item_image_id")
    private Long itemImageId;

    //경로로 바꿔줘야 하는 건지, 파일명으로 해야하는지
    @Column(name = "item_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Builder
    public ItemImage(String imageUrl, Item item) {
        this.imageUrl = imageUrl;
        this.item = item;
    }
}