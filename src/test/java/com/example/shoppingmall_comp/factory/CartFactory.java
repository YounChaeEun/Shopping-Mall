package com.example.shoppingmall_comp.factory;

import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.members.dto.CartPageResponse;
import com.example.shoppingmall_comp.domain.members.dto.CartResponse;
import com.example.shoppingmall_comp.domain.members.dto.CreateCartRequest;
import com.example.shoppingmall_comp.domain.members.dto.UpdateCartRequest;

import java.util.Arrays;
import java.util.List;

public class CartFactory {

    public static CreateCartRequest createMockCreateCartRequest() {
        CreateCartRequest.Option option = new CreateCartRequest.Option("색상", "WHITE");
        return new CreateCartRequest(1L, "노트북", 100, 897000, Arrays.asList(option));
    }

    public static UpdateCartRequest createMockUpdateRequest() {
        return new UpdateCartRequest(1L, 200);
    }

    public static CartResponse createMockCartResponse() {
        CartResponse.Option option = new CartResponse.Option("색상", "WHITE");
        return new CartResponse(1L, 2, 1L, "노트북", 897000, ItemState.ON_SALE, Arrays.asList(option));
    }

    public static CartPageResponse createMockCartPageResponse() {
        List<CartResponse> cartItems = Arrays.asList(createMockCartResponse());
        return new CartPageResponse(10, 50, 1, 5, cartItems);
    }

}
