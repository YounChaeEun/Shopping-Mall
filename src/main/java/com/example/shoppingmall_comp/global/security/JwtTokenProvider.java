package com.example.shoppingmall_comp.global.security;

import com.example.shoppingmall_comp.domain.members.entity.Member;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration-minutes}")
    private int expirationMinutes;

    @Value("${jwt.refresh-expiration-days}")
    private int refreshExpirationDays;

    private String getAuthorities(Member member) {
        return member.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public String createAccessToken(Member member) {
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(member.getEmail())
                .claim("auth", getAuthorities(member))
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .signWith(new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()))
                .setExpiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                .compact();
    }

    public String createRefreshToken() {
        return Jwts.builder()
                .setIssuer(issuer)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .signWith(new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()))
                .setExpiration(Date.from(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS)))
                .compact();
    }

    // jwt 토큰에서 멤버 정보 꺼내서 Authentication 객체를 반환하는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); // user의 이메일과 권한을 추출하기 위해 jwt에서 claim을 추출한다.

        // 권한을 추출한다.
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserDetails user = new User(claims.getSubject(), "", authorities); // user 이메일과 권한으로 UserDetails 객체 만듦
        return new UsernamePasswordAuthenticationToken(user,"", authorities); // 만든 userDetails 객체로 Authentication 인터페이스의 구현체인 UsernamePasswordAuthenticationToken을 만듦 즉, UsernamePasswordAuthenticationToken은 사용자의 아이디와 비밀번호를 담은 인증 토큰이며, 이 토큰은 Authentication 객체를 생성하고 인증 과정에서 사용된다.
    }

    public boolean validate(String token) {
        try {
            getClaims(token); // 클레임을 가져오지 못하면 유효하지 않은 토큰이다.
            return true;
        } catch (SecurityException | MalformedJwtException e) { // JWT 토큰의 형식이 잘못되었을 때 발생하는 예외
            log.info("Invalid JWT Token", e);
           // throw new BusinessException(ErrorCode.INVALID_JWT_ERROR);
        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 때 발생하는 예외
            log.info("Expired JWT Token", e);
            //throw new BusinessException(ErrorCode.EXPIRED_JWT_ERROR);
        } catch (UnsupportedJwtException e) {//  지원되지 않는 JWT 토큰을 처리하려고 할 때 발생하는 예외
            log.info("Unsupported JWT Token", e);
            //throw new BusinessException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) { // JWT 클레임 문자열이 비어있는 경우
            log.info("JWT claims string is empty.", e);
            //throw new BusinessException(ErrorCode.TOKEN_CLAIM_EMPTY);
        } catch (AuthenticationException e) {
            log.info("Authentication Failed. Username or Password not valid.", e);
            //throw new BusinessException(ErrorCode.USER_AUTH_ERROR);
        }
        return false;
    }

    // 클레임을 가져오는 메서드
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes()) // 토큰의 서명 검증에 사용할 서명키를 설정한다.
                .build() // parser 객체를 만든다.  파서 객체는 JWT 토큰을 해석하고 토큰에 포함된 정보를 추출하는 역할을 합니다. JWT 토큰은 일반적으로 세 부분으로 구성되어 있으며, 파서 객체는 이 각각의 부분을 해석하여 사용자에게 유용한 형태로 제공합니다.
                .parseClaimsJws(token) // 생성된 파서 객체를 사용하여 주어진 토큰을 우선 검증한다. 검증이 완료되면 토큰이 유효하다고 판단되어 토큰을 복호화한다. 복호화 후 jwt 토큰을 헤더, 페이로드, 서명로 파싱한다. 파싱된 결과로 ClaimsJws 객체를 반환합니다. ClaimsJws 객체에는 토큰의 속성 및 내용이 포함되어 있습니다.  ClaimsJws 객체는 토큰의 내용을 확인할 수 있는 메서드와 속성을 제공합니다. 예를 들어, .getBody() 메서드를 사용하여 토큰의 페이로드를 얻을 수 있습니다. 이렇게 얻은 페이로드는 클레임(Claim) 정보를 포함하고 있으며, 해당 정보를 활용할 수 있습니다.
                .getBody(); // 즉 이 과정에서 에러가 발생하면 유효하지 않은 토큰이란 뜻이다.
    }
}
