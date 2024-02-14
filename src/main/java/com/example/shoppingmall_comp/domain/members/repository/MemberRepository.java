package com.example.shoppingmall_comp.domain.members.repository;

import com.example.shoppingmall_comp.domain.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
