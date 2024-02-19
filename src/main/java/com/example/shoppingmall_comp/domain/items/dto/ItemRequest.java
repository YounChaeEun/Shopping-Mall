package com.example.shoppingmall_comp.domain.items.dto;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.SoldOutState;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Schema(description = "상품 요청 DTO")
public record ItemRequest(

        @Nullable
        @Schema(description = "상품 id", example = "1")
        Long itemId, //수정할때 컨트롤러에서 id도 같이 줄거 아니면 필요함!

        @NotBlank
        @Schema(description = "상품 이름", example = "노트북")
        @Size(min = 2, max = 50)
        String itemName,

        @NotNull
        @Schema(description = "카테고리 id", example = "1")
        Long categoryId,

        @Min(value = 1)
        @NotNull
        @Schema(description = "상품 가격", example = "879000")
        int price,

        @NotNull
        @Schema(description = "상품 수량", example = "1000")
        int count,

        @Nullable
        @Schema(description = "상품 옵션", example = "색상: WHITE")
        List<Option> optionValue,

        @NotNull
        @Schema(description = "상품 품절상태", example = "품절")
        SoldOutState soldOutState,

        @Nullable
        @Schema(description = "상품 상세 설명", example = "가볍고 화질이 선명해요.")
        String description


)  {
        public record Option (
                String key,
                String value) {
        }

}
