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
import com.example.shoppingmall_comp.domain.orders.entity.Order;
import com.example.shoppingmall_comp.domain.orders.entity.OrderState;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import com.example.shoppingmall_comp.domain.orders.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("주문 서비스 테스트")
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;
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
}
