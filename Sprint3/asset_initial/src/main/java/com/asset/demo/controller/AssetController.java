package com.asset.demo.controller;

import com.asset.demo.dto.AssetReqDto;
import com.asset.demo.dto.AssetResDto;
import com.asset.demo.service.AssetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/asset")
public class AssetController {
    private final AssetService assetService;

    /* Access : ADMIN  */
    @PostMapping("/add")
    public ResponseEntity<?> addAsset(@Valid @RequestBody AssetReqDto assetReqDto
                         ){
        assetService.addAsset(assetReqDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }
    /* Access : ADMIN , EMPLOYEE */
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAssets(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                          @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        List<AssetResDto> list=assetService.getAllAssets(page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    @GetMapping("/get/status/{status}")
    public ResponseEntity<?> getAssetByUser(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                            @PathVariable String status){
       List<AssetResDto>list=assetService.getAssetByStatus(page,size,status);
       return ResponseEntity
               .status(HttpStatus.OK)
               .body(list);
    }
    /* Access : ADMIN , EMPLOYEE */
    @GetMapping("/get/{assetId}")
    public ResponseEntity<?> getAssetById(@PathVariable long assetId){
       AssetResDto asset=assetService.getAssetById(assetId);
       return ResponseEntity
               .status(HttpStatus.OK)
               .body(asset);

    }

    @GetMapping("/get/category/{categoryId}")
    public ResponseEntity<?> getAssetByCategory(@PathVariable long categoryId){
        List<AssetResDto> list=assetService.getAssetByCategory(categoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }



}
