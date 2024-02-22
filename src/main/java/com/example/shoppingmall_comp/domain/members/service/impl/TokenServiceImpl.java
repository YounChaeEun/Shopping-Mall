package com.example.shoppingmall_comp.domain.members.service.impl;

import com.example.shoppingmall_comp.domain.members.dto.CreateAccessTokenReponse;
import com.example.shoppingmall_comp.domain.members.dto.CreateAccessTokenRequest;
import com.example.shoppingmall_comp.domain.members.entity.RefreshToken;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.example.shoppingmall_comp.domain.members.service.TokenService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
import com.example.shoppingmall_comp.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public CreateAccessTokenReponse createNewAccessToken(CreateAccessTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!tokenProvider.validate(refreshToken)) {
            RefreshToken foundRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_REFRESH_TOKEN));
            String accessToken = tokenProvider.createAccessToken(foundRefreshToken.getMember());
            return new CreateAccessTokenReponse(accessToken);
        }
        return null;
    }
}
