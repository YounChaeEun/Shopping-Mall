package com.example.shoppingmall_comp.orders.service;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemOptionRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.orders.dto.OrderPageResponse;
import com.example.shoppingmall_comp.domain.orders.dto.OrderResponse;
import com.example.shoppingmall_comp.domain.orders.dto.PayCancelRequest;
import com.example.shoppingmall_comp.domain.orders.entity.*;
import com.example.shoppingmall_comp.domain.orders.repository.OrderItemRepository;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import com.example.shoppingmall_comp.domain.orders.repository.PayRepository;
import com.example.shoppingmall_comp.domain.orders.service.OrderService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.shoppingmall_comp.global.exception.ErrorCode.NOT_FOUND_PAY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("주문 서비스 테스트")
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemOptionRepository itemOptionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PayRepository payRepository;

    private User user;
    private Item item;
    private ItemOption itemOption;
    private Member member;
    private Category category;
    UUID merchantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        this.user = new User("test@naver.com", "dayeTest", new ArrayList<>());
        this.member = new Member("test@naver.com", "dayeTest", Role.builder().roleName(RoleName.SELLER).build());
        member.updatePoints(10000);
        this.member = memberRepository.save(this.member);

        this.category = createCategory("전자제품");
        this.itemOption = createOption("색상", "WHITE");
        this.item = createItem("노트북", 897000, "상품 상세설명 test", 1000, category, member, itemOption, ItemState.ON_SALE);
    }

    @Test
    @DisplayName("주문 생성 성공 테스트")
    void addOrder() {
        //when
        Order createdOrder = createOrder(member, "이다예", "01012345678", "Street 66", "상세주소", "요청메세지", OrderState.COMPLETE, 1000000, merchantId);

        //then
        assertThat(createdOrder.getReceiverName()).isEqualTo("이다예");
        assertThat(createdOrder.getReceiverPhone()).isEqualTo("01012345678");
        assertThat(createdOrder.getZipcode()).isEqualTo("Street 66");
        assertThat(createdOrder.getAddress()).isEqualTo("상세주소");
        assertThat(createdOrder.getRequestMessage()).isEqualTo("요청메세지");
        assertThat(createdOrder.getMerchantId()).isEqualTo(merchantId);
        assertThat(createdOrder.getTotalPrice()).isEqualTo(1000000);
    }

    @Test
    @DisplayName("주문 후 상품 재고 변경 테스트")
    void updateStock() {
        //given
        int initialStock = 100;
        int orderedItemQuantity = 10;
        Item item = createItem("노트북", 897000, "상품 상세설명 test", initialStock, category, member, itemOption, ItemState.ON_SALE);
        int expectedStock = initialStock - orderedItemQuantity; //주문 후 상품의 예상 재고
        createOrder(member, "이다예", "01012345678", "Street 66", "상세주소", "요청메세지", OrderState.COMPLETE, 1000000, merchantId);

        //when
        updateItemStock(item, orderedItemQuantity);

        //then
        Item updatedItem = itemRepository.findById(item.getItemId()).orElse(null);
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getCount()).isEqualTo(expectedStock);
    }

    @Test
    @DisplayName("주문 후 적립금 부여 테스트")
    void saveRewardPoints() {
        //given
        int totalPrice = 1000000;
        createOrder(member, "이다예", "01012345678", "Street 66", "상세주소", "요청메세지", OrderState.COMPLETE, totalPrice, merchantId);

        //when
        int expectedPoints = totalPrice / 100;
        int actualPoints = member.getPoint();

        //then
        assertThat(actualPoints).isEqualTo(expectedPoints);
    }

    @Test
    @DisplayName("결제 취소 성공 테스트")
    void cancelOrder() {
        //given
        Order createdOrder = createOrder(member, "이다예", "01012345678", "Street 66", "상세주소", "요청메세지", OrderState.COMPLETE, 1000000, merchantId);
        createPay("카드사", "카드 번호", 10000, createdOrder);
        PayCancelRequest cancelRequest = new PayCancelRequest(createdOrder.getMerchantId(),createdOrder.getOrderId(), "취소 사유");

        //when
        orderService.payCancel(cancelRequest, user);

        //then
        Pay canceledPay = payRepository.findByOrder(createdOrder)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_PAY));
        assertThat(createdOrder.getOrderState()).isEqualTo(OrderState.CANCEL);
        assertThat(canceledPay.getPayState()).isEqualTo(PayState.CANCEL);
    }

    @Test
    @DisplayName("주문 상세 조회 성공 테스트")
    void getOneOrder() {
        //given
        Order createdOrder = createOrder(member, "이다예", "01012345678", "Street 66", "상세주소", "요청메세지", OrderState.COMPLETE, 10000, merchantId);
        createPay("카드사", "카드번호", 10000, createdOrder);

        //when
        OrderResponse orderResponse = orderService.getOne(user, createdOrder.getOrderId());

        //then
        //주문 정보
        assertThat(orderResponse).isNotNull();
        assertThat(createdOrder.getReceiverName()).isEqualTo(orderResponse.name());
        assertThat(createdOrder.getReceiverPhone()).isEqualTo(orderResponse.phone());
        assertThat(createdOrder.getZipcode()).isEqualTo(orderResponse.zipcode());
        assertThat(createdOrder.getAddress()).isEqualTo(orderResponse.address());
        assertThat(createdOrder.getRequestMessage()).isEqualTo(orderResponse.requestMessage());
        assertThat(createdOrder.getTotalPrice()).isEqualTo(orderResponse.totalPrice());
        assertThat(createdOrder.getMerchantId()).isEqualTo(orderResponse.merchantId());
        assertThat(createdOrder.getOrderState()).isEqualTo(orderResponse.orderState());

        //결제 정보
        assertThat(orderResponse.cardCompany()).isEqualTo("카드사");
        assertThat(orderResponse.cardNum()).isEqualTo("카드번호");
        assertThat(orderResponse.totalPrice()).isEqualTo(10000);
        assertThat(orderResponse.cardNum()).isEqualTo("카드번호");

        //주문한 상품 개수 확인
        List<OrderItem> orderItems = orderItemRepository.findByOrder(createdOrder);
        assertThat(orderItems.size()).isEqualTo(orderResponse.orderedItems().size());

        //주문한 각 상품에 대한 검증
        for(int i=0; i<orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);
            OrderResponse.OrderedItem orderedItem = orderResponse.orderedItems().get(i);

            assertThat(orderItem.getOrderItemName()).isEqualTo(orderedItem.name());
            assertThat(orderItem.getOrderItemPrice()).isEqualTo(orderedItem.price());
            assertThat(orderItem.getOrderItemCount()).isEqualTo(orderedItem.count());
        }
    }

    @Test
    @DisplayName("주문 전체 조회 성공 테스트")
    void getAllOrder() {
        //given
        Order order1 = createOrder(member, "이다예", "01012345678", "Street 66", "상세주소", "요청메세지", OrderState.COMPLETE, 10000, merchantId);
        createPay("카드사1", "카드번호1", 10000, order1);

        Order order2 = createOrder(member, "이다예", "01012345678", "Street 66", "상세주소", "요청메세지", OrderState.COMPLETE, 10000, merchantId);
        createPay("카드사2", "카드번호2", 10000, order2);

        //when
        OrderPageResponse orderPageResponse = orderService.getAll(user, PageRequest.of(0,10));

        //then
        //페이징 테스트
        assertThat(orderPageResponse).isNotNull();
        assertThat(orderPageResponse.totalPage()).isEqualTo(1);
        assertThat(orderPageResponse.totalCount()).isEqualTo(2);
        assertThat(orderPageResponse.pageNumber()).isEqualTo(0);
        assertThat(orderPageResponse.currentPageSize()).isEqualTo(10);

        //주문 개수 확인
        List<OrderPageResponse.OrderList> orderList = orderPageResponse.OrderList();
        assertThat(orderList).hasSize(2);

        //주문한 상품 정보 확인
        OrderPageResponse.OrderList firstOrder = orderList.get(0); //상품 1
        assertThat(firstOrder.orderId()).isEqualTo(order1.getOrderId());
        assertThat(firstOrder.orderState()).isEqualTo(order1.getOrderState());

    }

    //카테고리 생성 메소드
    private Category createCategory(String categoryName) {
        return categoryRepository.save(Category.builder()
                .categoryName(categoryName)
                .build()
        );
    }

    //옵션 생성 메소드
    private ItemOption createOption(String key, String value) {
        List<ItemOption.Option> options = List.of(new ItemOption.Option(key, value));
        return itemOptionRepository.save(ItemOption.builder().optionValues(options).build());
    }

    //상품 생성 메소드
    private Item createItem(String itemName, int itemPrice, String itemDetail, int count, Category category, Member member, ItemOption itemOption, ItemState itemState) {
        return itemRepository.save(Item.builder()
                .itemName(itemName)
                .itemPrice(itemPrice)
                .itemDetail(itemDetail)
                .count(count)
                .category(category)
                .member(member)
                .itemOption(itemOption)
                .itemState(itemState)
                .build()
        );
    }

    //주문 생성 메소드
    private Order createOrder(Member member, String receiverName, String receiverPhone, String zipcode, String address, String requestMessage, OrderState orderState, int totalPrice, UUID merchantId) {
        return orderRepository.save(Order.builder()
                .member(member)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .zipcode(zipcode)
                .address(address)
                .requestMessage(requestMessage)
                .orderState(orderState)
                .totalPrice(totalPrice)
                .merchantId(merchantId)
                .build()
        );
    }

    //결제 생성 메소드
    private Pay createPay(String cardCompany, String cardNum, int payPrice, Order order) {
        return payRepository.save(Pay.builder()
                .cardCompany(cardCompany)
                .cardNum(cardNum)
                .order(order)
                .payPrice(payPrice)
                .build()
        );
    }

    //상품 재고 감소 메소드
    private void updateItemStock(Item item, int quantity) {
        int newStock = item.getCount() - quantity;
        item.updateStock(newStock);
        itemRepository.save(item);
    }

}
