package com.example.shoppingmall_comp.domain.members.service;

import com.example.shoppingmall_comp.domain.members.dto.CreateAccessTokenReponse;
import com.example.shoppingmall_comp.domain.members.dto.CreateAccessTokenRequest;

public interface TokenService {
    CreateAccessTokenReponse createNewAccessToken(CreateAccessTokenRequest request);
}
