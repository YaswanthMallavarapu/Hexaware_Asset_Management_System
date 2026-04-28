package com.asset.demo.service;

import com.asset.demo.dto.AssetCategoryReqDto;
import com.asset.demo.dto.CategoryIdDto;
import com.asset.demo.enums.Role;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.repository.AssetCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetCategoryServiceTest {

    @Mock
    private AssetCategoryRepository assetCategoryRepository;

    @InjectMocks
    private AssetCategoryService assetCategoryService;

    @Test
    void addCategory_success() {

        AssetCategoryReqDto dto =
                new AssetCategoryReqDto("Electronics", "Devices");

        AssetCategory category = new AssetCategory();
        category.setCategoryName("Electronics");
        category.setDescription("Devices");

        assetCategoryService.addCategory(dto);

        verify(assetCategoryRepository, times(1)).save(any(AssetCategory.class));
    }

    @Test
    void getAllAssetCategory_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);
        category.setCategoryName("Electronics");

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
        category.setCategoryName("Electronics");

        when(assetCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        AssetCategory result =
                assetCategoryService.getAssetCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAssetCategoryById_notFound() {

        when(assetCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> assetCategoryService.getAssetCategoryById(1L)
        );
    }

    @Test
    void updateAssetCategory_success() {

        AssetCategory category = new AssetCategory();
        category.setId(1L);
        category.setCategoryName("Updated");

        assetCategoryService.updateAssetCategory(category);

        verify(assetCategoryRepository, times(1)).save(category);
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