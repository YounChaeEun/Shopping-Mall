package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.MemberResponse;
import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
import com.example.shoppingmall_comp.domain.members.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
@Tag(name = "멤버 관련 api", description = "회원 정보 조회, 삭제, 수정 api들입니다.")
public class MemberController {

    private final MemberServiceImpl memberService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "회원 상세 조회 api", description = "사용자가 자신의 정보를 조회하는 api 입니다.")
    public MemberResponse getOneMember(@AuthenticationPrincipal User user) {
        return memberService.getOne(user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "회원 탈퇴 api", description = "일반 사용자가 탈퇴하는 api 입니다.")
    public void deleteUser(@AuthenticationPrincipal User user) {
        memberService.deleteUser(user);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "회원 비밀번호 변경 api", description = "사용자가 자신의 비밀번호를 변경하는 api 입니다.")
    public void updateMemberEmail(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateMemberPaswordRequest request) {
        memberService.updatePassword(user, request);
    }
}
