package com.example.shoppingmall_comp.domain.items.service.Impl;

import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.dto.ItemResponse;
import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemImage;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.repository.CategoryRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemImageRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemOptionRepository;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.items.service.ItemService;
import com.example.shoppingmall_comp.domain.items.service.S3Service;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.shoppingmall_comp.global.exception.ErrorCode.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final S3Service s3Service;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    // 상품 등록 + 이미지 추가(필수) + 옵션 추가(필수X)
    @Override
    @Transactional
    public ItemResponse create(ItemRequest itemRequest, List<MultipartFile> multipartFiles, User user) {
        Member member = getMember(user);
        System.out.println("사용자 정보: {}" + member);

        // 같은 이름의 상품이 있으면 예외처리, 같은 이름의 상품을 등록할 수 없음
        if (itemRepository.findByItemName(itemRequest.itemName()).isPresent()) {
            throw new BusinessException(DUPLICATE_ITEM, "이미 존재하는 상품입니다.");
        }

        Category category = categoryRepository.findById(itemRequest.categoryId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_CATEGORY));

        //옵션 정보 저장
        List<ItemOption.Option> optionValues = itemRequest.optionValue() != null ?
                itemRequest.optionValue().stream()
                        .map(option -> new ItemOption.Option(option.key(), option.value()))
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        ItemOption itemOption = ItemOption.builder()
                .optionValues(optionValues)
                .build();

        Item item = Item.builder()
                .itemName(itemRequest.itemName())
                .itemPrice(itemRequest.price())
                .itemDetail(itemRequest.description())
                .category(category)
                .count(itemRequest.count()) // 적절한 초기값으로 설정, 판매자가 재고수량 입력하게, -> 수정
                .member(member)
                .itemOption(itemOption)
                .soldOutState(itemRequest.soldOutState())
                .build();

        Item savedItem = itemRepository.save(item);

        // S3 저장
        System.out.println("S3에 이미지를 업로드합니다.");
        List<String> imageUrls = s3Service.upload(multipartFiles);

        //이미지 1장 이상 등록 안했을때 에러 처리
        if (imageUrls.isEmpty()) {
            throw new BusinessException(REQUIRED_IMAGE,"이미지는 필수로 등록해야합니다.");
        }
        System.out.println("업로드된 이미지 URL: {}" + imageUrls);

        // 이미지 DB 저장
        List<ItemImage> imageList = imageUrls.stream()
                .map(url -> ItemImage.builder().imageUrl(url).item(savedItem).build())
                .collect(Collectors.toList());

        itemImageRepository.saveAll(imageList);

        return new ItemResponse(
                savedItem.getItemId(),
                savedItem.getItemName(),
                savedItem.getCategory().getCategoryId(),
                savedItem.getItemPrice(),
                savedItem.getCount(),
                savedItem.getItemOption().getOptionValues().stream()
                        .map(option -> new ItemResponse.Option(option.key(), option.value()))
                        .collect(Collectors.toList()),
                savedItem.getSoldOutState(),
                savedItem.getItemDetail()
        );

    }

    private Member getMember(User user) {
        return memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }
}
