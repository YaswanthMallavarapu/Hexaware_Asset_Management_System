//package com.asset.demo.service;
//
//import com.asset.demo.dto.AssetReqDto;
//import com.asset.demo.dto.AssetResDto;
//import com.asset.demo.enums.AssetStatus;
//import com.asset.demo.exceptions.ResourceNotFoundException;
//import com.asset.demo.mapper.AssetMapper;
//import com.asset.demo.model.Asset;
//import com.asset.demo.model.AssetCategory;
//import com.asset.demo.repository.AssetCategoryRepository;
//import com.asset.demo.repository.AssetRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import static org.mockito.Mockito.*;
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@ExtendWith(MockitoExtension.class)
//public class AssetServiceTest {
//
//    @InjectMocks
//    private AssetService assetService;
//    @Mock
//    private AssetRepository assetRepository;
//    @Mock
//    private AssetCategoryService  assetCategoryService;
//
//    @Test
//    public void getAssetByIdTest(){
//        //check for null reference
//        Assertions.assertNotNull(assetService);
//
//        //creating mock object
//        AssetCategory assetCategory=new AssetCategory();
//        assetCategory.setId(1L);
//        assetCategory.setCategoryName("Laptops");
//        assetCategory.setDescription("test description");
//        assetCategory.setQuantity(100);
//        assetCategory.setRemaining(48);
//        assetCategory.setCreatedAt(Instant.now());
//        Asset asset=new Asset();
//        asset.setId(1L);
//        asset.setAssetNo("LI320");
//        asset.setAssetModel("Idea pad 3");
//        asset.setAssetName("Lenova laptop");
//        asset.setCategory(assetCategory);
//        asset.setAssetValue(new BigDecimal(54000));
//        Asset asset1=new Asset();
//        asset.setId(2L);
//        asset.setAssetNo("LI321");
//        asset.setAssetModel("Idea pad 3");
//        asset.setAssetName("Lenova laptop");
//        asset.setCategory(assetCategory);
//        asset.setAssetValue(new BigDecimal(54000));
//
//        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
//
//        Assertions.assertEquals(asset,assetService.getAssetByGivenId(1L));
//        Assertions.assertNotEquals(asset1,assetService.getAssetByGivenId(1L));
//
//
//
//    }
//
//    @Test
//    public void getAssetByIdTestWhenNotExist(){
//        //create mock result
//        when(assetRepository.findById(100L)).thenReturn(Optional.empty());
//
//        //verifying mock result
//        Exception e=Assertions.assertThrows(ResourceNotFoundException.class,()->assetService.getAssetByGivenId(100L));
//
//        //checking for exception message
//        Assertions.assertEquals("Asset Not found with id.",e.getMessage());
//
//    }
//
//    @Test
//    public void getAssetDtoTestWhenExist(){
//        AssetCategory assetCategory=new AssetCategory();
//        assetCategory.setId(1L);
//        assetCategory.setCategoryName("Laptops");
//        assetCategory.setDescription("test description");
//        assetCategory.setQuantity(100);
//        assetCategory.setRemaining(48);
//        assetCategory.setCreatedAt(Instant.now());
//        Asset asset=new Asset();
//        asset.setId(1L);
//        asset.setAssetNo("LI320");
//        asset.setAssetModel("Idea pad 3");
//        asset.setAssetName("Lenova laptop");
//        asset.setCategory(assetCategory);
//        asset.setAssetValue(new BigDecimal(54000));
//
//        AssetResDto dto=new AssetResDto(
//                asset.getId(),
//                asset.getAssetNo(),
//                asset.getAssetName(),
//                asset.getAssetModel(),
//                asset.getManufacturingDate(),
//                asset.getCategory().getId(),
//                asset.getStatus()
//        );
//
//        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
//
//        Assertions.assertEquals(dto,assetService.getAssetById(1L));
//    }
//
//    @Test
//    public void getAssetDtoByIdTestWhenNotExist(){
//        //create mock result
//        when(assetRepository.findById(100L)).thenReturn(Optional.empty());
//
//        //verifying mock result
//        Exception e=Assertions.assertThrows(ResourceNotFoundException.class,()->assetService.getAssetById(100L));
//
//        //checking for exception message
//        Assertions.assertEquals("Asset not found with Id.",e.getMessage());
//
//    }
//
//    @Test
//    public void getAll(){
//        //creating mock object
//        AssetCategory assetCategory=new AssetCategory();
//        assetCategory.setId(1L);
//        assetCategory.setCategoryName("Laptops");
//        assetCategory.setDescription("test description");
//        assetCategory.setQuantity(100);
//        assetCategory.setRemaining(48);
//        assetCategory.setCreatedAt(Instant.now());
//        Asset asset=new Asset();
//        asset.setId(1L);
//        asset.setAssetNo("LI320");
//        asset.setAssetModel("Idea pad 3");
//        asset.setAssetName("Lenova laptop");
//        asset.setCategory(assetCategory);
//        asset.setAssetValue(new BigDecimal(54000));
//        Asset asset1=new Asset();
//        asset1.setId(2L);
//        asset1.setAssetNo("LI321");
//        asset1.setAssetModel("Idea pad 3");
//        asset1.setAssetName("Lenova laptop");
//        asset1.setCategory(assetCategory);
//        asset1.setAssetValue(new BigDecimal(54000));
//        List<Asset>list=List.of(asset,asset1);
//
//        //testing for page=0 and size=2
//        Page<Asset> pageAsset=new PageImpl<>(list);
//        int page=0;
//        int size=2;
//        Pageable pageable= PageRequest.of(page,size);
//        Mockito.when(assetRepository.findAll(pageable)).thenReturn(pageAsset);
//        Assertions.assertEquals(2,assetService.getAllAssets(0,2).size());
//
//        //testing for page=0 and size=1
//        pageAsset=new PageImpl<>(list.subList(0,1));
//        page=0;
//        size=1;
//        pageable= PageRequest.of(page,size);
//        Mockito.when(assetRepository.findAll(pageable)).thenReturn(pageAsset);
//        Assertions.assertEquals(1,assetService.getAllAssets(0,1).size());
//
//        //testing for page=0 and size=3
//        pageAsset=new PageImpl<>(list.subList(0,2));
//        page=0;
//        size=3;
//        pageable= PageRequest.of(page,size);
//        Mockito.when(assetRepository.findAll(pageable)).thenReturn(pageAsset);
//        Assertions.assertEquals(2,assetService.getAllAssets(0,3).size());
//
//    }
//
//    @Test
//    public void addAssetTest(){
//        //creating mock object
//        AssetCategory assetCategory=new AssetCategory();
//        assetCategory.setId(1L);
//        assetCategory.setCategoryName("Laptops");
//        assetCategory.setDescription("test description");
//        assetCategory.setQuantity(100);
//        assetCategory.setRemaining(48);
//        assetCategory.setCreatedAt(Instant.now());
//        Asset asset=new Asset();
//        asset.setId(1L);
//        asset.setAssetNo("LI320");
//        asset.setAssetModel("Idea pad 3");
//        asset.setAssetName("Lenova laptop");
//        asset.setCategory(assetCategory);
//        asset.setAssetValue(new BigDecimal(54000));
//
//        AssetReqDto dto=new AssetReqDto("len123","lenovo laptop","ideapad 3",1L, LocalDate.now(),new BigDecimal(54000));
//       when(assetCategoryService.getAssetCategoryById(1L)).thenReturn(assetCategory);
//
//        assetService.addAsset(dto);
//
//        ArgumentCaptor<Asset>captor=ArgumentCaptor.forClass(Asset.class);
//
//        verify(assetRepository).save(captor.capture());
//
//        Asset savedAsset=captor.getValue();
//        Assertions.assertEquals(assetCategory,savedAsset.getCategory());
//        Assertions.assertEquals(AssetStatus.AVAILABLE,asset.getStatus());
//
//
//    }
//
//    @Test
//    public void updateAssetTest(){
//        AssetCategory assetCategory=new AssetCategory();
//        assetCategory.setId(1L);
//        assetCategory.setCategoryName("Laptops");
//        assetCategory.setDescription("test description");
//        assetCategory.setQuantity(100);
//        assetCategory.setRemaining(48);
//        assetCategory.setCreatedAt(Instant.now());
//        Asset asset=new Asset();
//        asset.setId(1L);
//        asset.setAssetNo("LI320");
//        asset.setAssetModel("Idea pad 3");
//        asset.setAssetName("Lenova laptop");
//        asset.setCategory(assetCategory);
//        asset.setAssetValue(new BigDecimal(54000));
//
//        when(assetRepository.save(asset)).thenReturn(asset);
//
//        Assertions.assertEquals(asset,assetService.updateAsset(asset));
//        verify(assetRepository,times(1)).save(asset);
//    }
//
//    @Test
//    public void getAssetByCategoryTest(){
//        //creating mock object
//        AssetCategory assetCategory=new AssetCategory();
//        assetCategory.setId(1L);
//        assetCategory.setCategoryName("Laptops");
//        assetCategory.setDescription("test description");
//        assetCategory.setQuantity(100);
//        assetCategory.setRemaining(48);
//        assetCategory.setCreatedAt(Instant.now());
//        Asset asset=new Asset();
//        asset.setId(1L);
//        asset.setAssetNo("LI320");
//        asset.setAssetModel("Idea pad 3");
//        asset.setAssetName("Lenova laptop");
//        asset.setCategory(assetCategory);
//        asset.setAssetValue(new BigDecimal(54000));
//        Asset asset1=new Asset();
//        asset1.setId(2L);
//        asset1.setAssetNo("LI321");
//        asset1.setAssetModel("Idea pad 3");
//        asset1.setAssetName("Lenova laptop");
//        asset1.setCategory(assetCategory);
//        asset1.setAssetValue(new BigDecimal(54000));
//        List<Asset>list=List.of(asset,asset1);
//
//        AssetResDto assetResDto=new AssetResDto(
//                asset.getId(),asset.getAssetNo(),
//                asset.getAssetName(),
//                asset.getAssetModel(),
//                asset.getManufacturingDate(),
//                asset.getCategory().getId(),
//                asset.getStatus());
//        AssetResDto assetResDto1 = new AssetResDto(
//                asset1.getId(),
//                asset1.getAssetNo(),
//                asset1.getAssetName(),
//                asset1.getAssetModel(),
//                asset1.getManufacturingDate(),
//                asset1.getCategory().getId(),
//                asset.getStatus()
//        );
//        List<AssetResDto>reslist=List.of(assetResDto,assetResDto1);
//        when(assetRepository.getAssetByCategory(1L)).thenReturn(list);
//
//        Assertions.assertEquals(reslist,assetService.getAssetByCategory(1L));
//
//    }
//
//    @Test
//    public void getAssetByStatus(){
//        //creating mock object
//        AssetCategory assetCategory=new AssetCategory();
//        assetCategory.setId(1L);
//        assetCategory.setCategoryName("Laptops");
//        assetCategory.setDescription("test description");
//        assetCategory.setQuantity(100);
//        assetCategory.setRemaining(48);
//        assetCategory.setCreatedAt(Instant.now());
//        Asset asset=new Asset();
//        asset.setId(1L);
//        asset.setAssetNo("LI320");
//        asset.setAssetModel("Idea pad 3");
//        asset.setAssetName("Lenova laptop");
//        asset.setCategory(assetCategory);
//        asset.setAssetValue(new BigDecimal(54000));
//        Asset asset1=new Asset();
//        asset1.setId(2L);
//        asset1.setAssetNo("LI321");
//        asset1.setAssetModel("Idea pad 3");
//        asset1.setAssetName("Lenova laptop");
//        asset1.setCategory(assetCategory);
//        asset1.setAssetValue(new BigDecimal(54000));
//        List<Asset>list=List.of(asset,asset1);
//
//        AssetResDto assetResDto=new AssetResDto(
//                asset.getId(),asset.getAssetNo(),
//                asset.getAssetName(),
//                asset.getAssetModel(),
//                asset.getManufacturingDate(),
//                asset.getCategory().getId(),
//                asset.getStatus());
//        AssetResDto assetResDto1 = new AssetResDto(
//                asset1.getId(),
//                asset1.getAssetNo(),
//                asset1.getAssetName(),
//                asset1.getAssetModel(),
//                asset1.getManufacturingDate(),
//                asset1.getCategory().getId(),
//                asset1.getStatus()
//        );
//        List<AssetResDto>reslist=List.of(assetResDto,assetResDto1);
//        Page<AssetResDto>pageAsset=new PageImpl<>(reslist);
//        Page<Asset>pageAssetDto=new PageImpl<>(list);
//        int page=0;
//        int size=2;
//        Pageable pageable=PageRequest.of(page,size);
//        when(assetRepository.getByStatus(AssetStatus.AVAILABLE,pageable)).thenReturn(pageAssetDto);
//        when(assetRepository.getByStatus(AssetStatus.RETIRED,pageable)).thenReturn(Page.empty());
//
//        Assertions.assertEquals(pageAsset.toList(),assetService.getAssetByStatus(0,2,"AVAILABLE"));
//        Assertions.assertEquals(Page.empty().toList(),assetService.getAssetByStatus(0,2,"RETIRED"));
//
//
//    }
//
//
//
//
//
//}
