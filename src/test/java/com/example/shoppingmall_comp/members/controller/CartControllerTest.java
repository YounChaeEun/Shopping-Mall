package com.example.shoppingmall_comp.members.controller;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.CreateCartRequest;
import com.example.shoppingmall_comp.domain.members.dto.UpdateCartRequest;
import com.example.shoppingmall_comp.domain.members.entity.Cart;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.service.implement.CartServiceImpl;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("장바구니 컨트롤러 테스트")
public class CartControllerTest {

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
    MemberRepository memberRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartServiceImpl cartService;

    private Member member;
    private Item item;
    private Category category;

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
        this.item = createItem("상품명", 897000, "상세설명", 1000, category, member);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("장바구니 담기 컨트롤러 테스트")
    public void addCartTest() throws Exception {
        //given
        var cartOption = List.of(new CreateCartRequest.Option("색상","WHITE"));
        CreateCartRequest cartRequest = new CreateCartRequest(item.getItemId(), "상품명", 10, 897000, cartOption);

        //when
        mockMvc.perform(post("/api/carts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isCreated())

                //then
                .andExpect(jsonPath("$.itemName").value(cartRequest.itemName()))
                .andExpect(jsonPath("$.itemPrice").value(cartRequest.itemPrice()))
                .andExpect(jsonPath("$.itemCount").value(cartRequest.cartItemCount()));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("장바구니 수정 컨트롤러 테스트")
    public void updateCart() throws Exception {
        //given
        var cartOption = createItemOption();
        Cart cart = createCart(3, item, member, ItemState.ON_SALE, cartOption);
        UpdateCartRequest cartRequest = new UpdateCartRequest(item.getItemId(), 20);

        //when
        mockMvc.perform(patch("/api/carts/{cartId}", cart.getCartId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("장바구니 목록 조회 컨트롤러 테스트")
    public void getAllCarts() throws Exception {
        //given
        var cartOption = createItemOption();
        Cart createdCart = createCart(3, item, member, ItemState.ON_SALE, cartOption);

        //when
        mockMvc.perform(get("/api/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                //when
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.currentPageSize").value(10))

                //장바구니에 담겨있는 상품 정보 확인
                .andExpect(jsonPath("$.cartItems[0].itemName").value(createdCart.getItem().getItemName()))
                .andExpect(jsonPath("$.cartItems[0].itemPrice").value(createdCart.getItem().getItemPrice()))
                .andExpect(jsonPath("$.cartItems[0].itemCount").value(createdCart.getCount()))
                .andExpect(jsonPath("$.cartItems.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("선택한 장바구니 다중 삭제 컨트롤러 테스트")
    public void selectedDeleteCarts() throws Exception {
        //given
        var cartOption = createItemOption();
        Item item2 = createItem("상품명2", 10000, "상세설명", 100, category, member);
        Item item3 = createItem("상품명3", 10000, "상세설명", 100, category, member);

        Cart cart1 = createCart(3, item, member, ItemState.ON_SALE, cartOption);
        Cart cart2 = createCart(3, item2, member, ItemState.ON_SALE, cartOption);
        Cart cart3 = createCart(3, item3, member, ItemState.ON_SALE, cartOption);
        List<Long> cartIds = Arrays.asList(cart1.getCartId(), cart2.getCartId(), cart3.getCartId());

        //when
        mockMvc.perform(delete("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cartIds", cartIds.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andExpect(status().isNoContent());
    }

    //상품 옵션 생성 메소드
    private List<Cart.Option> createItemOption() {
        List<Cart.Option> option = new ArrayList<>();
        option.add(new Cart.Option("색상", "WHITE"));
        return option;
    }

    //상품 생성 메소드
    private Item createItem(String itemName, int itemPrice, String itemDetail, int count, Category category, Member member) {
        return itemRepository.save(Item.builder()
                .itemName(itemName)
                .itemPrice(itemPrice)
                .itemDetail(itemDetail)
                .count(count)
                .category(category)
                .itemOption(ItemOption.builder()
                        .optionValues(List.of(new ItemOption.Option("색상", "WHITE")))
                        .build())
                .member(member)
                .itemState(ItemState.ON_SALE)
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
