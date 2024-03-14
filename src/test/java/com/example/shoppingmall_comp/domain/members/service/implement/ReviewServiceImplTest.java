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
}