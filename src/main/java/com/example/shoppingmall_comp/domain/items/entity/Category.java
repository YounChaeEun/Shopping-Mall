package com.example.shoppingmall_comp.domain.items.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import com.example.shoppingmall_comp.domain.items.dto.CategoryRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Builder
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public void updateCategory(CategoryRequest request) {
        this.categoryName = request.categoryName();
    }
}

