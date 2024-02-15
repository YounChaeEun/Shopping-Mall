package com.example.shoppingmall_comp.domain.items.dto;

import com.example.shoppingmall_comp.domain.items.entity.SoldOutState;

import java.util.Map;

public record ItemResponse (
        Long itemId,
        String itemName,
        Long categoryId,
        int price,
        int count,
        Map<String, String> optionValue,
        SoldOutState soldOutState,
        String description

) {

}