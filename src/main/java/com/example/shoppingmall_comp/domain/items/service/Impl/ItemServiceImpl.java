package com.example.shoppingmall_comp.domain.items.service.Impl;
import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.dto.ItemResponse;
import com.example.shoppingmall_comp.domain.items.dto.SellerItemsResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        return getItemResponse(savedItem);

    }

    //상품 수정
    @Override
    @Transactional
    public ItemResponse update(ItemRequest itemRequest, List<MultipartFile> multipartFiles, User user) {
        Member member = getMember(user);

        // 수정할 상품이름이 이미 존재하면 예외처리
        if (itemRepository.findByItemName(itemRequest.itemName()).isPresent()) {
            throw new BusinessException(DUPLICATE_ITEM, "이미 존재하는 상품입니다.");
        }

        //todo: 상품을 등록한 회원과 로그인한 회원이 다르면 예외처리 - 로그인하고 등록하는거니까 예외처리 안해도 되나

        // 해당 상품이 없을 경우
        if(itemRequest.itemId() == null) {
            //itemRequest의 itemId가 Nullable이기 때문에 itemId가 null인지 검사하는 로직 처리
            throw new BusinessException(NOT_FOUND_ITEM, "상품 ID가 필요합니다.");
        }
        Item item = itemRepository.findById(itemRequest.itemId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));

        Category category = categoryRepository.findById(itemRequest.categoryId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CATEGORY));

        //상품의 회원(판매자) != 현재 로그인한 회원
        if (!item.getMember().equals(member)) {
            throw new BusinessException(NOT_SELLING_ITEM,"판매한 상품이 아닙니다.");
        }

        // 엔티티 수정
        item.updateItem(itemRequest.itemName(), itemRequest.price(),
                itemRequest.count(),itemRequest.description(), category);

        // 상품의 기존 옵션 삭제
        List<ItemOption> options = itemOptionRepository.findByItem(item); // 아이템 관련해서 아이템 옵션 조회
        itemOptionRepository.deleteAll(options);

        //새로운 옵션이 주어졌을때(옵션값이 null이 아닐 때) 옵션 DB에 저장
        if (itemRequest.optionValue() != null && !itemRequest.optionValue().isEmpty()) {
            //옵션값 -> 엔티티 변환
            List<ItemOption.Option> optionValues = itemRequest.optionValue().stream()
                    .map(option -> new ItemOption.Option(option.key(), option.value()))
                    .collect(Collectors.toList());
            ItemOption itemOption = ItemOption.builder()
                    .optionValues(optionValues)
                    .build();
            itemOptionRepository.save(itemOption);
        }

        // S3, 이미지DB 삭제
        List<ItemImage> imageList = itemImageRepository.findByItem(item);
        for (ItemImage image : imageList) {
            String fileName = image.getImageUrl();
            s3Service.deleteFile(fileName);
            itemImageRepository.deleteById(image.getItemImageId());
        }
        // S3 이미지 저장
        List<String> imageUrls = s3Service.upload(multipartFiles);

        // 이미지 정보 저장
        List<ItemImage> images = imageUrls.stream()
                .map(img -> ItemImage.builder().imageUrl(img).item(item).build())
                .collect(Collectors.toList());
        itemImageRepository.saveAll(images);

        return getItemResponse(item);
    }

    // 상품 삭제
    @Override
    @Transactional
    public void delete(Long itemId, User user) {
        Member member = getMember(user);

        // 해당 상품이 없을 경우
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM,"존재하는 상품이 아닙니다."));

        //현재 로그인한 사용자가 삭제하려는 상품의 소유자가 맞는지
        if (!item.getMember().equals(member)) {
            throw new BusinessException(NOT_SELLING_ITEM, "판매하고 있는 상품이 아닙니다.");
        }

        // S3, 이미지DB 삭제
        List<ItemImage> imageList = itemImageRepository.findByItem(item);
        for (ItemImage image : imageList) {
            String fileName = image.getImageUrl();
            s3Service.deleteFile(fileName);
            itemImageRepository.deleteById(image.getItemImageId());
        }

        // 상품 삭제
        itemRepository.deleteById(itemId);
    }

    //상품 조회(판매자)
    @Transactional(readOnly = true)
    public List<SellerItemsResponse> getSellerAll(Pageable pageable, User user) {
        Member member = getMember(user);
        Page<Item> sellerItems = itemRepository.findByMember(pageable, member);

        return sellerItems.stream()
                .map(item -> new SellerItemsResponse(
                        item.getItemId(),
                        item.getItemName(),
                        item.getItemPrice(),
                        item.getCount()
                ))
                .collect(Collectors.toList());
    }



    private Member getMember(User user) {
        return memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }

    //ItemResponse 코드 중복 방지
    private ItemResponse getItemResponse(Item item) {
        return new ItemResponse(
                item.getItemId(),
                item.getItemName(),
                item.getCategory().getCategoryId(),
                item.getItemPrice(),
                item.getCount(),
                item.getItemOption().getOptionValues().stream()
                        .map(option -> new ItemResponse.Option(option.key(), option.value()))
                        .collect(Collectors.toList()),
                item.getSoldOutState(),
                item.getItemDetail()
        );
    }
}
