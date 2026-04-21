package com.asset.demo.dto;

import com.asset.demo.enums.AssetStatus;

import java.time.LocalDate;

public record AssetResDto(
        long id,

        String assetNo,

        String assetName,

        String assetModel,

        LocalDate manufacturedDate,

        long categoryId,
        String categoryName,
        AssetStatus assetStatus


) {
}
