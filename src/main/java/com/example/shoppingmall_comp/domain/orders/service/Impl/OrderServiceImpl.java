package com.example.shoppingmall_comp.domain.orders.service.Impl;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.SoldOutState;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.entity.Cart;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.orders.dto.OrderRequest;
import com.example.shoppingmall_comp.domain.orders.dto.OrderResponse;
import com.example.shoppingmall_comp.domain.orders.entity.Order;
import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import com.example.shoppingmall_comp.domain.orders.entity.Pay;
import com.example.shoppingmall_comp.domain.orders.repository.OrderItemRepository;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import com.example.shoppingmall_comp.domain.orders.repository.PayRepository;
import com.example.shoppingmall_comp.domain.orders.service.OrderService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.shoppingmall_comp.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final PayRepository payRepository;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest orderRequest, User user) {
        Member member = getMember(user);

        //주문 생성
        Order order = Order.toOrder(orderRequest, member);
        orderRepository.save(order);

        //주문상품 DB에 저장
        //orderRequest에 있는 각 주문된 상품을 반복하면서, 해당 주문 항목의 상품 id를 이용하여 주문한 아이템 가져옴
        for(OrderRequest.orderItemCreate orderItemCreate : orderRequest.orderItemCreates()) {
            Item item = itemRepository.findById(orderItemCreate.itemId())
                    .orElseThrow(()-> new BusinessException(NOT_FOUND_ITEM));

            //상품 재고가 품절일 경우 주문 불가
            if(item.getSoldOutState() == SoldOutState.SOLD_OUT) {
                throw new BusinessException(NOT_SELLING_ITEM);
            }

            //주문 후 상품 재고 업데이트
            int orderedQuantity = orderItemCreate.count();
            if(item.getCount() < orderedQuantity) { //주문하려는 상품 재고 < 주문 수량
                throw new BusinessException(NOT_ENOUGH_STOCK);
            }

            //주문 수량만큼 주문한 해당 상품의 재고 감소
            int newStock = item.getCount() - orderedQuantity;
            item.updateStock(newStock);
            itemRepository.save(item);

            //주문된 상품을 주문 상품 DB에 저장
            OrderItem orderItem = OrderItem.createOrderItem(member, item, item.getItemName(), orderItemCreate.orderPrice(), orderItemCreate.count(), order);
            orderItemRepository.save(orderItem);

            //주문한 상품이 장바구니에 존재할 경우
            Cart cart = cartRepository.findByItemAndMember(item, member);
            if(cart != null) {
                cartRepository.delete(cart);
            }
        }

        //결제 정보 저장
        Pay pay = Pay.builder()
                .cardCompany(orderRequest.cardCompany())
                .cardNum(orderRequest.cardNum())
                .order(order)
                .payPrice(order.getTotalPrice())
                .build();
        payRepository.save(pay);

        //일반 회원 결제하면 적립금 부여
        int rewardPoints = order.getTotalPrice()/100; //총 주문 금액의 1%
        member.updatePoints(rewardPoints);
        memberRepository.save(member);

        return getOrderResponse(order, pay);
    }

    private Member getMember(User user) {
        return memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }

    //ItemResponse 코드 중복 방지
    private OrderResponse getOrderResponse(Order order, Pay pay) {

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order); //주문에 속한 주문상품들 조회

        List<OrderResponse.orderItemCreate> orderItemCreates = orderItems.stream()
                .map(orderItem -> new OrderResponse.orderItemCreate(
                        orderItem.getItem().getItemId(),
                        orderItem.getOrderItemName(),
                        orderItem.getOrderItemCount(),
                        orderItem.getOrderItemPrice()
                ))
                .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getZipcode(),
                order.getAddress(),
                order.getRequestMessage(),
                order.getTotalPrice(),
                order.getMerchantId(),
                order.getOrderState(),
                pay.getCardCompany(), // Pay 엔티티에서 카드사 정보 가져옴
                pay.getCardNum(), // Pay 엔티티에서 카드 번호 정보 가져옴
                orderItemCreates
                );
    }
}
