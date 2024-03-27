package com.example.shoppingmall_comp.items.controller;


import com.amazonaws.HttpMethod;
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
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        var options = createItemOption();
        var itemRequest = new ItemRequest("test item name2", category.getCategoryId(), 10000, 10000, options, "test item description");
        var request = new MockMultipartFile("itemRequest", "itemRequest", "application/json", objectMapper.writeValueAsString(itemRequest).getBytes(StandardCharsets.UTF_8));
        var file = new MockMultipartFile("file", "file.jpg", "multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));

        // when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .file(request)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("상품 수정 컨트롤러 성공 테스트")
    @WithMockUser(username = "amy4021123@naver.com")
    void updateItem() throws Exception {
        // given
        var url = "/api/seller/items/{itemId}";
        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);
        var options = createUpdateItemOption();
        var itemRequest = new UpdateItemRequest("test updated item name", category.getCategoryId(), 10000, 10000, options, ItemState.ON_SALE, "test updated item description");
        var request = new MockMultipartFile("itemRequest", "itemRequest", "application/json", objectMapper.writeValueAsString(itemRequest).getBytes(StandardCharsets.UTF_8));
        var file = new MockMultipartFile("file", "file.jpg", "multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));

        // when
        ResultActions result = mockMvc.perform(multipart(PATCH, url, item.getItemId())
                .file(file)
                .file(request)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
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
        ResultActions result = mockMvc.perform(delete(url, item.getItemId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNoContent());
    }

    private List<ItemRequest.Option> createItemOption() {
        List<ItemRequest.Option> options = new ArrayList<>(); // 이거 var로 바뀌면 오류남 왠지 파악하기!
        options.add(new ItemRequest.Option("색상", "빨강"));
        options.add(new ItemRequest.Option("사이즈", "large"));
        return options;
    }

    private List<UpdateItemRequest.Option> createUpdateItemOption() {
        List<UpdateItemRequest.Option> options = new ArrayList<>(); // 이거 var로 바뀌면 오류남 왠지 파악하기!
        options.add(new UpdateItemRequest.Option("색상", "빨강"));
        options.add(new UpdateItemRequest.Option("사이즈", "large"));
        return options;
    }

    private ItemOption saveSuccessItemOption(){
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
