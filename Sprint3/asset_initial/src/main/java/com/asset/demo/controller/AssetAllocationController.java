package com.asset.demo.controller;

import com.asset.demo.dto.AssetAllocationPageResDto;
import com.asset.demo.dto.AssetAllocationResDto;
import com.asset.demo.dto.FilterResDto;
import com.asset.demo.enums.AllocationStatus;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.model.AssetAllocation;
import com.asset.demo.service.AssetAllocationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.capitalize;

@RestController
@RequestMapping("/api/asset-allocation")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AssetAllocationController {
    private final AssetAllocationService assetAllocationService;





    @GetMapping("/get-all")
    public ResponseEntity<List<AssetAllocationResDto>> getAllAllocations(
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
    public ResponseEntity<Object> allocateAsset(Principal principal,
                                           @PathVariable long assetRequestId){
        assetAllocationService.allocateAsset(principal.getName(),assetRequestId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    /* Access : ADMIN */
    @PutMapping("/reject/{assetRequestId}")
    public ResponseEntity<Object> rejectRequest(@PathVariable long assetRequestId){
        assetAllocationService.rejectAsset(assetRequestId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    /* Access : EMPLOYEE */
    @PutMapping("/return-asset-request/{assetAllocationId}")
    public ResponseEntity<Object> returnAsset(
            @PathVariable long assetAllocationId,
            Principal principal
    ){

        assetAllocationService.returnAsset(principal.getName(),assetAllocationId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @PutMapping("/cancel-return-asset-request/{assetAllocationId}")
    public ResponseEntity<Object> cancelReturnRequest(
            @PathVariable long assetAllocationId,
            Principal principal
    ){

        assetAllocationService.cancelReturnAsset(principal.getName(),assetAllocationId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    /* Access : EMPLOYEE */
    @GetMapping("/get-all/allocated")
    public ResponseEntity<AssetAllocationPageResDto> getAllAllocatedAsset(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            Principal principal
    ){

        AssetAllocationPageResDto assetAllocationPageResDto =assetAllocationService.getAllAllocatedByUser(principal.getName(),page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(assetAllocationPageResDto);

    }

    /* Access : EMPLOYEE */
    @PutMapping("/accept-return-request/{assetAllocationId}")
    public ResponseEntity<Object> acceptReturnRequest(
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


    @GetMapping("/allocation-status")
    public ResponseEntity<List<FilterResDto>> getUserStatusV2(){
        List<FilterResDto> list=new ArrayList<>();
        list.add(new FilterResDto("ALL","ALL"));
        for (AllocationStatus value : AllocationStatus.values()) {
            list.add(new FilterResDto(value.toString(),value.toString()));
        }
        return  ResponseEntity
                .ok()
                .body(list);

    }



}
