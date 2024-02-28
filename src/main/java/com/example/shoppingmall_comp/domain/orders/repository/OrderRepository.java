package com.example.shoppingmall_comp.domain.orders.repository;

import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMerchantId(String merchantId);
    List<Order> findAllByMember(Member member);
}
