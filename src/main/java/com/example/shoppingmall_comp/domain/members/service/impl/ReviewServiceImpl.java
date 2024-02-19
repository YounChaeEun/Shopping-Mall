package com.example.shoppingmall_comp.domain.members.service.impl;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.ReviewRequest;
import com.example.shoppingmall_comp.domain.members.dto.ReviewResponse;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Review;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.ReviewRepository;
import com.example.shoppingmall_comp.domain.members.service.ReviewService;
import com.example.shoppingmall_comp.domain.orders.entity.OrderItem;
import com.example.shoppingmall_comp.domain.orders.repository.OrderItemRepository;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    public void checkIfIsForbidden(Long id, Long loginId, ErrorCode errorCode) {
        if (!id.equals(loginId)) {
            throw new BusinessException(errorCode);
        }
    }

    public Member getMember(User user) {
        return memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    }

    @Transactional
    @Override
    public ReviewResponse create(ReviewRequest request, User user) {
        Member member = getMember(user);

        OrderItem orderItem = orderItemRepository.findById(request.OrderItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ORDER_ITEM));

        // 주문한 회원의 id랑 로그인한 id가 다른지 확인한다.
        Long orderMemberId = orderItem.getOrder().getMember().getMemberId();
        checkIfIsForbidden(orderMemberId, member.getMemberId(), ErrorCode.CANT_WRITE_REVIEW);

        Item item = orderItem.getItem();

        Review review = Review.builder()
                .reviewTitle(request.title())
                .reviewContent(request.content())
                .star(request.star())
                .member(member)
                .item(item)
                .build();

        reviewRepository.save(review);

        return new ReviewResponse(review.getReviewId(),
                review.getReviewTitle(),
                review.getReviewContent(),
                review.getStar());
    }

    @Override
    @Transactional
    public ReviewResponse update(Long reviewId, ReviewRequest reviewRequest, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW));

        // 리뷰를 작성한 회원의 id랑 로그인한 id가 다른지 확인한다.
        Long reviewWriterId = review.getMember().getMemberId();
        Member member = getMember(user);
        checkIfIsForbidden(reviewWriterId, member.getMemberId(), ErrorCode.NOT_MATCH_REVIEW);

        review.updateReview(reviewRequest.title(), reviewRequest.content(), reviewRequest.star());

        return new ReviewResponse(review.getReviewId(),
                review.getReviewTitle(),
                review.getReviewContent(),
                review.getStar());
    }

    @Override
    @Transactional
    public void delete(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW));

        // 리뷰를 작성한 회원의 id랑 로그인한 id가 다른지 확인한다.
        Long reviewWriterId = review.getMember().getMemberId();
        Member member = getMember(user);
        checkIfIsForbidden(reviewWriterId, member.getMemberId(), ErrorCode.NOT_MATCH_REVIEW);

        reviewRepository.deleteById(reviewId);
    }
}
