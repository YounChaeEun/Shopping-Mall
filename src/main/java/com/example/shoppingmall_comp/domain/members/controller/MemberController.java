package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import com.example.shoppingmall_comp.domain.members.service.impl.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@AuthenticationPrincipal User user) {
        memberService.deleteUser(user);
    }
}
