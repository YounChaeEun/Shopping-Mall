package com.example.shoppingmall_comp.domain.members.repository;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByMember(Member member, Pageable pageable);
    Page<Review> findAllByItem(Item item, Pageable pageable);
    List<Review> findAllByMember(Member member);
    List<Review> findAllByItem(Item item);
}
