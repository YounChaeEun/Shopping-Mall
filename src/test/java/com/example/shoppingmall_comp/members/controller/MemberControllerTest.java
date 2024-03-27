package com.example.shoppingmall_comp.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    private Member member;

    @BeforeEach
    void mockMvcSetUp() {
        this.member = memberRepository.save(Member.builder()
                .email("user")
                .password(passwordEncoder.encode("Amy4021!"))
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("회원 상세 조회 컨트롤러 성공 테스트")
    @WithMockUser
    void getOneMember() throws Exception {
        // given
        var url = "/api/members";

        // when
        ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        // when
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(this.member.getEmail()))
                .andExpect(jsonPath("$.roleName").value(String.valueOf(this.member.getRole().getRoleName())))
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
        ResultActions result = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // when
        result.andExpect(status().isNoContent());
    }
}
