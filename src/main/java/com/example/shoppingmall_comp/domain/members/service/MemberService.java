package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpResponse;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface MemberService {
    MemberSignUpResponse getOne(User user);
    List<MemberSignUpResponse> getAll();
    void deleteUser(User user);
}
