package com.asset.demo.dto;

import java.util.List;

public record AssetRequestPageResDto(
        List<AssetRequestResDto> list,
        long totalElements,
        long totalPages
) {
}
