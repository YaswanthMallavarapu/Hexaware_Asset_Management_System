package com.asset.demo.service;

import com.asset.demo.dto.AssetByStatusResDto;
import com.asset.demo.dto.AssetPageResDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetCategoryService assetCategoryService;
    private final UserService userService;

    public void addAsset(@Valid AssetReqDto assetReqDto) {

        log.atInfo().log("Add asset started | categoryId={}", assetReqDto.assetCategoryId());

        AssetCategory assetCategory =
                assetCategoryService.getAssetCategoryById(assetReqDto.assetCategoryId());

        assetCategory.setQuantity(assetCategory.getQuantity() + 1);
        assetCategory.setRemaining(assetCategory.getRemaining() + 1);
        assetCategoryService.updateAssetCategory(assetCategory);

        Asset asset = AssetMapper.mapToEntity(assetReqDto);
        asset.setCategory(assetCategory);

        assetRepository.save(asset);

        log.atInfo().log("Asset added successfully | categoryId={}", assetReqDto.assetCategoryId());
    }

    public AssetPageResDto getAllAssets(int page, int size) {

        log.atInfo().log("Fetching all assets | page={} | size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Asset> list = assetRepository.findAll(pageable);

        List<AssetResDto> resList = list.toList()
                .stream()
                .map(AssetMapper::mapToDto)
                .toList();

        log.atInfo().log("Assets fetched count={}", list.getTotalElements());

        return new AssetPageResDto(resList,
                list.getTotalElements(),
                list.getTotalPages());
    }

    public AssetResDto getAssetById(long assetId) {

        log.atInfo().log("Fetching asset by id={}", assetId);

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> {
                    log.atError().log("Asset not found id={}", assetId);
                    return new ResourceNotFoundException("Asset not found with Id.");
                });

        return AssetMapper.mapToDto(asset);
    }

    public Asset getAssetByGivenId(long assetId) {

        log.atInfo().log("Fetching asset entity by id={}", assetId);

        return assetRepository.findById(assetId)
                .orElseThrow(() -> {
                    log.atError().log("Asset entity not found id={}", assetId);
                    return new ResourceNotFoundException("Asset Not found with id.");
                });
    }

    public Asset updateAsset(Asset asset) {

        log.atInfo().log("Updating asset id={}", asset.getId());

        return assetRepository.save(asset);
    }

    public List<AssetResDto> getAssetByCategory(long categoryId) {

        log.atInfo().log("Fetching assets by categoryId={}", categoryId);

        List<Asset> list = assetRepository.getAssetByCategory(categoryId);

        return list.stream()
                .map(AssetMapper::mapToDto)
                .toList();
    }

    public AssetByStatusResDto getAssetByStatus(int page, int size, String status) {

        log.atInfo().log("Fetching assets by status={}", status);

        AssetStatus assetStatus = AssetStatus.valueOf(status);
        Pageable pageable = PageRequest.of(page, size);

        Page<Asset> pageAsset = assetRepository.getByStatus(assetStatus, pageable);

        List<AssetResDto> list = pageAsset.toList()
                .stream()
                .map(AssetMapper::mapToDto)
                .toList();

        return new AssetByStatusResDto(list,
                pageAsset.getTotalPages(),
                pageAsset.getTotalElements());
    }

    public long getCount() {
        log.atInfo().log("Fetching asset count");
        return assetRepository.count();
    }

    public List<AssetResDto> getAllAsset() {

        log.atInfo().log("Fetching all assets (no pagination)");

        return assetRepository.findAll()
                .stream()
                .map(AssetMapper::mapToDto)
                .toList();
    }
}