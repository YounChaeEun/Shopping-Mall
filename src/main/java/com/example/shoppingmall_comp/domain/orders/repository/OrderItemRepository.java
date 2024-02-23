package com.example.shoppingmall_comp.domain.orders.repository;

import com.example.shoppingmall_comp.domain.orders.entity.Order;
import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order); //특정 주문에 해당하는 모든 주문상품 리스트
    List<OrderItem> findAllByOrder(Order order);

}
