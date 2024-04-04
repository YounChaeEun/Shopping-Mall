package com.example.shoppingmall_comp.domain.members.service.implement;

import com.example.shoppingmall_comp.domain.members.dto.MemberSignInRequest;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignInResponse;
import com.example.shoppingmall_comp.domain.members.dto.MemberSignUpRequest;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.entity.RefreshToken;
import com.example.shoppingmall_comp.domain.members.entity.Role;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.repository.RefreshTokenRepository;
import com.example.shoppingmall_comp.domain.members.repository.RoleRepository;
import com.example.shoppingmall_comp.domain.members.service.AuthService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
import com.example.shoppingmall_comp.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void saveMember(MemberSignUpRequest request) {
        checkIfIsDuplicated(request.email());

        Member member = Member.builder()
                .email(request.email())
                .password(bCryptPasswordEncoder.encode(request.password()))
                .role(Role.builder().roleName(request.roleName()).build())
                .build();

        memberRepository.save(member);
    }

    public void checkIfIsDuplicated(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    @Transactional
    @Override
    public MemberSignInResponse signIn(MemberSignInRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password()); //// 1. username + password 를 기반으로 Authentication 객체 생성. 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        Authentication authentication;

        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken); //2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행// authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.CHECK_LOGIN_ID_OR_PASSWORD);
        }

        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        String accessToken = jwtTokenProvider.createAccessToken(member); // 3. 인증정보를 기반으로 JWT 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // 해당 멤버의 리프레시 토큰가 이미 있으면 새로운 리프레시 토큰으로 재발급해주고, 없으면 저장해줄것
        refreshTokenRepository.findByMember(member)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(RefreshToken.builder().refreshToken(refreshToken).member(member).build()));

        return new MemberSignInResponse(accessToken, refreshToken);
    }
}
