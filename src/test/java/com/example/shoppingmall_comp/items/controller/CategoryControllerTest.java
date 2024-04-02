package com.example.shoppingmall_comp.items.controller;

import com.example.shoppingmall_comp.domain.items.dto.CategoryRequest;
import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.service.implement.CategoryServiceImpl;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("카테고리 컨트롤러 테스트")
public class CategoryControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    CategoryServiceImpl categoryService;

    private Member member;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        //멤버 생성
        this.member = memberRepository.save(Member.builder()
                .email("user@gmail.com")
                .password("password")
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    @DisplayName("카테고리 생성 컨트롤러 테스트")
    public void addCategory() throws Exception {
        //given
        CategoryRequest categoryRequest = new CategoryRequest("전자제품");

        //when
        mockMvc.perform(post("/api/admin/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())

                //then
                .andExpect(jsonPath("$.categoryName").value(categoryRequest.categoryName()));
    }

    @Test
    @DisplayName("카테고리 수정 컨트롤러 테스트")
    public void updateCategory() throws Exception {
        //given
        Category category = createCategory("전자제품");
        CategoryRequest categoryRequest = new CategoryRequest("생활용품");

        //when
        mockMvc.perform(patch("/api/admin/categories/{categoryId}", category.getCategoryId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("카테고리 삭제 컨트롤러 테스트")
    public void deleteCategory() throws Exception {
        //given
        Category category = createCategory("전자제품");

        //when
        mockMvc.perform(delete("/api/admin/categories/{categoryId}", category.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    //카테고리 생성 메소드
    private Category createCategory(String categoryName) {
        return categoryRepository.save(Category.builder()
                .categoryName(categoryName)
                .build());
    }
}
