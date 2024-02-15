package com.example.shoppingmall_comp.domain.members.dto;

import com.example.shoppingmall_comp.domain.members.entity.DeletedState;
import com.example.shoppingmall_comp.domain.members.entity.RoleName;
import com.example.shoppingmall_comp.domain.members.entity.VipState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답 DTO")
public record MemberSignInResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long memberId,

        @Schema(description = "사용자 이메일", example = "amy@naver.com")
        String email,

        @Schema(description = "사용자 보유 포인트", example = "1000")
        int point,

        @Schema(description = "사용자의 한달간 총 구매 금액", example = "100000")
        int consumePrice,

        @Schema(description = "사용자의 VIP 여부", example = "VIP")
        VipState vipState,

        @Schema(description = "사용자의 삭제 여부", example = "DELETED")
        DeletedState deletedState,

        @Schema(description = "사용자의 권한", example = "USER")
        RoleName roleName,

        @Schema(description = "사용자의 엑세스 토큰", example = "Uecjdjfldksjfdnsldkljs")
        String accessToken
) {
}
