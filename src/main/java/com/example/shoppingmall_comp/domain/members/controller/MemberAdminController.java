package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import com.example.shoppingmall_comp.domain.members.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자의 멤버 관련 api", description = "관리자의 멤버 관련 api들입니다.")
public class MemberAdminController {

    private final MemberServiceImpl memberService;

    // 회원 전체 조회
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "관리자의 회원 전체 조회 api", description = "관리자가 모든 회원들을 조회하는 api 입니다.")
    public List<MemberSignUpResponse> getAllMembers() {
        return memberService.getAll();
    }
}