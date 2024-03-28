package com.example.shoppingmall_comp.items.controller;


import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.dto.UpdateItemRequest;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemOptionRepository itemOptionRepository;

    private Category category;
    private Member member;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        // 카테고리 생성
        this.category = categoryRepository.save(Category.builder()
                .categoryName("test category name")
                .build());

        // 멤버 생성
        this.member = memberRepository.save(Member.builder()
                .email("amy4021123@naver.com")
                .password("1234")
                .role(Role.builder()
                        .roleName(RoleName.SELLER)
                        .build())
                .build());
    }

    @Test
    @DisplayName("상품 생성 컨트롤러 성공 테스트")
    @WithMockUser(username = "amy4021123@naver.com")
    void addItem() throws Exception {
        // given
        var url = "/api/seller/items";

        var options = List.of(new ItemRequest.Option("색상", "빨강"), new ItemRequest.Option("사이즈", "large"));
        var itemRequest = new ItemRequest("test item name2", category.getCategoryId(), 10000, 10000, options, "test item description");
        var request = new MockMultipartFile("itemRequest", "itemRequest", "application/json", objectMapper.writeValueAsString(itemRequest).getBytes(StandardCharsets.UTF_8));
        var file = new MockMultipartFile("file", "file.jpg", "multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));

        // when
        var result = mockMvc.perform(multipart(url)
                .file(file)
                .file(request)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemName").value(itemRequest.itemName()))
                .andExpect(jsonPath("$.price").value(itemRequest.price()))
                .andExpect(jsonPath("$.count").value(itemRequest.count()))
                .andExpect(jsonPath("$.description").value(itemRequest.description()))
                .andExpect(jsonPath("$.imgUrls").isNotEmpty())
                .andExpect(jsonPath("$.optionValue[0].value").value("빨강"));
    }

    @Test
    @DisplayName("상품 수정 컨트롤러 성공 테스트")
    @WithMockUser(username = "amy4021123@naver.com")
    void updateItem() throws Exception {
        // given
        var url = "/api/seller/items/{itemId}";

        var options = List.of(new UpdateItemRequest.Option("색상", "빨강"), new UpdateItemRequest.Option("사이즈", "large"));
        var itemRequest = new UpdateItemRequest("test updated item name", category.getCategoryId(), 10000, 10000, options, ItemState.ON_SALE, "test updated item description");
        var request = new MockMultipartFile("itemRequest", "itemRequest", "application/json", objectMapper.writeValueAsString(itemRequest).getBytes(StandardCharsets.UTF_8));
        var file = new MockMultipartFile("file", "file.jpg", "multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));

        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);

        // when
        var result = mockMvc.perform(multipart(PATCH, url, item.getItemId())
                .file(file)
                .file(request)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemImageId").isNotEmpty())
                .andExpect(jsonPath("$.itemUrls").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 삭제 컨트롤러 성공 테스트")
    @WithMockUser(username = "amy4021123@naver.com")
    void deleteItem() throws Exception {
        // given
        var url = "/api/seller/items/{itemId}";

        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);

        // when
        var result = mockMvc.perform(delete(url, item.getItemId()));

        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("판매자의 상품 전체 조회 컨트롤러 성공 테스트")
    @WithMockUser(username = "amy4021123@naver.com")
    void getSellerItems() throws Exception {
        // given
        var url = "/api/seller/items";

        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);

        // when
        var result = mockMvc.perform(get(url)
                .param("size", "15"));

        // then
        result.andExpect(status().isOk())
                // 상품 테스트
                .andExpect(jsonPath("$.sellerItemList[0].itemName").value(item.getItemName()))
                .andExpect(jsonPath("$.sellerItemList[0].price").value(item.getItemPrice()))
                .andExpect(jsonPath("$.sellerItemList[0].count").value(item.getCount()))
                // 페이징 테스트
                .andExpect(jsonPath("$.currentPageSize").value("15"));
    }

    @Test
    @DisplayName("상품 전체 조회 컨트롤러 성공 테스트")
    @WithMockUser(username = "amy4021123@naver.com")
    void getAllItems() throws Exception {
        // given
        var url = "/api/items";

        // 카테고리에 속한 상품이 없으면 에러가 남, 그걸 위해 카테고리에 상품 하나 만들어준다.
        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);

        // when
        var result = mockMvc.perform(get(url)
                .param("size", "15")
                .param("categoryId", String.valueOf(this.category.getCategoryId())));

        // then
        result.andExpect(status().isOk())
                // 상품 검사
                .andExpect(jsonPath("$.itemList[0].itemName").value(item.getItemName()))
                .andExpect(jsonPath("$.itemList[0].price").value(item.getItemPrice()))
                // 페이징 테스트
                .andExpect(jsonPath("$.currentPageSize").value("15"));
    }

    @Test
    @DisplayName("상품 상세 조회 컨트롤러 성공 테스트")
    @WithMockUser(username = "amy4021123@naver.com")
    void getItemDetail() throws Exception {
        // given
        var url = "/api/items/{itemId}";

        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);

        // when
        var result = mockMvc.perform(get(url, item.getItemId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value(item.getItemName()))
                .andExpect(jsonPath("$.price").value(item.getItemPrice()))
                .andExpect(jsonPath("$.description").value(item.getItemDetail()))
                .andExpect(jsonPath("$.optionValue[0].value").value("빨강"));
    }

    private ItemOption saveSuccessItemOption() {
        List<ItemOption.Option> options = new ArrayList<>();
        options.add(new ItemOption.Option("색상", "빨강"));
        options.add(new ItemOption.Option("사이즈", "large"));
        return itemOptionRepository.save(ItemOption.builder()
                .optionValues(options)
                .build());
    }

    private Item saveSuccessItem(ItemOption itemOption) {
        return itemRepository.save(Item.builder()
                .itemName("test item name")
                .itemPrice(10000)
                .itemDetail("test item detail")
                .count(10000)
                .itemState(ItemState.ON_SALE)
                .category(this.category)
                .itemOption(itemOption)
                .member(this.member)
                .build());
    }
}
