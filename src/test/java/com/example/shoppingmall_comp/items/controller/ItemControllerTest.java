package com.example.shoppingmall_comp.items.controller;


import com.amazonaws.HttpMethod;
import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
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
    @WithMockUser(username = "amy4021123@naver.com")
    void addItem() throws Exception {

        // given
        var url = "/api/seller/items";
        var options = createItemOption();
        var itemRequest = new ItemRequest("test item name2", category.getCategoryId(), 10000, 10000, options, "test item description");
        var request = new MockMultipartFile("itemRequest", "itemRequest", "application/json", objectMapper.writeValueAsString(itemRequest).getBytes(StandardCharsets.UTF_8));
        var file = new MockMultipartFile("file", "file.jpg", "multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));

        // when
        ResultActions result = mockMvc.perform(multipart(url, HttpMethod.POST)
                .file(file)
                .file(request)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isCreated());
    }

    private List<ItemRequest.Option> createItemOption() {
        List<ItemRequest.Option> options = new ArrayList<>(); // 이거 var로 바뀌면 오류남 왠지 파악하기!
        options.add(new ItemRequest.Option("색상", "빨강"));
        options.add(new ItemRequest.Option("사이즈", "large"));
        return options;
    }
//
//    private MockMultipartFile createItemImage() {
//        return new MockMultipartFile("file.jpg", "file.jpg", "multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));
//    }
//
//    private List<MultipartFile> createSuccessItemImage() {
//        List<MultipartFile> multipartFiles = new ArrayList<>();
//        MockMultipartFile multipartFile1 = new MockMultipartFile("file.jpg", "file.jpg", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
//        MockMultipartFile multipartFile2 = new MockMultipartFile("file.jpg", "file.jpg", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
//        multipartFiles.add(multipartFile1);
//        multipartFiles.add(multipartFile2);
//        return multipartFiles;
//    }
}
