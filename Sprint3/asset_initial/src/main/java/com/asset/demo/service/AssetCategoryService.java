package com.asset.demo.service;

import com.asset.demo.dto.AssetCategoryReqDto;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.repository.AssetCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AssetCategoryService {
    private final AssetCategoryRepository assetCategoryRepository;

    public void addCategory(AssetCategoryReqDto assetCategoryReqDto) {
        //map this to AssetCategory entity
        //it can be done directly
        AssetCategory assetCategory=new AssetCategory();
        assetCategory.setCategoryName(assetCategoryReqDto.categoryName());
        assetCategory.setDescription(assetCategoryReqDto.text());
        assetCategory.setQuantity(0);
        //when added both remaining and quantity wil be same
        assetCategory.setRemaining(0);
        //save in DB
        assetCategoryRepository.save(assetCategory);

    }


    public List<AssetCategory> getAllAssetCategory(int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<AssetCategory> pageAssetCategory=assetCategoryRepository.findAll(pageable);
        return pageAssetCategory
                .toList();
    }


    public AssetCategory getAssetCategoryById(long categoryId) {
        return assetCategoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category with id not found."));
    }

    public void updateAssetCategory(AssetCategory assetCategory) {
        assetCategoryRepository.save(assetCategory);
    }
}
