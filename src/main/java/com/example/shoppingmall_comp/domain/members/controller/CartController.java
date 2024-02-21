package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.items.service.Impl.ItemServiceImpl;
import com.example.shoppingmall_comp.domain.items.service.ItemService;
import com.example.shoppingmall_comp.domain.members.dto.CartRequest;
import com.example.shoppingmall_comp.domain.members.dto.CartResponse;
import com.example.shoppingmall_comp.domain.members.service.CartService;
import com.example.shoppingmall_comp.domain.members.service.Impl.CartServiceImpl;
import com.example.shoppingmall_comp.domain.members.service.Impl.MemberServiceImpl;
import com.example.shoppingmall_comp.domain.members.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public CartResponse addCart(@Valid @RequestBody CartRequest cartRequest,
                                @AuthenticationPrincipal User user) {
        return cartService.create(cartRequest, user);
    }

    //장바구니 수정
    @PatchMapping("/carts")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "장바구니 수정 api", description = "장바구니를 수정하는 api 입니다.")
    public CartResponse updateCart(@Valid @RequestBody CartRequest cartRequest,
                                   @AuthenticationPrincipal User user) {
        return cartService.update(cartRequest, user);
    }

}
