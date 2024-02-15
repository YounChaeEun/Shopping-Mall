package com.example.shoppingmall_comp.domain.members.service.Impl;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.service.MemberService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

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
}