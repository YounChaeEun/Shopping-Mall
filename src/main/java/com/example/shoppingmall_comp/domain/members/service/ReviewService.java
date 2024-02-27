package com.example.shoppingmall_comp.domain.members.service;


import com.example.shoppingmall_comp.domain.members.dto.ReviewRequest;
import com.example.shoppingmall_comp.domain.members.dto.ReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(ReviewRequest request, User user);
    ReviewResponse update(Long reviewId, ReviewRequest reviewRequest, User user);
    void delete(Long reviewId, User user);
    List<ReviewResponse> getAllByMember(User user, Pageable pageable);
    List<ReviewResponse> getAllByItem(Long itemId, Pageable pageable);
}
