package com.example.shoppingmall_comp.domain.orders.repository;

import com.example.shoppingmall_comp.domain.orders.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<Pay, Long> {
}
