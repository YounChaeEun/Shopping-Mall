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

    @DisplayName("카테고리 생성 테스트")
    @Test
    void create() {
        //when
        Category createdCategory = createCategory("전자제품");

        //then
        Assertions.assertNotNull(createdCategory);
        Assertions.assertEquals("전자제품", createdCategory.getCategoryName());
    }

    //카테고리 생성 메소드
    private Category createCategory(String categoryName) {
        return categoryRepository.save(Category.builder()
                        .categoryName(categoryName)
                        .build()
        );
    }
}