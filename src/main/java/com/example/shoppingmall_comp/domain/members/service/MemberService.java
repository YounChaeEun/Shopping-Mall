package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberResponse;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface MemberService {
    MemberResponse getOne(User user);
    List<MemberResponse> getAll();
    void deleteUser(User user);
    void deleteSeller(User user);
}
