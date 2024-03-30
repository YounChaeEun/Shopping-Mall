package com.example.shoppingmall_comp.domain.items.service.implement;

import com.example.shoppingmall_comp.domain.items.dto.*;
import com.example.shoppingmall_comp.domain.items.entity.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
    public CreateItemResponse create(ItemRequest itemRequest, List<MultipartFile> multipartFiles, User user) {
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
                        .toList() :
                new ArrayList<>();

        ItemOption itemOption = ItemOption.builder()
                .optionValues(optionValues)
                .build();

        Item item = Item.builder()
                .itemName(itemRequest.itemName())
                .itemPrice(itemRequest.price())
                .itemDetail(itemRequest.description())
                .category(category)
                .count(itemRequest.count())
                .member(member)
                .itemOption(itemOption)
                .itemState(ItemState.ON_SALE) // 따로 추가함
                .build();

        Item savedItem = itemRepository.save(item);

        // S3 저장
        System.out.println("S3에 이미지를 업로드합니다.");
        List<String> imageUrls = s3Service.upload(multipartFiles);

        //이미지 1장 이상 등록 안했을때 에러 처리
        if (imageUrls.isEmpty()) {
            throw new BusinessException(REQUIRED_IMAGE, "이미지는 필수로 등록해야합니다.");
        }
        System.out.println("업로드된 이미지 URL: {}" + imageUrls);

        // 이미지 DB 저장
        List<ItemImage> imageList = imageUrls.stream()
                .map(url -> ItemImage.builder().imageUrl(url).item(savedItem).build())
                .toList();

        itemImageRepository.saveAll(imageList);

        List<Long> itemImgIds = imageList.stream().map(ItemImage::getItemImageId).toList();

        return getCreateItemResponse(savedItem, imageUrls, itemImgIds);

    }

    //상품 수정
    @Override
    @Transactional
    public UpdateItemResponse update(Long itemId, UpdateItemRequest itemRequest, List<MultipartFile> multipartFiles, User user) {
        Member member = getMember(user);

        // 수정할 상품 이름이 이미 존재하면 예외처리
        if (itemRepository.findByItemName(itemRequest.itemName()).isPresent()) {
            throw new BusinessException(DUPLICATE_ITEM, "이미 존재하는 상품입니다.");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM));

        Category category = categoryRepository.findById(itemRequest.categoryId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CATEGORY));

        //상품의 회원(판매자) != 현재 로그인한 회원
        if (!item.getMember().equals(member)) {
            throw new BusinessException(FORBIDDEN_ERROR, "수정할 권한이 없습니다.");
        }

        // 엔티티 수정
        item.updateItem(itemRequest.itemName(), itemRequest.price(),
                itemRequest.count(), itemRequest.description(), category, itemRequest.itemState());

        // 상품의 기존 옵션 삭제
        List<ItemOption> options = itemOptionRepository.findByItem(item); // 아이템 관련해서 아이템 옵션 조회
        itemOptionRepository.deleteAll(options);

        //옵션 DB에 저장
        if (itemRequest.optionValue() != null && !itemRequest.optionValue().isEmpty()) {
            //옵션값 -> 엔티티 변환
            List<ItemOption.Option> optionValues = itemRequest.optionValue().stream()
                    .map(option -> new ItemOption.Option(option.key(), option.value()))
                    .toList();
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
                .toList();
        itemImageRepository.saveAll(images);

        List<Long> itemImgIds = images.stream().map(ItemImage::getItemImageId).toList();

        return new UpdateItemResponse(itemImgIds, imageUrls);
    }

    // 상품 삭제
    @Override
    @Transactional
    public void delete(Long itemId, User user) {
        Member member = getMember(user);

        // 해당 상품이 없을 경우
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM, "존재하는 상품이 아닙니다."));

        //현재 로그인한(상품 삭제하려는) 사용자 == 상품 등록했던 회원
        if (!item.getMember().equals(member)) {
            throw new BusinessException(FORBIDDEN_ERROR, "삭제할 권한이 없습니다.");
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

    //상품 전체 조회(판매자)
    @Override
    public SellerItemsResponse getSellerAll(Pageable pageable, User user) {
        Member member = getMember(user);
        Page<Item> sellerItems = itemRepository.findByMember(pageable, member);

        List<SellerItemsResponse.sellerItem> sellerItemList = sellerItems.stream()
                .map(sellerItem -> new SellerItemsResponse.sellerItem(
                        sellerItem.getItemId(),
                        sellerItem.getItemName(),
                        sellerItem.getItemPrice(),
                        sellerItem.getCount(),
                        sellerItem.getItemState()
                ))
                .toList();

        return new SellerItemsResponse(
                sellerItems.getTotalPages(),
                (int) sellerItems.getTotalElements(),
                sellerItems.getNumber(),
                sellerItems.getSize(),
                sellerItemList
        );
    }

    //상품 상세 조회(전체 사용자)
    @Override
    @Transactional(readOnly = true)
    public ItemResponse getOne(Long itemId) {
        //해당 상품이 없을 경우
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM, "존재하는 상품이 아닙니다."));

        // 이미지 URL 조회
        List<String> imgUrls = itemImageRepository.findByItem(item).stream()
                .map(ItemImage::getImageUrl)
                .toList();

        return new ItemResponse(
                item.getItemId(),
                item.getItemName(),
                item.getCategory().getCategoryId(),
                item.getItemPrice(),
                item.getItemOption().getOptionValues().stream()
                        .map(option -> new ItemResponse.Option(option.key(), option.value()))
                        .toList(),
                item.getItemState(),
                item.getItemDetail(),
                imgUrls
        );
    }

    private Member getMember(User user) {
        return memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }

    //ItemResponse 코드 중복 방지
    private CreateItemResponse getCreateItemResponse(Item item, List<String> imgUrls, List<Long> itemImageIds) {
        return new CreateItemResponse(
                item.getItemId(),
                item.getItemName(),
                item.getCategory().getCategoryId(),
                item.getItemPrice(),
                item.getCount(),
                item.getItemOption().getOptionValues().stream()
                        .map(option -> new CreateItemResponse.Option(option.key(), option.value()))
                        .toList(),
                item.getItemDetail(),
                itemImageIds,
                imgUrls
        );
    }

    //상품 전체 조회(전체 사용자)
    @Override
    public ItemPageResponse getAll(Pageable pageable, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CATEGORY));

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Item> items = itemRepository.findByCategory(pageRequest, category);

        if (items.isEmpty()) {
            throw new BusinessException(NOT_FOUND_ITEM, "해당 카테고리에 속한 상품이 없습니다.");
        }

        List<ItemPageResponse.ItemList> itemList = items.stream()
                .map(item -> new ItemPageResponse.ItemList(
                        item.getItemId(),
                        item.getItemName(),
                        item.getItemPrice()
                ))
                .toList();

        return new ItemPageResponse(items.getTotalPages(),
                (int) items.getTotalElements(),
                items.getNumber(),
                items.getSize(),
                itemList);
    }
}
