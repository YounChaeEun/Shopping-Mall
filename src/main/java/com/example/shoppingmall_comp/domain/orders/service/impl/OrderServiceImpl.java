package com.example.shoppingmall_comp.domain.orders.service.impl;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.entity.Cart;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.orders.dto.OrderPageResponse;
import com.example.shoppingmall_comp.domain.orders.dto.OrderRequest;
import com.example.shoppingmall_comp.domain.orders.dto.OrderResponse;
import com.example.shoppingmall_comp.domain.orders.dto.PayCancelRequest;
import com.example.shoppingmall_comp.domain.orders.entity.*;
import com.example.shoppingmall_comp.domain.orders.repository.OrderItemRepository;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import com.example.shoppingmall_comp.domain.orders.repository.PayCancelRepository;
import com.example.shoppingmall_comp.domain.orders.repository.PayRepository;
import com.example.shoppingmall_comp.domain.orders.service.OrderService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    private final PayCancelRepository payCancelRepository;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest orderRequest, User user) {
        Member member = getMember(user);

        //주문 생성
        Order order = Order.toOrder(orderRequest, member);
        orderRepository.save(order);

        //주문상품 DB에 저장
        //orderRequest에 있는 각 주문된 상품을 반복하면서, 해당 주문 항목의 상품 id를 이용하여 주문한 아이템 가져옴
        for (OrderRequest.orderItemCreate orderItemCreate : orderRequest.orderItemCreates()) {
            Item item = itemRepository.findById(orderItemCreate.itemId())
                    .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));

            //상품 재고가 품절일 경우 주문 불가
            if(item.getItemState() == ItemState.SOLD_OUT) {
                throw new BusinessException(NOT_SELLING_ITEM);
            }
            //판매가 중단된 상품일 때 주문 불가
            if(item.getItemState() == ItemState.DISCONTINUED) {
                throw new BusinessException(DISCONTINUED_ITEM);
            }

            //주문 후 상품 재고 업데이트
            int orderedQuantity = orderItemCreate.count();
            if (item.getCount() < orderedQuantity) { //주문하려는 상품 재고 < 주문 수량
                throw new BusinessException(NOT_ENOUGH_STOCK);
            }

            //주문 수량만큼 주문한 해당 상품의 재고 감소
            int newStock = item.getCount() - orderedQuantity;
            item.updateStock(newStock);
            itemRepository.save(item);

            //주문된 상품을 주문 상품 DB에 저장
            OrderItem orderItem = OrderItem.createOrderItem(member, item, item.getItemName(), orderItemCreate.orderPrice(), orderItemCreate.count(), order, orderItemCreate.optionValues());
            orderItemRepository.save(orderItem);

            //주문한 상품이 장바구니에 존재할 경우
            Cart cart = cartRepository.findByItemAndMember(item, member);
            if (cart != null) {
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
        int rewardPoints = order.getTotalPrice() / 100; //총 주문 금액의 1%
        member.updatePoints(rewardPoints);
        memberRepository.save(member);

        return getOrderResponse(order, pay);
    }

    //결제 취소
    @Override
    @Transactional
    public void payCancel(PayCancelRequest payCancelRequest, User user) {
        Member member = getMember(user);

        Order order = orderRepository.findByMerchantId(payCancelRequest.merchantId())
                .orElseThrow(() -> new BusinessException(NOT_EQUAL_MERCHANT_ID, "주문 번호가 같지 않습니다."));

        Pay pay = payRepository.findByOrder(order)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_PAY));

        //결제 회원과 로그인한 회원이 다를 경우
        if (!pay.getMemberId().equals(member.getMemberId())) {
            throw new BusinessException(CAN_NOT_CANCEL_PAY);
        }

        //이미 취소된 결제를 또 결제 취소하려고 할 경우
        if (pay.getPayState().equals(PayState.CANCEL)) {
            throw new BusinessException(ALREADY_CANCEL_PAY, "이미 결제 취소가 되었습니다.");
        }

        //주문 상태가 배송중일 경우
        if (order.getOrderState().equals(OrderState.DELIVERY)) {
            throw new BusinessException(ALREADY_DELIVERY_STATUS, "현재 배송 중이라 결제 취소가 불가능합니다.");
        }

        PayCancel payCancel = PayCancel.builder()
                .order(pay.getOrder())
                .merchantId(payCancelRequest.merchantId())
                .cancelReason(payCancelRequest.cancelReason())
                .cancelPrice(pay.getPayPrice())
                .cardCompany(pay.getCardCompany())
                .cardNum(pay.getCardNum())
                .build();

        //주문 상품 DB에서 삭제
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(pay.getOrder());//todo:이거 어떻게 가능?
        orderItemRepository.deleteAll(orderItems);

        //결제취소 DB 저장
        payCancelRepository.save(payCancel);

        //결제 상태 변경
        pay.PayStateToCancel();
        //주문 DB 상태 변경
        pay.getOrder().orderStateToCancel();

    }

    private Member getMember(User user) {
        return memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }

    private List<OrderResponse.OrderedItem> toOrderedItem(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> new OrderResponse.OrderedItem(
                        orderItem.getItem().getItemId(),
                        orderItem.getOrderItemName(),
                        orderItem.getOrderItemCount(),
                        orderItem.getOrderItemPrice(),
                        orderItem.getOptionValues()
                ))
                .toList();
    }

    //ItemResponse 코드 중복 방지
    private OrderResponse getOrderResponse(Order order, Pay pay) {

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order); //주문에 속한 주문상품들 조회

        List<OrderResponse.OrderedItem> orderedItems = toOrderedItem(orderItems);

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
                orderedItems
        );
    }

    @Override
    public OrderResponse getOne(User user, Long orderId) {
        getMember(user);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ORDERS));
        Pay pay = payRepository.findByOrder(order)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAY));
        return getOrderResponse(order, pay);
    }

    @Override
    public OrderPageResponse getAll(User user, Pageable pageable) {
        Member member = getMember(user);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Order> orders = orderRepository.findAllByMember(member, pageRequest);

        List<OrderPageResponse.OrderList> orderList = orders.stream()
                .map(order -> {
                    // 주문 상품 리스트
                    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                    List<OrderResponse.OrderedItem> orderedItem = toOrderedItem(orderItems);

                    // 주문 리스트
                    return new OrderPageResponse.OrderList(
                            order.getOrderId(),
                            order.getOrderState(),
                            order.getCreatedAt(),
                            orderedItem);
                })
                .collect(Collectors.toList());

        return new OrderPageResponse(
                orders.getTotalPages(),
                (int) orders.getTotalElements(),
                orders.getNumber(),
                orders.getSize(),
                orderList);
    }
}
