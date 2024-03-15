package com.example.shoppingmall_comp.auth.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpRequest;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.service.implement.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    AuthServiceImpl authService;
    @Autowired
    MemberRepository memberRepository;

    @DisplayName("회원가입 성공 테스트")
    @Test
    void saveMember() {
        // given
        var request = new MemberSignUpRequest("amy402123@naver.com", "Amy4021*", RoleName.USER);

        // when
        authService.saveMember(request);

        // then
        var member = memberRepository.findByEmail(request.email()).get();
        assertThat(member.getEmail()).isEqualTo("amy402123@naver.com");
        assertThat(member.getRole().getRoleName()).isEqualTo(RoleName.USER);
        // 질문: 비밀번호는 안해도 되나요?
    }
}
