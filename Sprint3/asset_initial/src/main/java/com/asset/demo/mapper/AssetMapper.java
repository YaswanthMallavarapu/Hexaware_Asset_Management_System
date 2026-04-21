package com.asset.demo.mapper;

import com.asset.demo.dto.AssetReqDto;
import com.asset.demo.dto.AssetResDto;
import com.asset.demo.model.Asset;

public class AssetMapper {
    public static Asset mapToEntity(AssetReqDto assetReqDto){
        Asset asset =new Asset();
        asset.setAssetNo(assetReqDto.assetNo());
        asset.setAssetName(assetReqDto.assetName());
        asset.setAssetModel(assetReqDto.assetModel());
        asset.setManufacturingDate(assetReqDto.manufacturedDate());
        asset.setAssetValue(assetReqDto.price());
        return asset;
    }
    public static AssetResDto mapToDto(Asset asset){
        return new AssetResDto(
                asset.getId(),
                asset.getAssetNo(),
                asset.getAssetName(),
                asset.getAssetModel(),
                asset.getManufacturingDate(),
                asset.getCategory().getId(),
                asset.getCategory().getCategoryName(),
                asset.getStatus()
        );
    }
}
