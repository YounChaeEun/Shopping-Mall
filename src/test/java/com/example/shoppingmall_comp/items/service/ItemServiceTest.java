package com.example.shoppingmall_comp.items.service;

import com.example.shoppingmall_comp.domain.items.dto.CreateItemResponse;
import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.ItemImage;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemImageRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemOptionRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.items.service.implement.ItemServiceImpl;
import org.assertj.core.api.Assertions;
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
    ItemServiceImpl itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    ItemImageRepository itemImageRepository;

    private User user;
    private Category category;

    @BeforeEach
    void setup() {
        this.user = new User("amy12345seller@naver.com", "Amy4021*", new ArrayList<>());
        this.category = new Category(1L, "test category");

    }

    @DisplayName("상품 생성 성공 테스트")
    @Test
    void create() {
        // given
        List<ItemRequest.Option> options = new ArrayList<>(); // 이거 var로 바뀌면 오류남 왠지 파악하기!
        options.add(new ItemRequest.Option("색상", "빨강"));

        var itemRequest = new ItemRequest("test item name ", 1L, 10000, 10000, options, "test item description");

        List<MultipartFile> multipartFiles = new ArrayList<>();
        MockMultipartFile multipartFile1 = new MockMultipartFile("file.jpg", "file.jpg", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );
        MockMultipartFile multipartFile2 = new MockMultipartFile("file.jpg", "file.jpg", "text/plain", "test file".getBytes(StandardCharsets.UTF_8) );
        multipartFiles.add(multipartFile1);
        multipartFiles.add(multipartFile2);

        // when
        var response = itemService.create(itemRequest, multipartFiles, user);

        // then
        var item = itemRepository.findById(response.itemId()).get();
        assertThat(item.getItemName()).isEqualTo(response.itemName());

        var images = itemImageRepository.findByItem(item);
        assertThat(images.isEmpty()).isFalse();
        assertThat(images.size()).isEqualTo(2);

        List<ItemOption> option = itemOptionRepository.findByItem(item);
        assertThat(option.isEmpty()).isFalse();
        assertThat(option.size()).isEqualTo(1);
    }
}
