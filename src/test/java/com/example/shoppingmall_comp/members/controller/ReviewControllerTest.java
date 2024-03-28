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
    void setUp() {
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
        var url = "/api/reviews";

        var order = saveSuccessOrder();
        var orderItem = saveSuccessOrderItem(order);
        var request = new ReviewRequest("test review title", "test review content", 5, orderItem.getOrderItemId());
        var requestBody = objectMapper.writeValueAsString(request);

        // when
        var result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // when
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(jsonPath("$.content").value(request.content()))
                .andExpect(jsonPath("$.star").value(request.star()));
    }

    @Test
    @DisplayName("리뷰 수정 성공 테스트")
    @WithMockUser
    public void updateReview() throws Exception {
        //  given
        var url = "/api/reviews/{reviewId}";

        var order = saveSuccessOrder();
        var orderItem = saveSuccessOrderItem(order);
        var request = new ReviewRequest("test review title", "test review content", 5, orderItem.getOrderItemId());
        var requestBody = objectMapper.writeValueAsString(request);

        var savedReview = saveSuccessReview();

        // when
        var result = mockMvc.perform(patch(url, savedReview.getReviewId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    @WithMockUser
    public void deleteReview() throws Exception {
        //  given
        var url = "/api/reviews/{reviewId}";
        var savedReview = saveSuccessReview();

        // when
        var result = mockMvc.perform(delete(url, savedReview.getReviewId()));

        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("마이페이지에서 리뷰 전체 조회 성공 테스트")
    @WithMockUser
    public void findAllByMember() throws Exception {
        //  given
        var url = "/api/members/reviews";
        var savedReview = saveSuccessReview();

        // when
        var result = mockMvc.perform(get(url)
                .param("page", "0")
                .param("size", "15")
                .param("direction", "Sort.Direction.DESC"));

        // then
        result.andExpect(status().isOk())
                // 페이징 테스트
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.currentPageSize").value(15))
                // 리뷰 테스트
                .andExpect(jsonPath("$.responseList[0].title").value(savedReview.getReviewTitle()))
                .andExpect(jsonPath("$.responseList[0].content").value(savedReview.getReviewContent()))
                .andExpect(jsonPath("$.responseList[0].star").value(savedReview.getStar()));
    }

    @Test
    @DisplayName("아이템 상세페이지에서 리뷰 전체 조회 성공 테스트")
    @WithMockUser
    public void findAllByItem() throws Exception {
        //  given
        var url = "/api/items/{itemId}/reviews";
        var savedReview = saveSuccessReview();

        // when
        var result = mockMvc.perform(get(url, this.item.getItemId())
                .param("page", "0")
                .param("size", "15")
                .param("direction", "Sort.Direction.DESC"));

        // then
        result.andExpect(status().isOk())
                // 페이징 테스트
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.currentPageSize").value(15))
                // 리뷰 테스트
                .andExpect(jsonPath("$.responseList[0].title").value(savedReview.getReviewTitle()))
                .andExpect(jsonPath("$.responseList[0].content").value(savedReview.getReviewContent()))
                .andExpect(jsonPath("$.responseList[0].star").value(savedReview.getStar()));
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
