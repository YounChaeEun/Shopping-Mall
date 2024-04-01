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
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    private Member member;
    private Item item;

    // 질문: EntityManager persist
    @BeforeEach
    void setUp() {
        // 멤버 생성
        this.member = memberRepository.save(Member.builder()
                .email("amy4021123@naver.com")
                .password("1234")
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());

        // User 생성
        this.user = new User("amy4021123@naver.com", "1234", new ArrayList<>());

        // 카테고리 생성
        Category category = categoryRepository.save(Category.builder()
                .categoryName("test category name")
                .build());

        // 상품 옵션 생성
        ItemOption itemOption = itemOptionRepository.save(ItemOption.builder()
                .optionValues(List.of(new ItemOption.Option("색상", "빨강")))
                .build());

        // 상품 생성
        this.item = itemRepository.save(Item.builder()
                .itemName("test item name")
                .itemPrice(10000)
                .itemDetail("test item detail")
                .count(1000)
                .category(category)
                .itemOption(itemOption)
                .member(member)
                .itemState(ItemState.ON_SALE)
                .build());
    }

    @DisplayName("리뷰 생성 성공 테스트")
    @Test
    void create() {
        // given
        var order = saveSuccessOrder();
        var orderItem = saveSuccessOrderItem(order);
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
        var order = saveSuccessOrder();
        var orderItem = saveSuccessOrderItem(order);
        var savedReview = saveSuccessReview();
        var newRequest = new ReviewRequest("test review new title", "test review new content", 5, orderItem.getOrderItemId());

        // when
        reviewService.update(savedReview.getReviewId(), newRequest, user);
        var review = reviewRepository.findById(savedReview.getReviewId()).orElseThrow();

        //then
        assertThat(review.getReviewTitle()).isEqualTo("test review new title");
        assertThat(review.getReviewContent()).isEqualTo("test review new content");
        assertThat(review.getStar()).isEqualTo(5);
    }

    @DisplayName("리뷰 삭제 성공 테스트")
    @Test
    void delete() {
        // given
        var savedReview = saveSuccessReview();

        // when
        reviewService.delete(savedReview.getReviewId(), user);

        // then
        var reviews = reviewRepository.findAllByMember(this.member);
        assertThat(reviews).isEmpty();

        var deletedReview = reviewRepository.findById(savedReview.getReviewId());
        assertThat(deletedReview.isPresent()).isFalse();
    }

    @DisplayName("상품의 리뷰 전체 조회 성공 테스트")
    @Test
    void getAllByItem() {
        // given
        var savedReview = saveSuccessReview();
        var pageable = PageRequest.of(0, 15, Sort.Direction.DESC, "reviewId");

        // when
        var response = reviewService.getAllByItem(item.getItemId(), pageable);

        // then
        // 페이징 테스트
        assertThat(response.currentPageSize()).isEqualTo(15);
        assertThat(response.pageNumber()).isEqualTo(0);
        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.totalPage()).isEqualTo(1);

        // 리뷰 테스트
        assertThat(response.responseList().size()).isEqualTo(1);
        var firstReview = response.responseList().get(0);
        assertThat(firstReview.title()).isEqualTo(savedReview.getReviewTitle());
        assertThat(firstReview.content()).isEqualTo(savedReview.getReviewContent());
    }

    @DisplayName("자신이 쓴 리뷰 전체 조회 성공 테스트")
    @Test
    void getAllByMember() {
        // given
        var savedReview = saveSuccessReview();
        var pageable = PageRequest.of(0, 15, Sort.Direction.DESC, "reviewId");

        // when
        var response = reviewService.getAllByMember(user, pageable);

        // then
        // 페이징 테스트
        assertThat(response.currentPageSize()).isEqualTo(15);
        assertThat(response.pageNumber()).isEqualTo(0);
        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.totalPage()).isEqualTo(1);

        // 리뷰 테스트
        assertThat(response.responseList().size()).isEqualTo(1);
        var firstReview = response.responseList().get(0);
        assertThat(firstReview.title()).isEqualTo(savedReview.getReviewTitle());
        assertThat(firstReview.content()).isEqualTo(savedReview.getReviewContent());

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
