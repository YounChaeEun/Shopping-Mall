package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.CartRequest;
import com.example.shoppingmall_comp.domain.members.dto.CartResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface CartService {
    CartResponse create(CartRequest cartRequest, User user);
    CartResponse update(CartRequest cartRequest, User user);
    Page<CartResponse> getAll(Pageable pageable, User user);
}
