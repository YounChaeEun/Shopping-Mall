package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.*;
import com.example.shoppingmall_comp.domain.members.service.implement.CartServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "장바구니 관련 api", description = "장바구니 등록, 수정, 조회, 삭제 api입니다.")
public class CartController {

    private final CartServiceImpl cartService;

    // 장바구니 등록
    @PostMapping("/carts")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "장바구니 생성 api", description = "장바구니를 생성하는 api 입니다.")
    public CartResponse addCart(@Valid @RequestBody CreateCartRequest cartRequest,
                                @AuthenticationPrincipal User user) {
        return cartService.create(cartRequest, user);
    }

    //장바구니 수정
    @PatchMapping("/carts/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "장바구니 수정 api", description = "장바구니를 수정하는 api 입니다.")
    public void updateCart(@PathVariable Long cartId,
                           @Valid @RequestBody UpdateCartRequest cartRequest,
                           @AuthenticationPrincipal User user) {
        cartService.update(cartId, cartRequest, user);
    }

    // 회원의 장바구니 전체 조회
    @GetMapping("/carts")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "장바구니 전체 조회 api", description = "장바구니를 전체 조회하는 api 입니다.")
    public CartPageResponse getCart(@PageableDefault(sort = "cartId", direction = Sort.Direction.DESC) Pageable pageable,
                                    @AuthenticationPrincipal User user) {
        return cartService.getAll(pageable, user);
    }

    //체크한 장바구니들 삭제
    @DeleteMapping("/carts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "장바구니 삭제 api", description = "선택한 장바구니들을 삭제하는 api 입니다.")
    public void deleteCarts(@RequestParam("cartIds") List<Long> cartIds,
                           @AuthenticationPrincipal User user) {
        cartService.deleteSelectedCarts(cartIds, user);
    }

}
