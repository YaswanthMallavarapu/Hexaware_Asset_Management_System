package com.asset.demo.service;

import com.asset.demo.dto.AssetCategoryReqDto;
import com.asset.demo.dto.CategoryIdDto;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.repository.AssetCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AssetCategoryServiceTest {

    @Mock
    private AssetCategoryRepository assetCategoryRepository;

    @InjectMocks
    private AssetCategoryService assetCategoryService;

    @Test
    void addCategory_success() {

        AssetCategoryReqDto dto =
                new AssetCategoryReqDto("Electronics", "Devices");

        assetCategoryService.addCategory(dto);

        verify(assetCategoryRepository).save(any(AssetCategory.class));
    }

    @Test
    void getAllAssetCategory_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);

        Page<AssetCategory> page = new PageImpl<>(List.of(category));

        when(assetCategoryRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        List<AssetCategory> result =
                assetCategoryService.getAllAssetCategory(0, 5);

        assertEquals(1, result.size());
    }

    @Test
    void getAssetCategoryById_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);

        when(assetCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        AssetCategory result =
                assetCategoryService.getAssetCategoryById(1L);

        assertNotNull(result);
    }

    @Test
    void getAssetCategoryById_notFound() {

        when(assetCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assetCategoryService.getAssetCategoryById(1L));
    }

    @Test
    void updateAssetCategory_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);

        assetCategoryService.updateAssetCategory(category);

        verify(assetCategoryRepository).save(category);
    }

    @Test
    void getCount_success() {

        when(assetCategoryRepository.count()).thenReturn(5L);

        long count = assetCategoryService.getCount();

        assertEquals(5L, count);
    }

    @Test
    void getAllWithId_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);
        category.setCategoryName("Electronics");

        when(assetCategoryRepository.findAll())
                .thenReturn(List.of(category));

        List<CategoryIdDto> result =
                assetCategoryService.getAllWithId();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.getFirst().name());
    }
}