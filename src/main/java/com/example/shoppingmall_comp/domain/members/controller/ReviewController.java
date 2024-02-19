package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.ReviewRequest;
import com.example.shoppingmall_comp.domain.members.dto.ReviewResponse;
import com.example.shoppingmall_comp.domain.members.service.impl.ReviewServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    /* 리뷰 전체 조회 (마이페이지) */
    @GetMapping("/reviews")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "리뷰 전체 조회 api", description = "마이페이지에서 리뷰를 전체 조회하는 api 입니다.")
    public List<ReviewResponse> findAllByMember(@AuthenticationPrincipal User user,
                                                Pageable pageable){
        return reviewService.getAllByMember(user,pageable);
    }

    /* 리뷰 전체 조회 (아이템 상세 페이지) */
    @GetMapping("/items/{itemId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "리뷰 전체 조회 api", description = "아이템 상세 페이지에서 리뷰를 전체 조회하는 api 입니다.")
    public List<ReviewResponse> findAllByItem(@PathVariable Long itemId,
                                              Pageable pageable) { // PageImpl: Spring Data에서 페이징된 데이터를 표현하기 위한 객체. Page 인터페이스 구현체
        return reviewService.getAllByItem(itemId, pageable);
    }
}