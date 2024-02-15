package com.example.shoppingmall_comp.domain.members.controller;

import com.example.shoppingmall_comp.domain.members.dto.CreateAccessTokenReponse;
import com.example.shoppingmall_comp.domain.members.dto.CreateAccessTokenRequest;
import com.example.shoppingmall_comp.domain.members.service.serviceImpl.TokenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {

    private final TokenServiceImpl tokenService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccessTokenReponse createNewAccessToken(@RequestBody @Valid CreateAccessTokenRequest request) {
        return tokenService.createNewAccessToken(request);
    }
}
