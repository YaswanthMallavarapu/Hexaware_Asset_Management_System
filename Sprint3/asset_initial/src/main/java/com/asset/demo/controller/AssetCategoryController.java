package com.asset.demo.controller;

import com.asset.demo.dto.AssetCategoryReqDto;
import com.asset.demo.model.AssetCategory;
import com.asset.demo.service.AssetCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/asset-category")
@CrossOrigin(origins = "http://localhost:5173/")
public class AssetCategoryController {
    private final AssetCategoryService assetCategoryService;

    /* Access : ADMIN */
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@Valid @RequestBody AssetCategoryReqDto assetCategoryReqDto){

        assetCategoryService.addCategory(assetCategoryReqDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    /* Access : ADMIN , EMPLOYEE */
    @GetMapping("/get-all")
    public ResponseEntity<List<AssetCategory>> getAllAssetCategory(@RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                 @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        List<AssetCategory> list=assetCategoryService.getAllAssetCategory(page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    /* Access : ADMIN , EMPLOYEE */
    @GetMapping("/get/{categoryId}")
    public ResponseEntity<AssetCategory> getAssetCategoryById(@PathVariable long categoryId){
        AssetCategory assetCategory=assetCategoryService.getAssetCategoryById(categoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(assetCategory);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=assetCategoryService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }



}
