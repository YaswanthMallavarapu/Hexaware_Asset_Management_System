package com.asset.demo.dto;

import java.util.List;

public record AssetAllocationPageResDto(
        List<AssetAllocationResDto> list,
        long totalElements,
        long totalPages
) {
}
