package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface CartService {
    CartResponse create(CreateCartRequest cartRequest, User user);
    void update(Long cartId, UpdateCartRequest cartRequest, User user);
    CartPageResponse getAll(Pageable pageable, User user);
    void deleteSelectedCarts(DeleteCartRequest cartRequest, User user);
}
