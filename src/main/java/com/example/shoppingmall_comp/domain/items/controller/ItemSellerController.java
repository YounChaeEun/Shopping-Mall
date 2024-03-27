package com.example.shoppingmall_comp.domain.items.controller;

import com.example.shoppingmall_comp.domain.items.dto.*;
import com.example.shoppingmall_comp.domain.items.service.implement.ItemServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
@Tag(name = "(판매자 권한) 상품 관련 api", description = "판매자의 상품 등록, 수정, 조회, 삭제 api입니다.")
public class ItemSellerController {

    private final ItemServiceImpl itemService;

    //(판매자)상품 등록 + 이미지 추가(필수) + 옵션 설정(필수X)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    @Operation(summary = "상품 등록 api", description = "상품을 등록하는 api 입니다.")
    public CreateItemResponse addItem(@Valid @RequestPart(value = "itemRequest", required = false) ItemRequest itemRequest,
                                      @RequestPart(value = "file", required = false) List<MultipartFile> multipartFiles,
                                      @AuthenticationPrincipal User user) {
        return itemService.create(itemRequest, multipartFiles, user);
    }

    //상품 수정
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/items/{itemId}")
    @Operation(summary = "상품 수정 api", description = "상품을 수정하는 api 입니다.")
    public UpdateItemResponse updateItem(@PathVariable Long itemId,
                                         @Valid @RequestPart(value = "itemRequest", required = false) UpdateItemRequest itemRequest,
                                         @RequestPart(value = "file", required = false)  List<MultipartFile> multipartFiles,
                                         @AuthenticationPrincipal User user) {
        return itemService.update(itemId, itemRequest, multipartFiles, user);
    }

    //상품 삭제
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "상품 삭제 api", description = "상품을 삭제하는 api 입니다.")
    public void deleteItem(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
        itemService.delete(itemId, user);
    }

    //상품 조회(판매자)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/items")
    @Operation(summary = "상품 판매자 조회 api", description = "판매자 자신이 등록한 상품을 조회하는 api 입니다.")
    public SellerItemsResponse getSellerItems(Pageable pageable, @AuthenticationPrincipal User user) {
        return itemService.getSellerAll(pageable, user);
    }
}