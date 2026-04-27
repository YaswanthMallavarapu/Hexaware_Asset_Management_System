package com.asset.demo.service;

import com.asset.demo.dto.AssetByStatusResDto;
import com.asset.demo.dto.AssetPageResDto;
import com.asset.demo.dto.AssetReqDto;
import com.asset.demo.dto.AssetResDto;
import com.asset.demo.enums.AssetStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.Asset;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.repository.AssetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @InjectMocks
    private AssetService assetService;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetCategoryService assetCategoryService;

    @Mock
    private UserService userService;

    @Test
    void addAsset_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);
        category.setCategoryName("Laptops");
        category.setQuantity(5);
        category.setRemaining(3);
        category.setCreatedAt(Instant.now());

        AssetReqDto dto = new AssetReqDto(
                "A1",
                "Laptop",
                "Dell",
                1L,
                LocalDate.now(),
                new BigDecimal(50000)
        );

        when(assetCategoryService.getAssetCategoryById(1L)).thenReturn(category);

        assetService.addAsset(dto);

        verify(assetRepository, times(1)).save(any(Asset.class));
        verify(assetCategoryService, times(1)).updateAssetCategory(category);
    }

    @Test
    void getAllAssets_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);
        category.setCategoryName("Laptops");

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setAssetNo("A101");
        asset.setAssetName("Dell");
        asset.setAssetModel("XPS");
        asset.setCategory(category);
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setAssetValue(new BigDecimal(50000));

        Page<Asset> page = new PageImpl<>(List.of(asset));

        when(assetRepository.findAll(any(Pageable.class))).thenReturn(page);

        AssetPageResDto result = assetService.getAllAssets(0, 5);

        Assertions.assertEquals(1, result.list().size());
    }

    @Test
    void getAssetById_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setAssetNo("A101");
        asset.setAssetName("Dell");
        asset.setAssetModel("XPS");
        asset.setCategory(category);
        asset.setStatus(AssetStatus.AVAILABLE);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        AssetResDto result = assetService.getAssetById(1L);

        Assertions.assertEquals(1L, result.id());
    }

    @Test
    void getAssetById_notFound() {

        when(assetRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> assetService.getAssetById(1L)
        );
    }

    @Test
    void getAssetByGivenId_success() {

        Asset asset = new Asset();
        asset.setId(1L);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        Asset result = assetService.getAssetByGivenId(1L);

        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    void getAssetByGivenId_notFound() {

        when(assetRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> assetService.getAssetByGivenId(1L)
        );
    }

    @Test
    void updateAsset_success() {

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setAssetName("Dell");

        when(assetRepository.save(asset)).thenReturn(asset);

        Asset result = assetService.updateAsset(asset);

        Assertions.assertEquals("Dell", result.getAssetName());
    }

    @Test
    void getAssetByCategory_success() {

        Asset asset = new Asset();
        asset.setId(1L);

        when(assetRepository.getAssetByCategory(1L))
                .thenReturn(List.of(asset));

        var result = assetService.getAssetByCategory(1L);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAssetByStatus_success() {

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setStatus(AssetStatus.AVAILABLE);

        Page<Asset> page = new PageImpl<>(List.of(asset));

        when(assetRepository.getByStatus(eq(AssetStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(page);

        AssetByStatusResDto result =
                assetService.getAssetByStatus(0, 5, "AVAILABLE");

        Assertions.assertEquals(1, result.list().size());
    }

    @Test
    void getAssetByStatus_invalidStatus() {

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> assetService.getAssetByStatus(0, 5, "INVALID")
        );
    }

    @Test
    void getCount_success() {

        when(assetRepository.count()).thenReturn(10L);

        long count = assetService.getCount();

        Assertions.assertEquals(10L, count);
    }
}