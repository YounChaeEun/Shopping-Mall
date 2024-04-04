package com.example.shoppingmall_comp.members.controller;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
import com.example.shoppingmall_comp.domain.members.entity.*;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.example.shoppingmall_comp.domain.members.repository.ReviewRepository;
import com.example.shoppingmall_comp.domain.orders.entity.Order;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        this.member = memberRepository.save(Member.builder()
                .email("user")
                .password(passwordEncoder.encode("Amy4021!"))
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());
    }

    @Test
    @DisplayName("회원 상세 조회 컨트롤러 성공 테스트")
    @WithMockUser
    void getOneMember() throws Exception {
        // given
        var url = "/api/members";

        // when
        var result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        // when
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.roleName").value(String.valueOf(member.getRole().getRoleName())))
                .andExpect(jsonPath("$.point").value(0));
    }

    @Test
    @DisplayName("회원 비밀번호 변경 컨트롤러 성공 테스트")
    @WithMockUser
    void updateMemberPassword() throws Exception {
        // given
        var url = "/api/members/password";
        var request = new UpdateMemberPaswordRequest("Amy4021!", "Amy4021*");
        var requestBody = objectMapper.writeValueAsString(request);

        // when
        var result = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // when
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("일반 회원 삭제 컨트롤러 성공 테스트")
    @WithMockUser
    void deleteUser() throws Exception {
        // given
        var category = categoryRepository.save(Category.builder()
                .categoryName("test category name")
                .build());

        var seller = memberRepository.save(Member.builder()
                .email("seller@naver.com")
                .password("1234")
                .role(Role.builder()
                        .roleName(RoleName.SELLER)
                        .build())
                .build());

        var item = itemRepository.save(Item.builder()
                .itemState(ItemState.ON_SALE)
                .itemPrice(10000)
                .itemDetail("test item detail")
                .itemName("test item name")
                .category(category)
                .count(10000)
                .itemOption(ItemOption.builder()
                        .optionValues(List.of(new ItemOption.Option("색상", "white")))
                        .build())
                .member(seller)
                .build());

        cartRepository.save(Cart.builder()
                .member(member)
                .count(10)
                .item(item)
                .optionValues(List.of(new Cart.Option("색상", "white")))
                .itemState(ItemState.ON_SALE)
                .build());

        refreshTokenRepository.save(RefreshToken.builder()
                .member(member)
                .refreshToken("test refresh token")
                .build());

        var url = "/api/members";

        // when
        var result = mockMvc.perform(delete(url));

        // when
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("관리자의 회원 전체 조회 컨트롤러 성공 테스트")
    void getAllMembers() throws Exception {
        // given
        var url = "/api/admin/members";

        // when
        var result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        // when
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isNotEmpty());

        var memberList = memberRepository.findAll();
        assertThat(memberList.size()).isGreaterThanOrEqualTo(1);
    }
}
