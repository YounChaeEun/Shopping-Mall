package com.example.shoppingmall_comp.domain.orders.service;

import com.example.shoppingmall_comp.domain.orders.dto.OrderRequest;
import com.example.shoppingmall_comp.domain.orders.dto.OrderResponse;
import org.springframework.security.core.userdetails.User;

public interface OrderService {
    OrderResponse create(OrderRequest orderRequest, User user);
}
