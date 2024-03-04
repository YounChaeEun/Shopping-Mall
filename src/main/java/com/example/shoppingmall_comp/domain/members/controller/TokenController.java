package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.CreateAccessTokenReponse;
import com.example.shoppingmall_comp.domain.members.dto.CreateAccessTokenRequest;
import com.example.shoppingmall_comp.domain.members.service.imple.TokenServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "토큰 관련 api", description = "토큰 관련 api입니다.")
public class TokenController {

    private final TokenServiceImpl tokenService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary ="엑세스 토큰 재발급 api", description = "리프레시 토큰으로 새 엑세스 토큰 발급해주는 api입니다.")
    public CreateAccessTokenReponse createNewAccessToken(@RequestBody @Valid CreateAccessTokenRequest request) {
        return tokenService.createNewAccessToken(request);
    }
}
