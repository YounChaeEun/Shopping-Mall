package com.example.shoppingmall_comp.members.controller;

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
import com.example.shoppingmall_comp.domain.members.entity.Cart;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.service.implement.CartServiceImpl;
import com.example.shoppingmall_comp.factory.CartFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
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
    private ItemOptionRepository itemOptionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartRepository cartRepository;
    @MockBean
    CartServiceImpl cartService;

    private User user;
    private Member member;
    private Item item;
    private ItemOption itemOption;
    private Category category;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        this.user = new User("test@naver.com", "dayeTest", new ArrayList<>());
        this.member = new Member("test@naver.com", "dayeTest", Role.builder().roleName(RoleName.SELLER).build());
        this.member = memberRepository.save(this.member);

        //카테고리 생성
        this.category = categoryRepository.save(Category.builder().categoryName("test category name").build());

        //옵션 생성
//        List<ItemOption.Option> options = List.of(new ItemOption.Option("색상","WHITE"));
//        ItemOption itemOption = itemOptionRepository.save(ItemOption.builder().optionValues(options).build());
//        this.itemOption = createItemOption(List.of(new ItemOption.Option("색상","WHITE")));

        //상품 생성
        this.item = createItem("노트북", 897000, "상품 상세설명 test", 1000, category, member, null, ItemState.ON_SALE);

    }

    @Test
    @DisplayName("장바구니 담기 컨트롤러 테스트")
    public void addCartTest() throws Exception {
        CreateCartRequest cartRequest = CartFactory.createMockCreateCartRequest();

        createCart(3, item, member, ItemState.ON_SALE, null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("장바구니 수정 컨트롤러 테스트")
    public void updateCart() throws Exception {
        Long cartId = 1L;
        UpdateCartRequest cartRequest = CartFactory.createMockUpdateRequest();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/carts/{cartId}", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("장바구니 목록 조회 컨트롤러 테스트")
    public void getAllCarts() throws Exception {
        //given
        Item item2 = createItem("키보드", 37000, "상품 상세설명2 test", 2000, category, member, null, ItemState.ON_SALE);
        createCart(3, item, member, ItemState.ON_SALE, null);

        List<CartResponse> cartItems = Arrays.asList(
                new CartResponse(1L, 1, 1L, "니트", 38000, ItemState.ON_SALE, null),
                new CartResponse(2L, 1, 2L, "키보드", 37000, ItemState.ON_SALE, null)
        );
        CartPageResponse pageResponse = new CartPageResponse(1, 2, 0, 10, cartItems);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        //then
        assertThat(pageResponse.totalCount()).isEqualTo(2);

    }

    @Test
    @DisplayName("선택한 장바구니 다중 삭제 컨트롤러 테스트")
    public void selectedDeleteCarts() throws Exception {
        List<Long> cartIds = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cartIds", cartIds.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    //상품 옵션 생성 메소드
    private ItemOption createItemOption(List<ItemOption.Option> optionValues) {
        return itemOptionRepository.save(ItemOption.builder()
                .optionValues(optionValues)
                .build()
        );
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
