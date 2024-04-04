package com.example.shoppingmall_comp.orders.controller;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.orders.dto.OrderRequest;
import com.example.shoppingmall_comp.domain.orders.dto.PayCancelRequest;
import com.example.shoppingmall_comp.domain.orders.entity.Order;
import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import com.example.shoppingmall_comp.domain.orders.entity.OrderState;
import com.example.shoppingmall_comp.domain.orders.entity.Pay;
import com.example.shoppingmall_comp.domain.orders.repository.OrderItemRepository;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import com.example.shoppingmall_comp.domain.orders.repository.PayRepository;
import com.example.shoppingmall_comp.domain.orders.service.implement.OrderServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("주문 컨트롤러 통합 테스트")
public class OrderControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private PayRepository payRepository;
    @Autowired
    OrderServiceImpl orderService;

    private Member member;
    private Item item;
    private Category category;
    UUID merchantId = UUID.randomUUID();

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        //멤버 생성
        this.member = memberRepository.save(Member.builder()
                .email("user")
                .password("password")
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());

        //카테고리 생성
        this.category = categoryRepository.save(Category.builder()
                .categoryName("카테고리명")
                .build());

        //상품 생성
        this.item = itemRepository.save(Item.builder()
                .itemName("상품명")
                .itemPrice(897000)
                .itemDetail("상세 설명")
                .count(1000)
                .category(category)
                .itemOption(ItemOption.builder()
                        .optionValues(List.of(new ItemOption.Option("색상", "WHITE")))
                        .build())
                .member(member)
                .itemState(ItemState.ON_SALE)
                .build()
        );
    }

    @Test
    @WithMockUser
    @DisplayName("주문번호 생성 컨트롤러 테스트")
    public void generateOrderKey() throws Exception {
        mockMvc.perform(post("/api/order-keys"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderKey").exists());
    }
    @Test
    @WithMockUser(username = "user")
    @DisplayName("주문 생성 컨트롤러 테스트")
    public void createOrder() throws Exception {
        //given
        List<OrderItem.Option> options = List.of(new OrderItem.Option("색상", "WHITE"));
        List<OrderRequest.OrderedItem> orderedItems = List.of(new OrderRequest.OrderedItem(item.getItemId(), "상품명", 1, 897000, options));
        OrderRequest orderRequest = new OrderRequest("이름", "01012345678", "12345", "주소", "요청메시지", 897000, "카드사", "카드번호", orderedItems);

        //when
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())

                //then
                .andExpect(jsonPath("$.totalPrice").value(orderRequest.totalPrice()))
                .andExpect(jsonPath("$.orderedItems[0].itemId").value(orderRequest.orderedItems().get(0).itemId()));
        //검증 기준: 주문 같은 경우에는 dto 필드가 많은데, 이걸 하나하나 다 검증해야 하나?
    }

    @Test
    @WithMockUser
    @DisplayName("결제 취소 컨트롤러 테스트")
    public void deleteOrder() throws Exception {
        //given
        Order order = createOrder(member, "수취인", "01012345678", "주소", "address", "요청메세지", OrderState.COMPLETE, 897000, merchantId);
        createPay(member.getMemberId(), "카드사", "카드번호", 897000, order);
        PayCancelRequest cancelRequest = new PayCancelRequest(merchantId, order.getOrderId(), "구매 실수");

        //when
        mockMvc.perform(delete("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("주문 상세 조회 컨트롤러 테스트")
    public void getOneOrder() throws Exception {
        //given
        Order order = createOrder(member, "수취인", "01012345678", "주소", "address", "요청메세지", OrderState.COMPLETE, 897000, merchantId);
        createPay(member.getMemberId(), "카드사", "카드번호", 897000, order);
        OrderItem orderItem = createOrderItem(member.getMemberId(), item, "상품명", 897000, 2, order);

        mockMvc.perform(get("/api/orders/{orderId}", order.getOrderId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //then
                .andExpect(jsonPath("$.merchantId").value(order.getMerchantId().toString().trim()))
                .andExpect(jsonPath("$.totalPrice").value(order.getTotalPrice()))
                .andExpect(jsonPath("$.orderState").value(order.getOrderState().toString().trim()))
                .andExpect(jsonPath("$.orderedItems[0].count").value(orderItem.getOrderItemCount()))
                .andExpect(jsonPath("$.orderedItems[0].price").value(orderItem.getOrderItemPrice()));

    }

    @Test
    @WithMockUser
    @DisplayName("주문목록 전체 조회 컨트롤러 테스트")
    public void getAllOrders() throws Exception {
        //given
        Order order = createOrder(member, "수취인", "01012345678", "주소", "address", "요청메세지", OrderState.COMPLETE, 897000, merchantId);
        createPay(member.getMemberId(), "카드사", "카드번호", 897000, order);
        OrderItem orderItem = createOrderItem(member.getMemberId(), item, "상품명", 897000, 2, order);

        //when
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                //then
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.currentPageSize").value(20))

                .andExpect(jsonPath("$.OrderList.length()").value(1))
                .andExpect(jsonPath("$.OrderList[0].orderState").value(order.getOrderState().toString().trim()))
                .andExpect(jsonPath("$.OrderList[0].orderItemList[0].name").value(orderItem.getOrderItemName()))
                .andExpect(jsonPath("$.OrderList[0].orderItemList[0].count").value(orderItem.getOrderItemCount()));

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

    //주문 상품 생성 메소드
    private OrderItem createOrderItem(Long memberId, Item item, String orderItemName, int orderItemPrice, int orderItemCount, Order order) {
        return orderItemRepository.save(OrderItem.builder()
                .memberId(memberId)
                .item(item)
                .orderItemName(orderItemName)
                .orderItemPrice(orderItemPrice)
                .orderItemCount(orderItemCount)
                .order(order)
                .build()
        );
    }

    //결제 생성 메소드
    private Pay createPay(Long memberId, String cardCompany, String cardNum, int payPrice, Order order) {
        return payRepository.save(Pay.builder()
                .memberId(memberId)
                .cardCompany(cardCompany)
                .cardNum(cardNum)
                .order(order)
                .payPrice(payPrice)
                .build()
        );
    }

}
