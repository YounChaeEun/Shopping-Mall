package com.example.shoppingmall_comp.domain.items.service.implement;

import com.example.shoppingmall_comp.domain.items.dto.CategoryRequest;
import com.example.shoppingmall_comp.domain.items.dto.CategoryResponse;
import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.service.CategoryService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
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

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = Category.builder()
                .categoryName(request.categoryName())
                .build();
        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory.getCategoryId(), savedCategory.getCategoryName());
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public void update(CategoryRequest request, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
        category.updateCategory(request);
    }
}