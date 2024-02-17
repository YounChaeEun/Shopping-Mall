package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignInRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignInResponse;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import com.example.shoppingmall_comp.domain.members.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "멤버 관련 api", description = "회원가입, 로그인, 회원 정보 조회, 삭제, 수정 api들입니다.")
public class MemberController {

    private final MemberServiceImpl memberService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입 api", description = "사용자가 회원가입하는 api 입니다.")
    public MemberSignUpResponse saveMember(@RequestBody @Valid MemberSignUpRequest request) {
        return memberService.saveMember(request);
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "로그인 api", description = "사용자가 로그인하는 api 입니다.")
    public MemberSignInResponse signInMember(@RequestBody @Valid MemberSignInRequest request) {
        return memberService.signIn(request);
    }
}
