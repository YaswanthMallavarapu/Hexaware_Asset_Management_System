package com.asset.demo.dto;

import java.util.List;

public record AssetPageResDto(
        List<AssetResDto> list,
        long totalElements,
        long totalPages
) {
}
