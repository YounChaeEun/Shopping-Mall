package com.example.shoppingmall_comp.domain.members.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {
    // UserDetails는 사용자의 인증 정보를 담아두는 인터페이스로, UserDetails를 상속받아 Member를 인증 객체로 사용한다.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "point", nullable = false)
    private int point;

    @Column(name = "consume_price", nullable = false)
    private int consumePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "vip_state", nullable = false)
    private VipState vipState;

    @Enumerated(EnumType.STRING)
    @Column(name = "deleted_state", nullable = false)
    private DeletedState deletedState;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder
    public Member(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.point = 0;
        this.consumePrice = 0;
        this.vipState = VipState.NOT_VIP;
        this.deletedState = DeletedState.NOT_DELETED;
    }

    //적립금 메소드
    public void updatePoints(int point) {
        this.point = point;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(String.valueOf(role.getRoleName())));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

//    @Column(name = "nickname", nullable = false)
//    private String nickname;
//
//    @Column(name = "platform_state", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private PlatformState platformState;
//
//    @Column(name = "platform_email", nullable = false)
//    private String platformEmail;
}
