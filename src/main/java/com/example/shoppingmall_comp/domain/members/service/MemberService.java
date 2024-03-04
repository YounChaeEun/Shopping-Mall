package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.MemberResponse;
import com.example.shoppingmall_comp.domain.members.dto.UpdateMemberPaswordRequest;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface MemberService {
    MemberResponse getOne(User user);
    List<MemberResponse> getAll();
    void deleteUser(User user);
    void deleteSeller(User user);
    void updatePassword(User user, UpdateMemberPaswordRequest request);
}
