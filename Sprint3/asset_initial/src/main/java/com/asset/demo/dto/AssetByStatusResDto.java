package com.asset.demo.dto;

import java.util.List;

public record AssetByStatusResDto(
        List<AssetResDto> list,
        long totalPages,
        long totalElements
) {
}
