package com.asset.demo.controller;

import com.asset.demo.dto.AssetAllocationPageResDto;
import com.asset.demo.dto.AssetAllocationResDto;
import com.asset.demo.model.AssetAllocation;
import com.asset.demo.service.AssetAllocationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/asset-allocation")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AssetAllocationController {
    private final AssetAllocationService assetAllocationService;





    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAllocations(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size

    ){

        List<AssetAllocationResDto> list=assetAllocationService.getAllAllocations(page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    /* Access : ADMIN */
    @PutMapping("/allocate/{assetRequestId}")
    public ResponseEntity<?> allocateAsset(Principal principal,
                                           @PathVariable long assetRequestId){
        assetAllocationService.allocateAsset(principal.getName(),assetRequestId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    /* Access : ADMIN */
    @PutMapping("/reject/{assetRequestId}")
    public ResponseEntity<?> rejectRequest(@PathVariable long assetRequestId){
        assetAllocationService.rejectAsset(assetRequestId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    /* Access : EMPLOYEE */
    @PutMapping("/return-asset-request/{assetAllocationId}")
    public ResponseEntity<?> returnAsset(
            @PathVariable long assetAllocationId,
            Principal principal
    ){

        assetAllocationService.returnAsset(principal.getName(),assetAllocationId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    /* Access : EMPLOYEE */
    @GetMapping("/get-all/allocated")
    public ResponseEntity<?> getAllAllocatedAsset(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            Principal principal
    ){

        List<AssetAllocationResDto> list=assetAllocationService.getAllAllocatedByUser(principal.getName(),page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    /* Access : EMPLOYEE */
    @PutMapping("/accept-return-request/{assetAllocationId}")
    public ResponseEntity<?> acceptReturnRequest(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            @PathVariable long assetAllocationId,
            Principal principal
    ){

        assetAllocationService.acceptReturnRequest(assetAllocationId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();

    }


    @GetMapping("/get/status/{status}")
    public ResponseEntity<List<AssetAllocationResDto>> getAllByStatus(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            @PathVariable String status
    ){

        List<AssetAllocationResDto>list=assetAllocationService.getAllByStatus(status,page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=assetAllocationService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }
    @GetMapping("/count-by-user")
    public ResponseEntity<Long> getAllocationsByUser(Principal principal){
        long count=assetAllocationService.getCountByUser(principal.getName());
        return ResponseEntity
                .ok()
                .body(count);
    }

    @GetMapping("/get/user/status/{status}")
    public ResponseEntity<AssetAllocationPageResDto> getAllByUserStatus(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            @PathVariable String status,
            Principal principal
    ){

        AssetAllocationPageResDto assetAllocationPageResDto =assetAllocationService.getAllByUserStatus(status,page,size,principal.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(assetAllocationPageResDto);

    }



}
