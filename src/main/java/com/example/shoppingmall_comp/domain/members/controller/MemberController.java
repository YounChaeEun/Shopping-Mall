package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import com.example.shoppingmall_comp.domain.members.service.Impl.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberServiceImpl memberService;
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public MemberSignUpResponse getOneMember(@AuthenticationPrincipal User user) {
        return memberService.getOne(user);
    }
}
