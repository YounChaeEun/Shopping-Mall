package com.example.shoppingmall_comp.members.service;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemOptionRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.CartPageResponse;
import com.example.shoppingmall_comp.domain.members.dto.CartResponse;
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
import org.springframework.data.domain.PageRequest;
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
        this.item = createItem("노트북", 897000, "상품 상세설명 test", 1000, category, member,itemOption, ItemState.ON_SALE);
    }

    @Test
    @DisplayName("장바구니 생성 성공 테스트")
    void addCart() {
        //when
        Cart createdCart = createCart(100, item, member, ItemState.ON_SALE, List.of(new Cart.Option("색상", "WHITE")));

        //then
        Assertions.assertNotNull(createdCart);
        Assertions.assertEquals(createdCart.getCount(), 100);
        Assertions.assertEquals(createdCart.getItem().getItemId(), item.getItemId());
        Assertions.assertEquals(createdCart.getItem().getItemName(), item.getItemName());
        Assertions.assertTrue(createdCart.getOptionValues().stream()
                .anyMatch(option -> option.key().equals("색상") && option.value().equals("WHITE")));
    }

    @Test
    @DisplayName("장바구니 수정 성공 테스트")
    void updateCart() {
        //given
        Cart createdCart = createCart(100, item, member, ItemState.ON_SALE, List.of(new Cart.Option("색상", "WHITE")));
        UpdateCartRequest updateRequest = new UpdateCartRequest(item.getItemId(), 200);

        //when
        cartService.update(createdCart.getCartId(), updateRequest, user);

        //then
        //장바구니 id 다시 가져와서 수정 내용 확인
        Cart updatedCart = cartRepository.findById(createdCart.getCartId()).orElseThrow();
        Assertions.assertEquals(updateRequest.count(), updatedCart.getCount());
        Assertions.assertEquals(item, updatedCart.getItem());
    }

    @Test
    @DisplayName("장바구니 전체 조회")
    void getAllCart() {
        //given
        // 장바구니에 상품 추가
        Cart createdCart = createCart(100, item, member, ItemState.ON_SALE, List.of(new Cart.Option("색상", "WHITE")));

        //when
        CartPageResponse cartPageResponse = cartService.getAll(PageRequest.of(0,10), user);

        //then
        //페이징 테스트
        Assertions.assertNotNull(cartPageResponse);
        Assertions.assertEquals(1,cartPageResponse.totalPage());
        Assertions.assertEquals(1, cartPageResponse.totalCount());
        Assertions.assertEquals(0, cartPageResponse.pageNumber());
        Assertions.assertEquals(10, cartPageResponse.currentPageSize());

        //장바구니 추가된 상품들 개수 확인
        List<CartResponse> cartItems = cartPageResponse.cartItems();
        Assertions.assertEquals(1, cartItems.size());

        //장바구니의 상품 정보 확인
        CartResponse cartItem = cartItems.get(0);
        Assertions.assertEquals(createdCart.getCartId(), cartItem.cartId());
        Assertions.assertEquals(createdCart.getCount(), cartItem.cartCount());
        Assertions.assertEquals(item.getItemId(), cartItem.itemId());
        Assertions.assertEquals(item.getItemName(), cartItem.itemName());
        Assertions.assertTrue(cartItem.optionValue().stream()
                .anyMatch(option -> option.key().equals("색상") && option.value().equals("WHITE")));
    }

    @Test
    @DisplayName("선택한 장바구니들 삭제")
    void deleteCarts() {
        //given
        //상품 추가
        Item item2 = createItem("책상", 21000, "책상 상세설명", 20, category, member, itemOption, ItemState.ON_SALE);

        //첫번째, 두번째 장바구니 추가
        Cart cart1 = createCart(100, item, member, ItemState.ON_SALE, List.of(new Cart.Option("색상", "WHITE")));
        Cart cart2 = createCart(10, item2, member, ItemState.ON_SALE, List.of(new Cart.Option("색상", "BLACK")));

        //when
        List<Long> cartIdsDelete = List.of(cart1.getCartId());
        cartService.deleteSelectedCarts(cartIdsDelete, user);

        //then
        //첫번째 장바구니 삭제 확인
        Assertions.assertFalse(cartRepository.existsById(cart1.getCartId()));

        //남은 장바구니들 몇개 있는지
        List<Cart> remainCarts = cartRepository.findAll();
        Assertions.assertEquals(1, remainCarts.size());

        //남은 장바구니 상품 맞는지 조회
        Cart remainCart = remainCarts.get(0);
        Assertions.assertEquals(cart2.getCartId(), remainCart.getCartId());
        Assertions.assertEquals(cart2.getCount(), remainCart.getCount());
        Assertions.assertEquals(cart2.getItem().getItemName(), remainCart.getItem().getItemName());
        Assertions.assertTrue(remainCart.getOptionValues().stream()
                .anyMatch(option -> option.key().equals("색상") && option.value().equals("BLACK")));

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

    //장바구니 생성 메소드
    private Cart createCart(int count, Item item, Member member, ItemState itemState, List<Cart.Option> optionValues) {
        return cartRepository.save(Cart.builder()
                .count(count)
                .item(item)
                .member(member)
                .itemState(itemState)
                .optionValues(optionValues)
                .build()
        );
    }

}
