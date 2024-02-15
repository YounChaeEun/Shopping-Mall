package com.example.shoppingmall_comp.global.security;

import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import com.example.shoppingmall_comp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // UserDetailsService를 구현받으면 loadUserByUsername를 필수로 오버라이딩 해야한다.
    // loadUserByUsername: 사용자의 이름으로 사용자의 정보를 가져오는 메서드
    // default 형태: UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    // Member가 UserDetails를 상속 받았기 때문에, 반환값을 Member로 바꿔도 됨.
    // 사용자 이름은 email로 직접 지정함.

    @Override
    public Member loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
