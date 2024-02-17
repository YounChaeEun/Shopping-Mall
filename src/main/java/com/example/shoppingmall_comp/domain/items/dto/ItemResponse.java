package com.example.shoppingmall_comp.domain.items.dto;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.SoldOutState;

import java.util.List;
import java.util.Map;

public record ItemResponse (
        Long itemId,
        String itemName,
        Long categoryId,
        int price,
        int count,
        List<Option> optionValue,
        SoldOutState soldOutState,
        String description

) {
    public record Option (
            String key,
            String value
    ) {
    }
}