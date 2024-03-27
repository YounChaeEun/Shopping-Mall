package com.example.shoppingmall_comp.items.service;

import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.dto.UpdateItemRequest;
import com.example.shoppingmall_comp.domain.items.entity.*;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemImageRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemOptionRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.items.service.ItemService;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    @Autowired
    MemberRepository memberRepository;

    private User user;
    private Category category;
    private Member member;

    @BeforeEach
    void setup() {
        this.member = memberRepository.save(Member.builder()
                .email("amy4021123@naver.com")
                .password("1234")
                .role(Role.builder()
                        .roleName(RoleName.SELLER)
                        .build())
                .build());

        // User 생성
        this.user = new User("amy4021123@naver.com", "1234", new ArrayList<>());

        // 카테고리 생성
        this.category = categoryRepository.save(Category.builder()
                .categoryName("test category")
                .build());
    }

    @DisplayName("상품 생성 성공 테스트")
    @Test
    void create() {
        // given
        var options = List.of(new ItemRequest.Option("색상", "빨강"), new ItemRequest.Option("사이즈", "large"));
        var itemRequest = new ItemRequest("test item name ", category.getCategoryId(), 10000, 10000, options, "test item description");
        var images = createSuccessItemImage();

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

    @DisplayName("상품 삭제 성공 테스트")
    @Test
    void delete() {
        // given
        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);
        saveSuccessItemImage(item);

        // when
        itemService.delete(item.getItemId(), user);

        // then
        var items = itemRepository.findAllByMember(this.member);
        assertThat(items).isEmpty();

        var foundImages = itemImageRepository.findByItem(item); // 질문: 이게 맞는지? 디비에서는 삭제되었지만 자바의 메모리에서는 여전히 남아있어서 이 로직을 수행할 수 있다고 하는데..
        assertThat(foundImages).isEmpty();

        // 옵션 부분
    }

    @DisplayName("상품 수정 성공 테스트")
    @Test
    void update() {
        // given
        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);
        saveSuccessItemImage(item);

        var newOptions = List.of(new UpdateItemRequest.Option("색상", "빨강"), new UpdateItemRequest.Option("사이즈", "large"));
        var updateRequest = new UpdateItemRequest("new test item name", category.getCategoryId(), 20000, 20000, newOptions, ItemState.SOLD_OUT, "new test item description");
        var images = createSuccessItemImage();

        // when
        itemService.update(item.getItemId(), updateRequest, images, user);

        // then
        assertThat(item.getItemName()).isEqualTo("new test item name");
        assertThat(item.getCount()).isEqualTo(20000);
        assertThat(item.getItemState()).isEqualTo(ItemState.SOLD_OUT);

        var foundOptions = item.getItemOption().getOptionValues();
        //assertThat(foundOptions.get(0).value()).isEqualTo("초록");
        //assertThat(foundOptions.get(1).value()).isEqualTo("small");

        var foundImages = itemImageRepository.findByItem(item);
        assertThat(foundImages.size()).isEqualTo(2);
    }

    @DisplayName("상품 상세 조회 성공 테스트")
    @Test
    void getOne() {
        // given
        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);
        saveSuccessItemImage(item);

        // when
        var response = itemService.getOne(item.getItemId());

        // then
        assertThat(response.itemName()).isEqualTo("test item name");
        assertThat(response.description()).isEqualTo("test item detail");
        assertThat(response.price()).isEqualTo(10000);

        assertThat(response.optionValue().size()).isEqualTo(2);
        assertThat(response.optionValue().get(0).value()).isEqualTo("빨강");
        assertThat(response.optionValue().get(1).value()).isEqualTo("large");

        assertThat(response.imgUrls().size()).isEqualTo(1);
    }

    @DisplayName("상품 전체 조회 성공 테스트")
    @Test
    void getAll() {
        // given
        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);
        saveSuccessItemImage(item);

        var pageRequest = PageRequest.of(0, 15, Sort.Direction.DESC, "itemId");

        // when
        var response = itemService.getAll(pageRequest, category.getCategoryId());

        // then
        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.totalPage()).isEqualTo(1);
        assertThat(response.pageNumber()).isEqualTo(0);

        assertThat(response.itemList().size()).isEqualTo(1);
    }

    @DisplayName("판매자가 파는 상품 전체 조회 성공 테스트")
    @Test
    void getSellerAll() {
        // given
        var itemOption = saveSuccessItemOption();
        var item = saveSuccessItem(itemOption);
        saveSuccessItemImage(item);

        var pageRequest = PageRequest.of(0, 15, Sort.Direction.DESC, "itemId");

        // when
        var response = itemService.getSellerAll(pageRequest, user);

        // then
        // 테스트가 실제 데이터베이스에 의존하지 않도록 설계하는 것이 중요하다..? -> 사람들이 같은 디비를 바라볼 보장이 없기 때문임
        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.totalPage()).isEqualTo(1);
        assertThat(response.pageNumber()).isEqualTo(0);
        assertThat(response.currentPageSize()).isEqualTo(15);

        assertThat(response.sellerItemList().size()).isEqualTo(1);
    }


    // 추후의 실패 테스트까지 고려해서 setup에 두지 않고 따로 메서드로 뺌, setup은 성공,실패 테스트 모두에 사용 가능한 것만 넣음
    private List<MultipartFile> createSuccessItemImage() {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        MockMultipartFile multipartFile1 = new MockMultipartFile("file.jpg", "file.jpg", "multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("file.jpg", "file.jpg", "multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));
        multipartFiles.add(multipartFile1);
        multipartFiles.add(multipartFile2);
        return multipartFiles;
    }

    private ItemOption saveSuccessItemOption(){
        List<ItemOption.Option> options = new ArrayList<>();
        options.add(new ItemOption.Option("색상", "빨강"));
        options.add(new ItemOption.Option("사이즈", "large"));
        return itemOptionRepository.save(ItemOption.builder()
                .optionValues(options)
                .build());
    }

    private Item saveSuccessItem(ItemOption itemOption) {
        return itemRepository.save(Item.builder()
                .itemName("test item name")
                .itemPrice(10000)
                .itemDetail("test item detail")
                .count(10000)
                .itemState(ItemState.ON_SALE)
                .category(this.category)
                .itemOption(itemOption)
                .member(this.member)
                .build());
    }

    private ItemImage saveSuccessItemImage(Item item) {
        return itemImageRepository.save(ItemImage.builder()
                .imageUrl("image url")
                .item(item)
                .build());
    }
}

