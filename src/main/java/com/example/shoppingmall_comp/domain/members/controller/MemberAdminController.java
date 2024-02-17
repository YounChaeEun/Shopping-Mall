package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import com.example.shoppingmall_comp.domain.members.service.Impl.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class MemberAdminController {

    private final MemberServiceImpl memberService;

    // 회원 전체 조회
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public List<MemberSignUpResponse> getAllMembers() {
        return memberService.getAll();
    }
}