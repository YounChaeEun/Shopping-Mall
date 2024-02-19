package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.ReviewRequest;
import com.example.shoppingmall_comp.domain.members.dto.ReviewResponse;
import com.example.shoppingmall_comp.domain.members.service.impl.ReviewServiceImpl;
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
@RequestMapping("/api")
@Tag(name = "리뷰 관련 api", description = "리뷰 관련 api입니다.")
public class ReviewController {

    private final ReviewServiceImpl reviewService;

    /*리뷰 작성하기*/
    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "리뷰 등록 api", description = "리뷰를 등록하는 api 입니다.")
    public ReviewResponse addReview(@Valid @RequestBody ReviewRequest request,
                                    @AuthenticationPrincipal User user) {
        return reviewService.create(request, user);
    }

    /* 리뷰 수정하기 */
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 수정 api", description = "리뷰를 수정하는 api 입니다.")
    public ReviewResponse updateReview(@PathVariable Long reviewId,
                                       @Valid @RequestBody ReviewRequest reviewRequest,
                                       @AuthenticationPrincipal User user) {
        return reviewService.update(reviewId, reviewRequest, user);
    }

    /* 리뷰 삭제하기 */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 삭제 api", description = "리뷰를 삭제하는 api 입니다.")
    public void deleteReview(@PathVariable Long reviewId,
                             @AuthenticationPrincipal User user) {
        reviewService.delete(reviewId, user);
    }
}