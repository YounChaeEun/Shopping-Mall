package com.example.shoppingmall_comp.members.service;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
import com.example.shoppingmall_comp.domain.members.entity.*;
import com.example.shoppingmall_comp.domain.members.repository.*;
import com.example.shoppingmall_comp.domain.members.service.implement.MemberServiceImpl;
import com.example.shoppingmall_comp.domain.orders.entity.Order;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Member member;

    @BeforeEach
    void setUp() {
        this.member = memberRepository.save(Member.builder()
                .email("amy111234@naver.com")
                .password(passwordEncoder.encode("Amy4021!"))
                .role(Role.builder()
                        .roleName(RoleName.USER)
                        .build())
                .build());
        this.user = new User(member.getUsername(), member.getPassword(), new ArrayList<>());
    }

    @DisplayName("회원 상세 조회 성공 테스트")
    @Test
    void getOne() {
        // when
        var response = memberService.getOne(user);

        // then
        assertThat(response.email()).isEqualTo("amy111234@naver.com");
        assertThat(response.roleName()).isEqualTo(RoleName.USER);
        assertThat(response.point()).isEqualTo(0);
    }

    @DisplayName("회원 전체 조회 성공 테스트")
    @Test
    void getAll() {
        // when
        var responses = memberService.getAll();

        // then
        assertThat(responses.size()).isEqualTo(1);
    }

    @DisplayName("비밀번호 변경 성공 테스트")
    @Test
    void updatePassword() {
        // given
        var request = new UpdateMemberPaswordRequest("Amy4021!", "Amy4021*");

        // when
        memberService.updatePassword(user, request);

        // then
        var member = memberRepository.findByEmail(user.getUsername()).orElseThrow();
        var result = passwordEncoder.matches(request.newPassword(), member.getPassword());
        assertThat(result).isTrue();
    }

    @DisplayName("일반 회원 삭제 성공 테스트")
    @Test
    void deleteUser() {
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
                        .optionValues(List.of())
                        .build())
                .member(seller)
                .build());

        refreshTokenRepository.save(RefreshToken.builder()
                .member(this.member)
                .refreshToken("test refresh token")
                .build());

        cartRepository.save(Cart.builder()
                .member(this.member)
                .count(10)
                .item(item)
                .itemState(ItemState.ON_SALE)
                .build());

        // when
        memberService.deleteUser(user);

        // then
        var deletedMember = memberRepository.findByEmail(user.getUsername());
        assertThat(deletedMember.isPresent()).isFalse();

        var refreshToken = refreshTokenRepository.findByMember(this.member);
        assertThat(refreshToken.isPresent()).isFalse();

        var cartList = cartRepository.findAllByMember(this.member);
        assertThat(cartList).isEmpty();
    }
}
