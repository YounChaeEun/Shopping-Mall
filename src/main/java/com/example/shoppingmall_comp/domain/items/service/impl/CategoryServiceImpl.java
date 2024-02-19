package com.example.shoppingmall_comp.domain.items.service.impl;

import com.example.shoppingmall_comp.domain.items.dto.CategoryResponse;
import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {return new CategoryResponse(category.getCategoryId(), category.getCategoryName());})
                .collect(Collectors.toList());
    }
}