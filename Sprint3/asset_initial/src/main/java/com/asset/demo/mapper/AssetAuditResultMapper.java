package com.asset.demo.mapper;

import com.asset.demo.dto.AssetAuditResultResDto;
import com.asset.demo.model.AssetAuditResult;

public class AssetAuditResultMapper {
    public static AssetAuditResultResDto mapToDto(AssetAuditResult assetAuditResult){
        return new AssetAuditResultResDto(
                assetAuditResult.getId(),
                assetAuditResult.getAssetAllocation().getAsset().getId(),
                assetAuditResult.getAssetAllocation().getAsset().getAssetName(),
                assetAuditResult.getAssetAllocation().getId(),
                assetAuditResult.getAudit().getId(),
                assetAuditResult.getAuditDate(),
                assetAuditResult.getStatus()
        );

    }
}
