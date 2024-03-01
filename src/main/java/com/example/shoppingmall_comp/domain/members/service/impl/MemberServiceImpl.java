package com.example.shoppingmall_comp.domain.members.service.impl;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.items.service.impl.ItemServiceImpl;
import com.example.shoppingmall_comp.domain.members.dto.MemberResponse;
import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberEmailRequest;
import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Review;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.example.shoppingmall_comp.domain.members.repository.ReviewRepository;
import com.example.shoppingmall_comp.domain.members.service.MemberService;
import com.example.shoppingmall_comp.domain.orders.entity.Order;
import com.example.shoppingmall_comp.domain.orders.repository.OrderRepository;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthServiceImpl authService;
    private final ItemServiceImpl itemService;
    private final ItemRepository itemRepository;

    @Override
    public MemberResponse getOne(User user) {
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        return new MemberResponse(member.getMemberId(),
                member.getEmail(),
                member.getPoint(),
                member.getTotalConsumePrice(),
                member.getGrade(),
                member.getDeletedState(),
                member.getRole().getRoleName());
    }

    @Override
    public List<MemberResponse> getAll() {
        List<Member> memberList = memberRepository.findAll();
        return memberList.stream()
                .map(member -> new MemberResponse(member.getMemberId(),
                        member.getEmail(),
                        member.getPoint(),
                        member.getTotalConsumePrice(),
                        member.getGrade(),
                        member.getDeletedState(),
                        member.getRole().getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        // 리뷰의 member를 null로 바꾼다. (member를 삭제할 것이기때문에 삭제할 member를 참조하고 있는 리뷰가 있으면 에러가 남)
        reviewRepository.findAllByMember(member)
                .forEach(Review::changeMemberToNull);

        // 나중에 orderItem의 memberId 수정하기
        // 주문의 member를 null로 바꾼다. (member를 삭제할 것이기때문에 삭제할 member를 참조하고 있는 주문이 있으면 에러가 남)
        orderRepository.findAllByMember(member)
                .forEach(Order::changeMemberToNull);

        // 구매자의 장바구니를 삭제한다.
        cartRepository.findAllByMember(member)
                .forEach(cart -> cartRepository.deleteById(cart.getCartId()));

        // 구매자의 refresh token을 삭제한다. (refresh token도 casecade option으로 수정할 것!)
        refreshTokenRepository.findByMember(member)
                        .ifPresentOrElse(
                                refreshToken -> refreshTokenRepository.deleteById(refreshToken.getId()),
                                () -> { throw new BusinessException(ErrorCode.NOT_FOUND_REFRESH_TOKEN); }
                        );

        // 구매자를 삭제한다. (role은 casecade option으로 같이 삭제된다.)
        memberRepository.deleteById(member.getMemberId());
    }

    @Override
    @Transactional
    public void deleteSeller(User user) {
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        List<Item> itemList = itemRepository.findAllByMember(member);
        itemList.forEach(item -> {
            // 판매자의 판매 상품의 리뷰의 item을 null로 바꾼다.
            reviewRepository.findAllByItem(item)
                    .forEach(Review::changeItemToNull);

            // 판매자의 판매 상품을 시킨 주문의 item을 null로 바꾼다.


            // 구매자들 장바구니에 판매자의 판매 상품을 삭제한다.
            cartRepository.findAllByItem(item)
                    .forEach(cart -> cartRepository.deleteById(cart.getCartId()));

            // 판매자의 판매 상품을 삭제한다.
            itemService.delete(item.getItemId(), user);
        });

        // 구매자일때 삭제하는 것들을 삭제한다. (장바구니, 권한, 리프레시, 회원 자체)
        deleteUser(user);
    }

    @Override
    @Transactional
    public void updateEmail(User user, UpdateMemberEmailRequest request) {
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        authService.checkIfIsDuplicated(request.newEmail()); //  이게 맞을까??
        member.updateEmail(request.newEmail());
    }

    @Override
    @Transactional
    public void updatePassword(User user, UpdateMemberPaswordRequest request) {
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        if(passwordEncoder.matches(request.oldPassword(), member.getPassword())) {
            member.updatePassword(passwordEncoder.encode(request.newPassword()));
        } else {
            throw new BusinessException(ErrorCode.NOT_EQUAL_PASSWORD);
        }
    }
}
