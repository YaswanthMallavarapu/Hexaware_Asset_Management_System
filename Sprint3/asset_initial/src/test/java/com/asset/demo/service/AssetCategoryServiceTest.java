package com.asset.demo.service;


import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.repository.AssetCategoryRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AssetCategoryServiceTest {

    @InjectMocks
    private AssetCategoryService assetCategoryService;
    @Mock
    private  AssetCategoryRepository assetCategoryRepository;

    @Test
    public void getByIdTestWhenExist(){
        //check for null reference
        Assertions.assertNotNull(assetCategoryService);

        //create mock AssetCategory
        AssetCategory assetCategory=new AssetCategory();
        assetCategory.setId(1L);
        assetCategory.setCategoryName("Laptops");
        assetCategory.setDescription("test description");
        assetCategory.setQuantity(100);
        assetCategory.setRemaining(48);
        assetCategory.setCreatedAt(Instant.now());

        AssetCategory assetCategory1=new AssetCategory();
        assetCategory1.setId(1L);
        assetCategory1.setCategoryName("Laptops");
        assetCategory1.setDescription("test description");
        assetCategory1.setQuantity(190);
        assetCategory1.setRemaining(50);
        assetCategory1.setCreatedAt(Instant.now());

        //creating mock repo response
        when(assetCategoryRepository.findById(1L)).thenReturn(Optional.of(assetCategory));

        //verifying mock results
        Assertions.assertEquals(assetCategory,assetCategoryService.getById(1L));
        Assertions.assertNotEquals(assetCategory1,assetCategoryService.getById(1L));

        //also check for Db calls
        verify(assetCategoryRepository,times(2)).findById(1L);
    }

    @Test
    public void getByIdTestWhenNotExist(){
        //create mock result
        when(assetCategoryRepository.findById(100L)).thenReturn(Optional.empty());

        //verifying mock result
        Exception e=Assertions.assertThrows(ResourceNotFoundException.class,()->assetCategoryService.getById(100L));

        //checking for exception message
        Assertions.assertEquals("Category with id not found.",e.getMessage());


    }

    @Test
    public void getAll(){

        //create mock AssetCategory
        AssetCategory assetCategory=new AssetCategory();
        assetCategory.setId(1L);
        assetCategory.setCategoryName("Laptops");
        assetCategory.setDescription("test description");
        assetCategory.setQuantity(100);
        assetCategory.setRemaining(48);
        assetCategory.setCreatedAt(Instant.now());

        AssetCategory assetCategory1=new AssetCategory();
        assetCategory1.setId(2L);
        assetCategory1.setCategoryName("HeadPhones");
        assetCategory1.setDescription("test description");
        assetCategory1.setQuantity(50);
        assetCategory1.setRemaining(18);
        assetCategory1.setCreatedAt(Instant.now());

        List<AssetCategory>list= List.of(assetCategory,assetCategory1);

        when(assetCategoryRepository.findAll()).thenReturn(list);
        Assertions.assertEquals(list,assetCategoryService.getAllAssetCategory());




    }

}
