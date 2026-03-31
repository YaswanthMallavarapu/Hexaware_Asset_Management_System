package com.asset.demo.service;

import com.asset.demo.dto.AssetResdto;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.Asset;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.repository.AssetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @InjectMocks
    private AssetService assetService;
    @Mock
    private AssetRepository assetRepository;

    @Test
    public void getAssetByIdTest(){
        //check for null reference
        Assertions.assertNotNull(assetService);

        //creating mock object
        AssetCategory assetCategory=new AssetCategory();
        assetCategory.setId(1L);
        assetCategory.setCategoryName("Laptops");
        assetCategory.setDescription("test description");
        assetCategory.setQuantity(100);
        assetCategory.setRemaining(48);
        assetCategory.setCreatedAt(Instant.now());
        Asset asset=new Asset();
        asset.setId(1L);
        asset.setAssetNo("LI320");
        asset.setAssetModel("Idea pad 3");
        asset.setAssetName("Lenova laptop");
        asset.setCategory(assetCategory);
        asset.setAssetValue(new BigDecimal(54000));
        Asset asset1=new Asset();
        asset.setId(2L);
        asset.setAssetNo("LI321");
        asset.setAssetModel("Idea pad 3");
        asset.setAssetName("Lenova laptop");
        asset.setCategory(assetCategory);
        asset.setAssetValue(new BigDecimal(54000));

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        Assertions.assertEquals(asset,assetService.getAssetByGivenId(1L));
        Assertions.assertNotEquals(asset1,assetService.getAssetByGivenId(1L));



    }

    @Test
    public void getAssetByIdTestWhenNotExist(){
        //create mock result
        when(assetRepository.findById(100L)).thenReturn(Optional.empty());

        //verifying mock result
        Exception e=Assertions.assertThrows(ResourceNotFoundException.class,()->assetService.getAssetByGivenId(100L));

        //checking for exception message
        Assertions.assertEquals("Asset Not found with id.",e.getMessage());

    }

    @Test
    public void getAssetDtoTestWhenExist(){
        AssetCategory assetCategory=new AssetCategory();
        assetCategory.setId(1L);
        assetCategory.setCategoryName("Laptops");
        assetCategory.setDescription("test description");
        assetCategory.setQuantity(100);
        assetCategory.setRemaining(48);
        assetCategory.setCreatedAt(Instant.now());
        Asset asset=new Asset();
        asset.setId(1L);
        asset.setAssetNo("LI320");
        asset.setAssetModel("Idea pad 3");
        asset.setAssetName("Lenova laptop");
        asset.setCategory(assetCategory);
        asset.setAssetValue(new BigDecimal(54000));

        AssetResdto dto=new AssetResdto(
                asset.getId(),
                asset.getAssetNo(),
                asset.getAssetName(),
                asset.getAssetModel(),
                asset.getManufacturingDate(),
                asset.getCategory().getId()
        );

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        Assertions.assertEquals(dto,assetService.getAssetById(1L));
    }

    @Test
    public void getAssetDtoByIdTestWhenNotExist(){
        //create mock result
        when(assetRepository.findById(100L)).thenReturn(Optional.empty());

        //verifying mock result
        Exception e=Assertions.assertThrows(ResourceNotFoundException.class,()->assetService.getAssetById(100L));

        //checking for exception message
        Assertions.assertEquals("Asset not found with Id.",e.getMessage());

    }

    @Test
    public void getAll(){
        //creating mock object
        AssetCategory assetCategory=new AssetCategory();
        assetCategory.setId(1L);
        assetCategory.setCategoryName("Laptops");
        assetCategory.setDescription("test description");
        assetCategory.setQuantity(100);
        assetCategory.setRemaining(48);
        assetCategory.setCreatedAt(Instant.now());
        Asset asset=new Asset();
        asset.setId(1L);
        asset.setAssetNo("LI320");
        asset.setAssetModel("Idea pad 3");
        asset.setAssetName("Lenova laptop");
        asset.setCategory(assetCategory);
        asset.setAssetValue(new BigDecimal(54000));
        Asset asset1=new Asset();
        asset1.setId(2L);
        asset1.setAssetNo("LI321");
        asset1.setAssetModel("Idea pad 3");
        asset1.setAssetName("Lenova laptop");
        asset1.setCategory(assetCategory);
        asset1.setAssetValue(new BigDecimal(54000));
        List<Asset>list=List.of(asset,asset1);

        //testing for page=0 and size=2
        Page<Asset> pageAsset=new PageImpl<>(list);
        int page=0;
        int size=2;
        Pageable pageable= PageRequest.of(page,size);
        Mockito.when(assetRepository.findAll(pageable)).thenReturn(pageAsset);
        Assertions.assertEquals(2,assetService.getAllAssets(0,2).size());

        //testing for page=0 and size=1
        pageAsset=new PageImpl<>(list.subList(0,1));
        page=0;
        size=1;
        pageable= PageRequest.of(page,size);
        Mockito.when(assetRepository.findAll(pageable)).thenReturn(pageAsset);
        Assertions.assertEquals(1,assetService.getAllAssets(0,1).size());

        //testing for page=0 and size=3
        pageAsset=new PageImpl<>(list.subList(0,2));
        page=0;
        size=3;
        pageable= PageRequest.of(page,size);
        Mockito.when(assetRepository.findAll(pageable)).thenReturn(pageAsset);
        Assertions.assertEquals(2,assetService.getAllAssets(0,3).size());

    }


}
