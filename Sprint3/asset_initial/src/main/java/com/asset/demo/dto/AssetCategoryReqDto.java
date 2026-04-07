package com.asset.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssetCategoryReqDto(
        @NotNull(message="category cannot be null")
        @NotBlank(message="category cannot be blank")
        String categoryName,

        @NotNull(message="text cannot be null")
        @NotBlank(message="text cannot be blank")
        String text
        

) {
}
