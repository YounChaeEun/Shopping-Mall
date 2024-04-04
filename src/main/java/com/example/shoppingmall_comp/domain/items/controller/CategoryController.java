package com.example.shoppingmall_comp.domain.items.controller;

import com.example.shoppingmall_comp.domain.items.dto.CategoryResponse;
import com.example.shoppingmall_comp.domain.items.service.implement.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "카테고리 관련 api", description = "누구나 접근 가능한 카테고리 관련 api 입니다.")
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "카테고리 전체 조회 api", description = "모든 카테고리를 조회하는 api 입니다.")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAll();
    }
}
