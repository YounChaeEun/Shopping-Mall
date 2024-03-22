package com.example.shoppingmall_comp.orders.controller;

import com.example.shoppingmall_comp.domain.orders.dto.OrderRequest;
import com.example.shoppingmall_comp.domain.orders.dto.OrderResponse;
import com.example.shoppingmall_comp.domain.orders.dto.PayCancelRequest;
import com.example.shoppingmall_comp.domain.orders.entity.OrderState;
import com.example.shoppingmall_comp.domain.orders.service.OrderService;
import com.example.shoppingmall_comp.domain.orders.service.implement.OrderServiceImpl;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    OrderServiceImpl orderService;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("주문번호 생성 컨트롤러 테스트")
    public void generateOrderKey() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/order-keys"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderKey").exists());
    }
    @Test
    @WithMockUser
    @DisplayName("주문 생성 컨트롤러 테스트")
    public void createOrder() throws Exception {
        OrderResponse mockOrderResponse = createMockOrderResponse();
        when(orderService.create(any(OrderRequest.class), any())).thenReturn(mockOrderResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMockOrderRequest())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(mockOrderResponse.orderId()));
    }

    @Test
    @WithMockUser
    @DisplayName("결제 취소 컨트롤러 테스트")
    public void testDeleteOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMockPayCancelRequest())))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private OrderRequest createMockOrderRequest() {
        List<OrderRequest.OrderedItem> orderedItems = Arrays.asList(
                new OrderRequest.OrderedItem(1L, "노트북", 1, 897000, null)
        );
        return new OrderRequest("홍길동", "01012345678", "12345", "주소", "요청메시지", 897000, "카드사", "카드번호", orderedItems);
    }

    private PayCancelRequest createMockPayCancelRequest() {
        return new PayCancelRequest(UUID.randomUUID(), 1L, "취소사유");
    }

    private OrderResponse createMockOrderResponse() {
        List<OrderResponse.OrderedItem> orderedItems = Arrays.asList(
                new OrderResponse.OrderedItem(1L, "노트북", 1, 897000, null)
        );
        return new OrderResponse(1L, "name", "01012345678", "12345", "주소", "요청메시지", 897000,
                UUID.randomUUID(), OrderState.COMPLETE, "카드사", "카드번호", orderedItems);
    }

}
