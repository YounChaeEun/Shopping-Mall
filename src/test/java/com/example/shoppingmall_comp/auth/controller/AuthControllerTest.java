package com.example.shoppingmall_comp.auth.controller;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignInRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpRequest;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("회원가입 컨트롤러 성공 테스트")
    @WithMockUser
    public void saveMember() throws Exception {
        // given
        var url = "/api/signup";

        var request = new MemberSignUpRequest("amy11234@naver.com", "Amy4021!", RoleName.USER);
        var requestBody = objectMapper.writeValueAsString(request);

        // when
        var result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // when
        result.andExpect(status().isCreated());

        var member = memberRepository.findByEmail(request.email());
        assertThat(member.isPresent()).isTrue();
    }

    @Test
    @DisplayName("로그인 컨트롤러 성공 테스트")
    @WithMockUser
    public void signInMember() throws Exception {
        // given
        var url = "/api/signin";

        var savedMember = memberRepository.save(Member.builder()
                .email("amy11234@naver.com")
                .password(bCryptPasswordEncoder.encode("Amy4021!"))
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());
        var request = new MemberSignInRequest("amy11234@naver.com", "Amy4021!");
        var requestBody = objectMapper.writeValueAsString(request);

        // when
        var result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // when
        var refreshToken = refreshTokenRepository.findByMember(savedMember).orElseThrow();
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken.getRefreshToken()));
    }
}
