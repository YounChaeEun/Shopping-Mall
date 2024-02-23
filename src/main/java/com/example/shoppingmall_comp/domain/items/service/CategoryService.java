package com.example.shoppingmall_comp.domain.items.service;

import com.example.shoppingmall_comp.domain.items.dto.CategoryRequest;
import com.example.shoppingmall_comp.domain.items.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAll();
    CategoryResponse create(CategoryRequest request);
    void delete(Long categoryId);
    void update(CategoryRequest request, Long categoryId);
}
