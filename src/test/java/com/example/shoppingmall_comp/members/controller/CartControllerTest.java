package com.example.shoppingmall_comp.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.CartPageResponse;
import com.example.shoppingmall_comp.domain.members.dto.CartResponse;
import com.example.shoppingmall_comp.domain.members.dto.CreateCartRequest;

import com.example.shoppingmall_comp.domain.members.dto.UpdateCartRequest;
import com.example.shoppingmall_comp.domain.members.service.implement.CartServiceImpl;
import com.example.shoppingmall_comp.factory.CartFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    CartServiceImpl cartService;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("장바구니 담기 컨트롤러 테스트")
    public void addCartTest() throws Exception {
        CreateCartRequest request = CartFactory.createMockCreateCartRequest();
        CartResponse response = CartFactory.createMockCartResponse();

        when(cartService.create(any(CreateCartRequest.class), any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartId").value(response.cartId()));
    }

    @Test
    @DisplayName("장바구니 수정 컨트롤러 테스트")
    public void updateCart() throws Exception {
        Long cartId = 1L;
        UpdateCartRequest request = CartFactory.createMockUpdateRequest();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/carts/{cartId}", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("장바구니 목록 조회 컨트롤러 테스트")
    public void getAllCarts() throws Exception {
        CartPageResponse pageResponse = CartFactory.createMockCartPageResponse();

        when(cartService.getAll(any(Pageable.class), any())).thenReturn(pageResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPage").value(pageResponse.totalPage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalCount").value(pageResponse.totalCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNumber").value(pageResponse.pageNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPageSize").value(pageResponse.currentPageSize()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartItems.length()").value(pageResponse.cartItems().size()));
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
}
