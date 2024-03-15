package com.example.shoppingmall_comp.members.service;

import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.example.shoppingmall_comp.domain.members.repository.ReviewRepository;
import com.example.shoppingmall_comp.domain.members.service.implement.MemberServiceImpl;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
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
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReviewRepository reviewRepository;

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

    @DisplayName("일반 회원 삭제 성공 테스트")
    @Test
    void deleteUser() {
        // given
        var member = memberRepository.findByEmail(user.getUsername()).get();

        // when
        memberService.deleteUser(user);

        // then
        // 질문 -> 멤버와 관련된 객체들이 너무 많다.. 여기있는 모든 객체들을 다 사용해야한다.. 그래서 이번만큼만 테스트용 데이터를 디비에 미리 넣어두고, 그걸 삭제하는 방식으로 해도 되는지 (여기서 객체들을 다 만들지 않고 review, cart .. )
        var deletedMember = memberRepository.findByEmail(user.getUsername());
        assertThat(deletedMember.isPresent()).isFalse();

        var refreshToken = refreshTokenRepository.findByMember(member);
        assertThat(refreshToken.isPresent()).isFalse();

        var cartList = cartRepository.findAllByMember(member);
        assertThat(cartList).isEmpty();

        // 아래 두 개는 삭제하지는 않지만, member 부분을 null로 바꾼다.
        var orderList = orderRepository.findAllByMember(member);
        assertThat(orderList).isEmpty();

        var reviewList = reviewRepository.findAllByMember(member);
        assertThat(reviewList).isEmpty();
    }

}
