package com.example.shoppingmall_comp.items.service;

import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemImageRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemOptionRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.items.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    ItemImageRepository itemImageRepository;

    @Autowired
    CategoryRepository categoryRepository;

//    @PersistenceContext
//    EntityManager em;

    private User user;
    private Category category;

    @BeforeEach
    void setup() {
        this.user = new User("amy12345seller@naver.com", "Amy4021*", new ArrayList<>());
        this.category = categoryRepository.save(Category.builder()
                .categoryName("test category")
                .build());
    }

    @DisplayName("상품 생성 성공 테스트")
    @Test
    void create() {
        // given
        var images = createSuccessItemImage();
        var options = createSuccessItemOption();
        var itemRequest = new ItemRequest("test item name ", category.getCategoryId(), 10000, 10000, options, "test item description");

        // when
        var response = itemService.create(itemRequest, images, user);

        // then
        var item = itemRepository.findById(response.itemId()).orElseThrow(); // get()으로 그냥 해도 되지 않은지, 아무것도 안 던지는데 이렇게 해도되는지...
        assertThat(item.getItemName()).isEqualTo("test item name ");

        var foundImages = itemImageRepository.findByItem(item);
        assertThat(foundImages.isEmpty()).isFalse();
        assertThat(foundImages.size()).isEqualTo(2);

        var foundOptions = item.getItemOption().getOptionValues();
        assertThat(foundOptions.isEmpty()).isFalse();
        assertThat(foundOptions.size()).isEqualTo(2);
    }

    // 추후의 실패 테스트까지 고려해서 setup에 두지 않고 따로 메서드로 뺌, setup은 성공,실패 테스트 모두에 사용 가능한 것만 넣음
    private List<ItemRequest.Option> createSuccessItemOption() {
        List<ItemRequest.Option> options = new ArrayList<>(); // 이거 var로 바뀌면 오류남 왠지 파악하기!
        options.add(new ItemRequest.Option("색상", "빨강"));
        options.add(new ItemRequest.Option("사이즈", "large"));
        return options;
    }

    private List<MultipartFile> createSuccessItemImage() {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        MockMultipartFile multipartFile1 = new MockMultipartFile("file.jpg", "file.jpg", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("file.jpg", "file.jpg", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        multipartFiles.add(multipartFile1);
        multipartFiles.add(multipartFile2);
        return multipartFiles;
    }

//    private Category createCategory() {
//        Category category = Category.builder()
//                .categoryName("test category")
//                .build();
//        em.persist(category);
//        return category;
//    }
}

