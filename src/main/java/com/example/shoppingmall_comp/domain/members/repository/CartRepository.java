package com.example.shoppingmall_comp.domain.members.repository;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.members.entity.Cart;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByItemAndMember(Item item, Member member);
}
