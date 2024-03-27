package com.example.shoppingmall_comp.auth.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignInRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpRequest;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.example.shoppingmall_comp.domain.members.service.implement.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    AuthServiceImpl authService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @DisplayName("회원가입 성공 테스트")
    @Test
    void saveMember() {
        // given
        var request = new MemberSignUpRequest("amy11234@naver.com", "Amy4021!", RoleName.USER);

        // when
        authService.saveMember(request);

        // then
        var member = memberRepository.findByEmail(request.email()).orElseThrow();
        assertThat(member.getEmail()).isEqualTo("amy11234@naver.com");
        assertThat(member.getRole().getRoleName()).isEqualTo(RoleName.USER);
    }

    @DisplayName("로그인 성공 테스트")
    @Test
    void signin() {
        // given
        memberRepository.save(Member.builder()
                .email("amy11234@naver.com")
                .password(bCryptPasswordEncoder.encode("Amy4021!"))
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());
        var request = new MemberSignInRequest("amy11234@naver.com", "Amy4021!");

        // when
        var response = authService.signIn(request);

        // then
        var member = memberRepository.findByEmail(request.email()).orElseThrow();
        var refreshToken = refreshTokenRepository.findByMember(member);
        assertThat(refreshToken.isPresent()).isTrue();
        assertThat(refreshToken.get().getRefreshToken()).isEqualTo(response.refreshToken());
    }
}
