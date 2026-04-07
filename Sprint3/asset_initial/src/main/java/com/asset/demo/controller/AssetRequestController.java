package com.asset.demo.controller;

import com.asset.demo.dto.AssetRequestReqDto;
import com.asset.demo.dto.AssetRequestResDto;
import com.asset.demo.model.AssetRequest;
import com.asset.demo.model.ServiceRequest;
import com.asset.demo.service.AssetRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/asset-request")
public class AssetRequestController {

    private final AssetRequestService assetRequestService;
    /* Access :  EMPLOYEE */
    @PostMapping("/add/{assetId}")
    public ResponseEntity<?> requestAsset(@RequestBody AssetRequestReqDto assetRequestReqdto,
                                          @PathVariable long assetId,
                                          Principal principal){
        assetRequestService.requestAsset(assetRequestReqdto,assetId,principal.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }
    /* Access : MANAGER  */
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAssetRequests(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                                  @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        List<AssetRequestResDto> list=assetRequestService.getAllAssetRequests(page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    /* Access : EMPLOYEE  */
    @GetMapping("/get/user")
    public ResponseEntity<?> getAssetRequestByUser(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                                          @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                                                          Principal principal){
        List<AssetRequestResDto>list=assetRequestService.getRequestByUser(principal.getName(),page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    /* Access : MANAGER  */
    @GetMapping("/get/status/{status}")
    public ResponseEntity<?> getAssetRequestByStatus(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                   @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                                   @PathVariable String status){
        List<AssetRequestResDto>list=assetRequestService.getRequestByStatus(status,page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

}
