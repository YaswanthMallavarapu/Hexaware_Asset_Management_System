package com.asset.demo.mapper;

import com.asset.demo.dto.AssetRequestReqDto;
import com.asset.demo.dto.AssetRequestResDto;
import com.asset.demo.model.AssetRequest;

public class AssetRequestMapper {
    public static AssetRequest mapToEntity(AssetRequestReqDto assetRequestReqdto){
        AssetRequest assetRequest=new AssetRequest();
        assetRequest.setRemarks(assetRequestReqdto.remarks());
        return assetRequest;

    }
    public static AssetRequestResDto mapToDto(AssetRequest assetRequest){
        return new AssetRequestResDto(
                assetRequest.getId(),

                assetRequest.getEmployee().getId(),
                assetRequest.getEmployee().getFirstName()+" "+assetRequest.getEmployee().getLastName(),
                assetRequest.getAsset().getId(),
                assetRequest.getAsset().getAssetName(),
                assetRequest.getRequestDate(),
                assetRequest.getStatus()
        );
    }
}
