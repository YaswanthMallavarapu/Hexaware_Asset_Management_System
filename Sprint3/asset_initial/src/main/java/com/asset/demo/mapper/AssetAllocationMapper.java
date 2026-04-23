package com.asset.demo.mapper;

import com.asset.demo.dto.AssetAllocationAuditResDto;
import com.asset.demo.dto.AssetAllocationResDto;
import com.asset.demo.enums.AssetStatus;
import com.asset.demo.enums.AuditStatus;
import com.asset.demo.model.AssetAllocation;

public class AssetAllocationMapper {
    public static AssetAllocationResDto mapToDto(AssetAllocation assetAllocation){
        return new AssetAllocationResDto(
                assetAllocation.getId(),
                assetAllocation.getAsset().getId(),
                assetAllocation.getEmployee().getId(),
                assetAllocation.getAsset().getAssetName(),
                assetAllocation.getEmployee().getFirstName()+" "+assetAllocation.getEmployee().getLastName(),
                assetAllocation.getManager().getFirstName()+assetAllocation.getManager().getLastName(),
                assetAllocation.getAllocationDate(),
                assetAllocation.getStatus()

        );
    }
}
