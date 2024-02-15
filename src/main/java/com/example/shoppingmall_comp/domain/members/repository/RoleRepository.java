package com.example.shoppingmall_comp.domain.members.repository;

import com.example.shoppingmall_comp.domain.members.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
