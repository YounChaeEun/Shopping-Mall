package com.example.shoppingmall_comp.domain.members.entity;

import com.example.shoppingmall_comp.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "platform_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlatformState platformState;

    @Column(name = "platform_email", nullable = false)
    private String platformEmail;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    //적립금 메소드
    public void updatePoints(int point) {
        this.point = point;
    }
}
