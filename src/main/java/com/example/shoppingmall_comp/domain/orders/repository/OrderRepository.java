package com.example.shoppingmall_comp.domain.orders.repository;

import com.example.shoppingmall_comp.domain.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
