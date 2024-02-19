package com.example.shoppingmall_comp.domain.members.service;


import com.example.shoppingmall_comp.domain.members.dto.ReviewRequest;
import com.example.shoppingmall_comp.domain.members.dto.ReviewResponse;
import org.springframework.security.core.userdetails.User;

public interface ReviewService {
    ReviewResponse create(ReviewRequest request, User user);
    ReviewResponse update(Long reviewId, ReviewRequest reviewRequest, User user);

}
