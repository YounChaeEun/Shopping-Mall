package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface CartService {
    CartResponse create(CreateCartRequest cartRequest, User user);
    void update(Long cartId, UpdateCartRequest cartRequest, User user);
    CartPageResponse getAll(Pageable pageable, User user);
    void deleteSelectedCarts(List<Long> cartIds, User user);
}
