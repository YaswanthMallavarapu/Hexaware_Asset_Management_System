package com.asset.demo.controller;

import com.asset.demo.dto.AssetByStatusResDto;
import com.asset.demo.dto.AssetPageResDto;
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
@CrossOrigin(origins = "http://localhost:5173/")
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
    public ResponseEntity<AssetPageResDto> getAllAssets(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                          @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        AssetPageResDto list=assetService.getAllAssets(page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    @GetMapping("/get/status/{status}")
    public ResponseEntity<AssetByStatusResDto> getAssetByUser(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                            @PathVariable String status){
       AssetByStatusResDto assetByStatusResDto=assetService.getAssetByStatus(page,size,status);
       return ResponseEntity
               .status(HttpStatus.OK)
               .body(assetByStatusResDto);
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

    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=assetService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }


}
