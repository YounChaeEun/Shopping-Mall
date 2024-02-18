package com.example.shoppingmall_comp.domain.items.dto;

import com.example.shoppingmall_comp.domain.items.entity.Category;
import com.example.shoppingmall_comp.domain.items.entity.ItemOption;
import com.example.shoppingmall_comp.domain.items.entity.SoldOutState;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public record ItemRequest(

        @Nullable
        Long itemId, //수정할때 컨트롤러에서 id도 같이 줄거 아니면 필요함!

        @NotBlank
        @Size(min = 2, max = 50)
        String itemName,

        @NotNull
        Long categoryId,

        @Min(value = 1)
        @NotNull
        int price,

        @NotNull
        int count,

        @Nullable
        List<Option> optionValue,

        @NotNull
        SoldOutState soldOutState,

        @Nullable
        String description


)  {
        public record Option (
                String key,
                String value) {
        }

}
