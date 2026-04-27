package com.asset.demo.controller;

import com.asset.demo.dto.AssetRequestPageResDto;
import com.asset.demo.dto.AssetRequestReqDto;
import com.asset.demo.dto.AssetRequestResDto;
import com.asset.demo.dto.FilterResDto;
import com.asset.demo.enums.RequestStatus;
import com.asset.demo.model.AssetRequest;
import com.asset.demo.model.ServiceRequest;
import com.asset.demo.service.AssetRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/asset-request")
@CrossOrigin(origins = "http://localhost:5173/")
public class AssetRequestController {

    private final AssetRequestService assetRequestService;
    /* Access :  EMPLOYEE */
    @PostMapping("/add/{assetId}")
    public ResponseEntity<Object> requestAsset(@RequestBody AssetRequestReqDto assetRequestReqdto,
                                               @PathVariable long assetId,
                                               Principal principal){
        assetRequestService.requestAsset(assetRequestReqdto,assetId,principal.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    /* Access :  EMPLOYEE */
    @PostMapping("/add/{assetId}/{employeeId}/v1")
    public ResponseEntity<Object> requestAssetV1(@RequestBody AssetRequestReqDto assetRequestReqdto,
                                                 @PathVariable long assetId,
                                                 @PathVariable long employeeId){
        assetRequestService.requestAssetV1(assetRequestReqdto,assetId,employeeId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }
    /* Access : MANAGER  */
    @GetMapping("/get-all")
    public ResponseEntity<List<AssetRequestResDto>> getAllAssetRequests(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                                        @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        List<AssetRequestResDto> list=assetRequestService.getAllAssetRequests(page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    /* Access : EMPLOYEE  */
    @GetMapping("/get/user")
    public ResponseEntity<AssetRequestPageResDto> getAssetRequestByUser(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                                        @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                                                        Principal principal){
        AssetRequestPageResDto assetRequestPageResDto =assetRequestService.getRequestByUser(principal.getName(),page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(assetRequestPageResDto);

    }

    /* Access : MANAGER  */
    @GetMapping("/get/status/{status}")
    public ResponseEntity<List<AssetRequestResDto>> getAssetRequestByStatus(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                                            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                                                            @PathVariable String status){
        List<AssetRequestResDto>list=assetRequestService.getRequestByStatus(status,page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }
    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=assetRequestService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }

    @GetMapping("/count-by-user")
    public ResponseEntity<Long> getRequestByUser(Principal principal){
        long count=assetRequestService.getCountByUser(principal.getName());
        return ResponseEntity
                .ok()
                .body(count);
    }

    @DeleteMapping("/delete/{assetRequestId}")
    public void deleteAssetRequest(Principal principal,
                                   @PathVariable long assetRequestId){
        assetRequestService.deleteAssetRequest(assetRequestId);
        ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();

    }

    @GetMapping("/get/user/status/{status}")
    public ResponseEntity<AssetRequestPageResDto> getAssetRequestByUserStatus(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                              @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                                              @PathVariable String status,
                                                              Principal principal){
        AssetRequestPageResDto list=assetRequestService.getRequestByUserStatus(status,page,size,principal.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    @GetMapping("/request-status")
    public ResponseEntity<List<FilterResDto>> getUserStatusV2(){
        List<FilterResDto> list=new ArrayList<>();
        list.add(new FilterResDto("ALL","ALL"));
        for (RequestStatus value : RequestStatus.values()) {
            list.add(new FilterResDto(value.toString(),value.toString()));
        }
        return  ResponseEntity
                .ok()
                .body(list);

    }

}
