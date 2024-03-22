package com.example.shoppingmall_comp.members.service;

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
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.ReviewRepository;
import com.example.shoppingmall_comp.domain.members.service.ReviewService;
import com.example.shoppingmall_comp.domain.orders.entity.Order;
import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import com.example.shoppingmall_comp.domain.orders.repository.OrderItemRepository;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    MemberRepository memberRepository;
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

    private User user;
    private Pageable pageable;
    private Member member;
    private Item item;

    @BeforeEach
    void setUp() {
        this.user = new User("amy1234@naver.com", "Amy4021*", new ArrayList<>()); // username은 디비에 잇는 거 쓰기
        this.pageable = PageRequest.of(0, 15, Sort.Direction.DESC, "reviewId");
        this.member = memberRepository.findByEmail(user.getUsername()).get();

        Category category = categoryRepository.save(Category.builder().categoryName("test category name").build());
        List<ItemOption.Option> options = new ArrayList<>();
        options.add(new ItemOption.Option("색상", "빨강"));
        ItemOption itemOption = itemOptionRepository.save(ItemOption.builder().optionValues(options).build());
        this.item = itemRepository.save(Item.builder().itemName("test item name").itemPrice(10000).itemDetail("test item detail").count(1000).category(category).itemOption(itemOption).member(member).itemState(ItemState.ON_SALE).build());
    }

    @DisplayName("리뷰 생성 성공 테스트")
    @Test
    void create() {
        // given
        Order order = orderRepository.save(Order.builder()
                .receiverName("test receiver name")
                .receiverPhone("test receiver phone")
                .zipcode("test zipcode")
                .address("test address")
                .totalPrice(10000)
                .member(this.member)
                .merchantId(UUID.randomUUID())
                .build());

        OrderItem orderItem = orderItemRepository.save(OrderItem.builder()
                .memberId(this.member.getMemberId())
                .orderItemName("test order item name")
                .orderItemCount(10000)
                .orderItemPrice(10000)
                .order(order)
                .item(this.item)
                .build());

        var request = new ReviewRequest("test review title", "test review content", 3, orderItem.getOrderItemId());

        // when
        var response = reviewService.create(request, user);

        //then
        assertThat(response.title()).isEqualTo("test review title");
        assertThat(response.content()).isEqualTo("test review content");
        assertThat(response.star()).isEqualTo(3);
    }

    @DisplayName("리뷰 수정 성공 테스트")
    @Test
    void update() {
        // given
        Review savedReview = reviewRepository.save(Review.builder()
                .reviewTitle("test review title")
                .reviewContent("test review content")
                .star(3)
                .member(member)
                .item(item)
                .build());

        var newRequest = new ReviewRequest("test review new title", "test review new content", 5, 5L);

        // when
        reviewService.update(savedReview.getReviewId(), newRequest, user);
        var review = reviewRepository.findById(savedReview.getReviewId()).get();

        //then
        assertThat(review.getReviewTitle()).isEqualTo("test review new title");
        assertThat(review.getReviewContent()).isEqualTo("test review new content");
        assertThat(review.getStar()).isEqualTo(5);
    }

    @DisplayName("리뷰 삭제 성공 테스트")
    @Test
    void delete() {
        // given
        Review savedReview = reviewRepository.save(Review.builder()
                .reviewTitle("test review title")
                .reviewContent("test review content")
                .star(3)
                .member(member)
                .item(item)
                .build());

        // when
        reviewService.delete(savedReview.getReviewId(), user);

        // then
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews.size()).isEqualTo(8);

        Optional<Review> deletedReview = reviewRepository.findById(savedReview.getReviewId());
        assertThat(deletedReview.isPresent()).isFalse();
    }

    @DisplayName("상품의 리뷰 전체 조회 성공 테스트")
    @Test
    void getAllByItem() {
        // when
        var response = reviewService.getAllByItem(1L, pageable);

        // then
        assertThat(response.responseList().size()).isEqualTo(8);
        // 굳이 안해도 될 것 같긴 하지만.. pageable 검사 -> 해줘야 하나?
        assertThat(response.currentPageSize()).isEqualTo(15);
        assertThat(response.pageNumber()).isEqualTo(0);
        assertThat(response.totalCount()).isEqualTo(8);
        assertThat(response.totalPage()).isEqualTo(1);
    }

    @DisplayName("자신이 쓴 리뷰 전체 조회 성공 테스트")
    @Test
    void getAllByMember() {
        // when
        var response = reviewService.getAllByMember(user, pageable);

        // then
        assertThat(response.responseList().size()).isEqualTo(4);
        // 굳이 안해도 될 것 같긴 하지만.. pageable 검사
        assertThat(response.currentPageSize()).isEqualTo(15);
        assertThat(response.pageNumber()).isEqualTo(0);
        assertThat(response.totalCount()).isEqualTo(4);
        assertThat(response.totalPage()).isEqualTo(1);
    }
}