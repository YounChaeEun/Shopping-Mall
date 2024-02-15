package com.example.shoppingmall_comp.domain.members.repository;

import com.example.shoppingmall_comp.domain.members.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
