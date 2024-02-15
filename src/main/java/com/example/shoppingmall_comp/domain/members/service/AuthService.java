package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignInRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignInResponse;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;

public interface AuthService {
    MemberSignUpResponse saveMember(MemberSignUpRequest request);
    MemberSignInResponse signIn(MemberSignInRequest request);
}
