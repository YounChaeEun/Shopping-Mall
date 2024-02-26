package com.example.shoppingmall_comp.domain.items.controller;

import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.dto.ItemResponse;
import com.example.shoppingmall_comp.domain.items.dto.SellerItemsResponse;
import com.example.shoppingmall_comp.domain.items.service.Impl.ItemServiceImpl;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
@Tag(name = "상품 관련 api", description = "상품 관련 api입니다.")
public class ItemSellerController {

    private final ItemServiceImpl itemService;

    //(판매자)상품 등록 + 이미지 추가(필수) + 옵션 설정(필수X)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    @Operation(summary = "상품 등록 api", description = "상품을 등록하는 api 입니다.")
    public ItemResponse addItem(@Valid @RequestPart ItemRequest itemRequest,
                                @RequestPart List<MultipartFile> multipartFiles,
                                @AuthenticationPrincipal User user) {
        return itemService.create(itemRequest, multipartFiles, user);
    }

    //상품 수정
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/items")
    @Operation(summary = "상품 수정 api", description = "상품을 수정하는 api 입니다.")
    public ItemResponse updateItem(@Valid @RequestPart ItemRequest itemRequest,
                           @RequestPart List<MultipartFile> multipartFiles,
                           @AuthenticationPrincipal User user) {
         return itemService.update(itemRequest, multipartFiles, user);
    }

    //상품 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "상품 삭제 api", description = "상품을 삭제하는 api 입니다.")
    public void deleteItem(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
        itemService.delete(itemId, user);
    }

    //상품 조회(판매자)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/items")
    @Operation(summary = "상품 판매자 조회 api", description = "판매자 자신이 등록한 상품을 조회하는 api 입니다.")
    public List<SellerItemsResponse> getSellerItems(Pageable pageable, @AuthenticationPrincipal User user) {
        return itemService.getSellerAll(pageable, user);
    }
}
