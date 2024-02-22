package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignInRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignInResponse;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpRequest;

public interface AuthService {
    void saveMember(MemberSignUpRequest request);
    MemberSignInResponse signIn(MemberSignInRequest request);
}
