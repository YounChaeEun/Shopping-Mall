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
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

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
        this.item = createItem("노트북", 897000, "상품 상세설명 test", 1000, category, member, itemOption, ItemState.ON_SALE);
    }

    @Test
    @DisplayName("장바구니 생성 성공 테스트")
    void addCart() {
        //given
        CreateCartRequest cartRequest = new CreateCartRequest(item.getItemId(),item.getItemName(), 10, 897000, List.of(new CreateCartRequest.Option("색상", "WHITE")));

        //when
        CartResponse cart = cartService.create(cartRequest, user);

        //then
        assertThat(cart).isNotNull();
        assertThat(cart.itemCount()).isEqualTo(cartRequest.cartItemCount());
        assertThat(cart.itemId()).isEqualTo(cartRequest.itemId());
        assertThat(cart.itemName()).isEqualTo(cartRequest.itemName());
        assertThat(cart.optionValue().stream()
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
        Cart updatedCart = cartRepository.findById(createdCart.getCartId()).orElseThrow();
        assertThat(updatedCart.getCount()).isEqualTo(updateRequest.count());
    }

    @Test
    @DisplayName("장바구니 전체 조회")
    void getAllCart() {
        //given
        Cart cart = createCart(100, item, member, ItemState.ON_SALE, List.of(new Cart.Option("색상", "WHITE")));

        //when
        CartPageResponse cartPageResponse = cartService.getAll(PageRequest.of(0,10), user);

        //then
        //페이징 테스트
        assertThat(cartPageResponse).isNotNull();
        assertThat(cartPageResponse.totalPage()).isEqualTo(1);
        assertThat(cartPageResponse.totalCount()).isEqualTo(1);
        assertThat(cartPageResponse.pageNumber()).isEqualTo(0);
        assertThat(cartPageResponse.currentPageSize()).isEqualTo(10);

        //장바구니 추가된 상품들 개수 확인
        assertThat(cartPageResponse.cartItems()).hasSize(1);

        //장바구니의 상품 정보 확인
        CartResponse cartItem = cartPageResponse.cartItems().get(0);
        assertThat(cartItem.cartId()).isEqualTo(cart.getCartId());
        assertThat(cartItem.itemCount()).isEqualTo(cart.getCount());
        assertThat(cartItem.itemId()).isEqualTo(item.getItemId());
        assertThat(cartItem.itemName()).isEqualTo(item.getItemName());
        assertThat(cartItem.optionValue()).anyMatch(option ->
                option.key().equals("색상") && option.value().equals("WHITE"));
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
        assertThat(cartRepository.existsById(cart1.getCartId())).isFalse();

        //남은 장바구니들 몇개 있는지
        List<Cart> remainCarts = cartRepository.findAll();
        assertThat(remainCarts).hasSize(1);

        //남은 장바구니 상품 맞는지 조회
        Cart remainCart = remainCarts.get(0);
        assertThat(remainCart.getCartId()).isEqualTo(cart2.getCartId());
        assertThat(remainCart.getCount()).isEqualTo(cart2.getCount());
        assertThat(remainCart.getItem().getItemName()).isEqualTo(cart2.getItem().getItemName());
        assertThat(remainCart.getOptionValues()).anyMatch(option ->
                option.key().equals("색상") && option.value().equals("BLACK"));
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
