package com.example.shoppingmall_comp.items.service;

import com.example.shoppingmall_comp.domain.items.dto.CategoryRequest;
import com.example.shoppingmall_comp.domain.items.dto.CategoryResponse;
import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.service.implement.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@DisplayName("카테고리 서비스 테스트")
public class CategoryServiceTest {

    @Autowired
    CategoryServiceImpl categoryService;
    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        createCategory("전자제품");
    }

    @DisplayName("카테고리 생성 성공 테스트")
    @Test
    void create() {
        //when
        Category createdCategory = createCategory("전자제품");

        //then
        Assertions.assertNotNull(createdCategory);
        Assertions.assertEquals("전자제품", createdCategory.getCategoryName());
    }

    @DisplayName("카테고리 수정 성공 테스트")
    @Test
    void update() {
        //given
        Category createdCategory = createCategory("전자제품");
        CategoryRequest categoryRequest = new CategoryRequest("도서");

        //when
        categoryService.update(categoryRequest, createdCategory.getCategoryId());

        //then
        Category updatedCategory = categoryRepository.findById(createdCategory.getCategoryId()).orElseThrow();
        Assertions.assertEquals(categoryRequest.categoryName(), updatedCategory.getCategoryName());
    }

    @DisplayName("카테고리 전체 조회 테스트")
    @Test
    void getAll() {
        //given
        createCategory("생활용품");

        //when
        List<CategoryResponse> categories = categoryService.getAll();

        //then
        Assertions.assertEquals(2, categories.size());
    }

    @DisplayName("카테고리 삭제 테스트")
    @Test
    void delete() {
        //given
        Category createdCategory = createCategory("전자제품");

        //when
        categoryService.delete(createdCategory.getCategoryId());

        //then
        Optional<Category> deletedCategory = categoryRepository.findById(createdCategory.getCategoryId());
        Assertions.assertFalse(deletedCategory.isPresent());
    }

    //카테고리 생성 메소드
    private Category createCategory(String categoryName) {
        return categoryRepository.save(Category.builder()
                        .categoryName(categoryName)
                        .build()
        );
    }
}