package com.example.shoppingmall_comp.members.controller;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemOptionRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.ReviewRequest;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Review;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.ReviewRepository;
import com.example.shoppingmall_comp.domain.orders.entity.Order;
import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import com.example.shoppingmall_comp.domain.orders.repository.OrderItemRepository;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReviewControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ReviewRepository reviewRepository;

    private Member member;
    private Item item;

    @BeforeEach
    void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        // 멤버 생성
        this.member = memberRepository.save(Member.builder()
                .email("user")
                .password("1234")
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());

        // 카테고리 생성
        Category category = categoryRepository.save(Category.builder()
                .categoryName("test category name")
                .build());

        // 상품 생성
        this.item = itemRepository.save(Item.builder()
                .itemName("test item name")
                .itemPrice(10000)
                .itemDetail("test item detail")
                .count(1000)
                .category(category)
                .itemOption(ItemOption.builder()
                        .optionValues(List.of(new ItemOption.Option("색상", "빨강")))
                        .build())
                .member(member)
                .itemState(ItemState.ON_SALE)
                .build());
    }

    @Test
    @DisplayName("리뷰 생성 성공 테스트")
    @WithMockUser
    public void addReview() throws Exception {
        // given
        String url = "/api/reviews";

        var order = saveSuccessOrder();
        var orderItem = saveSuccessOrderItem(order);
        ReviewRequest request = new ReviewRequest("test review title", "test review content", 5, orderItem.getOrderItemId());
        String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // when
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(jsonPath("$.content").value(request.content()))
                .andExpect(jsonPath("$.star").value(request.star()));
    }


    private Review saveSuccessReview() {
        return reviewRepository.save(Review.builder()
                .reviewTitle("test review title")
                .reviewContent("test review content")
                .star(3)
                .member(member)
                .item(item)
                .build());
    }

    private Order saveSuccessOrder() {
        return orderRepository.save(Order.builder()
                .receiverName("test receiver name")
                .receiverPhone("test receiver phone")
                .zipcode("test zipcode")
                .address("test address")
                .totalPrice(10000)
                .member(this.member)
                .merchantId(UUID.randomUUID())
                .build());
    }

    private OrderItem saveSuccessOrderItem(Order order) {
        return orderItemRepository.save(OrderItem.builder()
                .memberId(this.member.getMemberId())
                .orderItemName("test order item name")
                .orderItemCount(10000)
                .orderItemPrice(10000)
                .order(order)
                .item(this.item)
                .build());
    }
}
