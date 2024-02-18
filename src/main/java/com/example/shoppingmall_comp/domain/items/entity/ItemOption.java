package com.example.shoppingmall_comp.domain.items.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "item_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(name = "json", typeClass = JsonType.class)
public class ItemOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;

    @Column(name = "option_values", columnDefinition = "longtext")
    @Type(type = "json")
    private List<Option> optionValues;

    @OneToOne(mappedBy = "itemOption", fetch = FetchType.LAZY)
    private Item item;

    @Builder
    public ItemOption(List<Option> optionValues) {
        this.optionValues = optionValues;
    }

    public void updateOption(List<Option> optionValues) {
        this.optionValues = optionValues;
    }

    public record Option (
            String key,
            String value
    ) {
    }
}
