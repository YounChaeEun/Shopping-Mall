package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
@Tag(name = "판매자의 멤버 관련 api", description = "판매자의 멤버 관련 api들입니다.")
public class MemberSellerController {

    private final MemberServiceImpl memberService;

    @DeleteMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "판매자의 회원 탈퇴 api", description = "판매자가 탈퇴하는 api 입니다.")
    public void deleteSeller(@AuthenticationPrincipal User user) {
        memberService.deleteSeller(user);
    }
}

