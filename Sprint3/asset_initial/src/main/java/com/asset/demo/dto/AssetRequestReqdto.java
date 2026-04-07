package com.asset.demo.dto;

import jakarta.validation.constraints.NotNull;

public record AssetRequestReqDto(
        @NotNull
        String remarks
) {
}
