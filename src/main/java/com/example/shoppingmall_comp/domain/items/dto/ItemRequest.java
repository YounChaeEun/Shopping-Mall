package com.example.shoppingmall_comp.domain.items.dto;

import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Schema(description = "상품 요청 DTO")
public record ItemRequest(

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

        @NotNull
        @Schema(description = "상품 옵션", example = "{key: 색상, value: WHITE}")
        List<Option> optionValue,

        @NotBlank
        @Schema(description = "상품 상세 설명", example = "가볍고 화질이 선명해요.")
        String description


)  {
        public record Option (
                String key,
                String value) {
        }

}
