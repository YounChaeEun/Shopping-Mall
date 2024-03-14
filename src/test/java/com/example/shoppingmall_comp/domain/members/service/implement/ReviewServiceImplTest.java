package com.example.shoppingmall_comp.domain.members.service.implement;

import com.example.shoppingmall_comp.domain.members.dto.ReviewRequest;
import com.example.shoppingmall_comp.domain.members.entity.Review;
import com.example.shoppingmall_comp.domain.members.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ReviewServiceImplTest {

    @Autowired
    ReviewServiceImpl reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    private User user;

    @BeforeEach
    void setUp() {
        this.user = new User("amy1234@naver.com", "Amy4021*", new ArrayList<>()); // username은 디비에 잇는 거 쓰기
    }

    @DisplayName("리뷰 생성 테스트")
    @Test
    void create() {
        // given
        var request = new ReviewRequest("test review title", "test review content", 3, 5L);

        // when
        var response = reviewService.create(request, user);

        //then
        assertThat(response.title()).isEqualTo("test review title");
        assertThat(response.content()).isEqualTo("test review content");
        assertThat(response.star()).isEqualTo(3);
    }

    @DisplayName("리뷰 수정 테스트")
    @Test
    void update() {
        // given
        var request = new ReviewRequest("test review title", "test review content", 3, 5L);
        reviewService.create(request, user); // 리뷰 ID 7로 생성됨

        var newRequest = new ReviewRequest("test review new title", "test review new content", 5, 5L);

        // when
        reviewService.update(7L, newRequest, user);
        var review = reviewRepository.findById(7L).get();

        //then
        assertThat(review.getReviewTitle()).isEqualTo("test review new title");
        assertThat(review.getReviewContent()).isEqualTo("test review new content");
        assertThat(review.getStar()).isEqualTo(5);
    }

    @DisplayName("리뷰 삭제 테스트")
    @Test
    void delete() {
        // given
        var request = new ReviewRequest("test review title", "test review content", 3, 5L);
        reviewService.create(request, user); // 리뷰 ID 8로 생성됨

        // when
        reviewService.delete(8L, user);

        // then
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews.size()).isEqualTo(7);
    }
}