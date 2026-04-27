package com.asset.demo.service;

import com.asset.demo.dto.AssetCategoryReqDto;
import com.asset.demo.dto.CategoryIdDto;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.repository.AssetCategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AssetCategoryService {

    private final AssetCategoryRepository assetCategoryRepository;

    public void addCategory(AssetCategoryReqDto dto) {

        log.atInfo().log("Adding new asset category: {}", dto.categoryName());

        AssetCategory category = new AssetCategory();
        category.setCategoryName(dto.categoryName());
        category.setDescription(dto.text());
        category.setQuantity(0);
        category.setRemaining(0);

        assetCategoryRepository.save(category);

        log.atInfo().log("Category created successfully: {}", dto.categoryName());
    }

    public List<AssetCategory> getAllAssetCategory(int page, int size) {

        log.atInfo().log("Fetching asset categories page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AssetCategory> pageData = assetCategoryRepository.findAll(pageable);

        return pageData.getContent();
    }

    public AssetCategory getAssetCategoryById(long categoryId) {

        log.atInfo().log("Fetching category by ID {}", categoryId);

        return assetCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id not found."));
    }

    public void updateAssetCategory(AssetCategory category) {

        log.atInfo().log("Updating category ID {}", category.getId());

        assetCategoryRepository.save(category);
    }

    public long getCount() {

        long count = assetCategoryRepository.count();
        log.atInfo().log("Total category count {}", count);

        return count;
    }

    public List<CategoryIdDto> getAllWithId() {

        log.atInfo().log("Fetching all categories with ID");

        List<AssetCategory> list = assetCategoryRepository.findAll();
        List<CategoryIdDto> result = new ArrayList<>();

        list.forEach(c ->
                result.add(new CategoryIdDto(c.getId(), c.getCategoryName()))
        );

        log.atInfo().log("Fetched {} categories", result.size());

        return result;
    }
}