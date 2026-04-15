package com.asset.demo.service;

import com.asset.demo.dto.AssetReqDto;
import com.asset.demo.dto.AssetResDto;
import com.asset.demo.enums.AssetStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.AssetMapper;
import com.asset.demo.model.Asset;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.repository.AssetRepository;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AssetService {
    private final AssetRepository assetRepository;
    private final AssetCategoryService assetCategoryService;
    private final UserService userService;

    public void addAsset(@Valid AssetReqDto assetReqDto) {
        //check for category
        AssetCategory assetCategory=assetCategoryService.getAssetCategoryById(assetReqDto.assetCategoryId());

        assetCategory.setQuantity(assetCategory.getQuantity()+1);
        assetCategory.setRemaining(assetCategory.getRemaining()+1);
        assetCategoryService.updateAssetCategory(assetCategory);
        //map dto to entity
        Asset asset= AssetMapper.mapToEntity(assetReqDto);
        //add category to asset
        asset.setCategory(assetCategory);
        //add to DB
        assetRepository.save(asset);

    }

    public List<AssetResDto> getAllAssets(int page, int size) {
        Pageable pageable=PageRequest.of(page,size);
        Page<Asset> list=assetRepository.findAll(pageable);
        return list
                .toList()
                .stream()
                .map(AssetMapper::mapToDto)
                .toList();
    }

    public AssetResDto getAssetById(long assetId) {
        Asset asset=assetRepository.findById(assetId)
                .orElseThrow(()->new ResourceNotFoundException("Asset not found with Id."));
    return AssetMapper.mapToDto(asset);
    }

    public Asset getAssetByGivenId(long assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(()->new ResourceNotFoundException("Asset Not found with id."));
    }

    public Asset updateAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    public List<AssetResDto> getAssetByCategory(long categoryId) {
        List<Asset>list=assetRepository.getAssetByCategory(categoryId);
        return list.stream()
                .map(AssetMapper::mapToDto)
                .toList();
    }


    public List<AssetResDto> getAssetByStatus(int page, int size, String status) {
        AssetStatus assetStatus=AssetStatus.valueOf(status);
        Pageable pageable=PageRequest.of(page,size);
        Page<Asset>pageAsset=assetRepository.getByStatus(assetStatus,pageable);
        return pageAsset
                .toList()
                .stream()
                .map(AssetMapper::mapToDto)
                .toList();
    }

    public long getCount() {
        return assetRepository.count();
    }
}
