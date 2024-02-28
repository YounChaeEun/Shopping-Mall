package com.example.shoppingmall_comp.domain.members.repository;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.members.entity.Cart;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByItemAndMember(Item item, Member member);
    Optional<Cart> findByCartIdAndMember(Long cartId, Member member);
    Page<Cart> findAllByMember(Member member, Pageable pageable);
    List<Cart> findAllByMember(Member member);
}
