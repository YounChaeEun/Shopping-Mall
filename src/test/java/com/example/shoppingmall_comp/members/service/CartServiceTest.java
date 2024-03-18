package com.example.shoppingmall_comp.members.service;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemOptionRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.CartResponse;
import com.example.shoppingmall_comp.domain.members.dto.CreateCartRequest;
import com.example.shoppingmall_comp.domain.members.dto.UpdateCartRequest;
import com.example.shoppingmall_comp.domain.members.entity.*;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.service.implement.CartServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@DisplayName("장바구니 서비스 테스트")
public class CartServiceTest {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemOptionRepository itemOptionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartRepository cartRepository;

    private Item item;
    private ItemOption itemOption;
    private User user;
    private Category category;
    private Member member;

    @BeforeEach
    void setUp() {
        this.user = new User("test@naver.com", "dayeTest", new ArrayList<>());
        this.member = new Member("test@naver.com", "dayeTest", Role.builder().roleName(RoleName.SELLER).build());
        this.member = memberRepository.save(this.member);

        //카테고리 생성
        this.category = categoryRepository.save(Category.builder().categoryName("test category name").build());

        //옵션 생성
        List<ItemOption.Option> options = List.of(new ItemOption.Option("색상","WHITE"));
        itemOption = itemOptionRepository.save(ItemOption.builder().optionValues(options).build());

        //상품 생성
        this.item = itemRepository.save(Item.builder()
                        .itemName("노트북")
                        .itemPrice(897000)
                        .itemDetail("상품 상세 내용 test")
                        .count(1000)
                        .itemOption(itemOption)
                        .itemState(ItemState.ON_SALE)
                        .category(category)
                        .member(this.member)
                        .build());
    }

    @Test
    @DisplayName("장바구니 생성 성공 테스트")
    void addCart() {
        //given
        //setUp에 있는 내용은 성공, 실패 모두 사용 가능할 때임. 성공한 경우만은 given에 있는 것이 맞음
        CreateCartRequest cartRequest = new CreateCartRequest(
                item.getItemId(),
                "노트북",
                100,
                897000,
                List.of(new CreateCartRequest.Option("색상","WHITE"))
        );

        //when
        CartResponse cartResponse = cartService.create(cartRequest, user);

        //then
        Assertions.assertNotNull(cartResponse);
        Assertions.assertEquals(cartResponse.cartCount(), cartRequest.count());
        Assertions.assertEquals(cartResponse.itemId(), item.getItemId());
        Assertions.assertEquals(cartResponse.itemName(), item.getItemName());
        Assertions.assertFalse(cartResponse.optionValue().isEmpty());
        Assertions.assertTrue(cartResponse.optionValue().stream()
                .anyMatch(option -> option.key().equals("색상") && option.value().equals("WHITE")));
    }

    @Test
    @DisplayName("장바구니 수정 성공 테스트")
    void updateCart() {
        //given
        CreateCartRequest cartRequest = new CreateCartRequest(
                item.getItemId(),
                "노트북",
                100,
                897000,
                List.of(new CreateCartRequest.Option("색상","WHITE"))
        );
        CartResponse cartResponse = cartService.create(cartRequest, user);
        Cart cart = cartRepository.findById(cartResponse.cartId()).orElseThrow();

        UpdateCartRequest updateRequest = new UpdateCartRequest(
                item.getItemId(),
                200
        );

        //when
        cartService.update(cart.getCartId(), updateRequest, user);

        //then
        //장바구니 id 다시 가져와서 수정 내용 확인
        Cart updatedCart = cartRepository.findById(cartResponse.cartId()).orElseThrow();
        Assertions.assertEquals(updateRequest.count(), updatedCart.getCount());
        Assertions.assertEquals(cart.getItem(), updatedCart.getItem());
    }
}
