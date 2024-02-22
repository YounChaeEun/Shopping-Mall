package com.example.shoppingmall_comp.domain.members.service.impl;

import com.example.shoppingmall_comp.domain.members.dto.MemberResponse;
import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberEmailRequest;
import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.example.shoppingmall_comp.domain.members.service.MemberService;
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

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthServiceImpl authService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberResponse getOne(User user) {
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        return new MemberResponse(member.getMemberId(),
                member.getEmail(),
                member.getPoint(),
                member.getConsumePrice(),
                member.getVipState(),
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
                        member.getConsumePrice(),
                        member.getVipState(),
                        member.getDeletedState(),
                        member.getRole().getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(User user) { // 이렇게 해주는 게 맞는지? 메서드 하나 만들고 if else if 이렇게 해서 하는 게 더 나은가?
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        // 구매자의 장바구니를 삭제한다.

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

        // 판매자의 판매 상품을 삭제한다.

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
