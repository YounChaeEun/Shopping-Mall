package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import org.springframework.security.core.userdetails.User;

public interface MemberService {
    MemberSignUpResponse getOne(User user);
}
