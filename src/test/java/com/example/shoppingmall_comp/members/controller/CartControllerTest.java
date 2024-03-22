package com.example.shoppingmall_comp.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.CartResponse;
import com.example.shoppingmall_comp.domain.members.dto.CreateCartRequest;

import com.example.shoppingmall_comp.domain.members.dto.UpdateCartRequest;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

}
