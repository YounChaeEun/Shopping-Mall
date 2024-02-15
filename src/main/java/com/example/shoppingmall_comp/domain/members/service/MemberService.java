package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;

public interface MemberService {
    MemberSignUpResponse saveMember(MemberSignUpRequest request);
}
