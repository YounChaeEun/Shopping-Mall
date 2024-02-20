package com.example.shoppingmall_comp.domain.members.service.impl;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.example.shoppingmall_comp.domain.members.service.MemberService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
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

    @Override
    public MemberSignUpResponse getOne(User user) {
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        return new MemberSignUpResponse(member.getMemberId(),
                member.getEmail(),
                member.getPoint(),
                member.getConsumePrice(),
                member.getVipState(),
                member.getDeletedState(),
                member.getRole().getRoleName());
    }

    @Override
    public List<MemberSignUpResponse> getAll() {
        List<Member> memberList = memberRepository.findAll();
        return memberList.stream()
                .map(member -> new MemberSignUpResponse(member.getMemberId(),
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
    public void deleteUser(User user) {
        Member member = memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        // 회원이 판매자거나, 관리자면 예외 처리 해준다. -> 이렇게 해주는 게 맞는지? 메서드 하나 만들고 if else if 이렇게 해서 하는 게 더 나은가?
        // 이것도 코드가 겹치는데 메서드로 따로 빼줘야 하나?
        // 예외처리로 메서드를 빼면 @Override를 붙여주는 게 맞나?
        RoleName roleName = member.getRole().getRoleName();
        if(roleName.equals(RoleName.SELLER) || roleName.equals(RoleName.ADMIN)) {
            throw new BusinessException(ErrorCode.NOT_USER);
        }

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
}
