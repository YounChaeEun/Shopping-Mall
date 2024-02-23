package com.example.shoppingmall_comp.domain.items.controller;

import com.example.shoppingmall_comp.domain.items.dto.CategoryRequest;
import com.example.shoppingmall_comp.domain.items.dto.CategoryResponse;
import com.example.shoppingmall_comp.domain.items.service.impl.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "관리자의 카테고리 관련 api", description = "관리자만 접근 가능한 카테고리 관련 api입니다.")
public class CategoryAdminController {

    private final CategoryServiceImpl categoryService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "카테고리 등록 api", description = "관리자가 카테고리를 등록하는 api 입니다.")
    @ApiResponse(responseCode = "201", description = "카테고리 등록 성공", content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    public CategoryResponse addCategory(@RequestBody @Valid CategoryRequest request, @AuthenticationPrincipal User user) {
        System.out.println("user.getAuthorities() = " + user.getAuthorities());
        return categoryService.create(request);
    }

    @DeleteMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "카테고리 삭제 api", description = "관리자가 카테고리를 삭제하는 api 입니다.")
    @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공", content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    public void deleteCategory(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
    }

    @PatchMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "카테고리 수정 api", description = "관리자가 카테고리를 수정하는 api 입니다.")
    @ApiResponse(responseCode = "204", description = "카테고리 수정 성공", content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    public void updateCategory(@Valid @RequestBody CategoryRequest request,
                               @PathVariable Long categoryId) {
        categoryService.update(request, categoryId);
    }
}