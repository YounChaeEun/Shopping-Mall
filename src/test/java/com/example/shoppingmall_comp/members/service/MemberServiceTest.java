package com.example.shoppingmall_comp.members.service;

import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.service.implement.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberServiceImpl memberService;
    @Autowired 
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private User user;

    @BeforeEach
    void setUp() {
        this.user = new User("amy1234@naver.com", "Amy4021*", new ArrayList<>());
    }

    @DisplayName("회원 상세 조회 성공 테스트")
    @Test
    void getOne() {
        // when
        var response = memberService.getOne(user);

        // then
        assertThat(response.email()).isEqualTo("amy1234@naver.com");
        // 질문: Point, TotalConsumePrice 같은 나머지것들도 확인하는 게 맞나? 근데 이거 확인하려면, 여기서 직접 설정하는 게 아니라서 디비 들어가서 확인해야하는데..
    }

    @DisplayName("회원 전체 조회 성공 테스트")
    @Test
    void getAll() {
        // when
        var responses = memberService.getAll();

        // then
        assertThat(responses.size()).isEqualTo(3);
    }

    @DisplayName("비밀번호 변경 성공 테스트")
    @Test
    void updatePassword() {
        // given
        var request = new UpdateMemberPaswordRequest("Amy4021*", "Amy4021!");

        // when
        memberService.updatePassword(user, request);

        // then
        var member = memberRepository.findByEmail(user.getUsername()).get();
        var result = passwordEncoder.matches(request.newPassword(), member.getPassword());
        assertThat(result).isTrue();
    }

}
