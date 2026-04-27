package com.asset.demo.controller;

import com.asset.demo.dto.*;
import com.asset.demo.enums.ServiceStatus;
import com.asset.demo.model.ServiceRequest;
import com.asset.demo.service.ServiceRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/service-request")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    /* Access : EMPLOYEE */
    @PostMapping("/request-service/{assetAllocationId}")
    public ResponseEntity<Object> requestService(@RequestBody ServiceRequestReqDto serviceRequestReqDto,
                                                 Principal principal,
                                                 @PathVariable long assetAllocationId){
        serviceRequestService.requestService(serviceRequestReqDto,principal.getName(),assetAllocationId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    /* Access : ADMIN */
    @PutMapping("/accept-service-request/{serviceRequestId}")
    public ResponseEntity<Object> acceptServiceRequest(@PathVariable long serviceRequestId,
                                                       Principal principal){


        serviceRequestService.acceptRequest(principal.getName(),serviceRequestId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }
    /* Access : ADMIN */
    @PutMapping("/reject-service-request/{serviceRequestId}")
    public ResponseEntity<Object> rejectServiceRequest(@PathVariable long serviceRequestId,
                                                       Principal principal){


        serviceRequestService.rejectRequest(serviceRequestId,principal.getName());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }
    /* Access : ADMIN */
    @PutMapping("/resolved-service-request/{serviceRequestId}")
    public ResponseEntity<Object> resolvedServiceRequest(@PathVariable long serviceRequestId){


        serviceRequestService.resolvedRequest(serviceRequestId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    /* Access : EMPLOYEE */
    @GetMapping("/user/get-all")
    public ResponseEntity<ServiceRequestResponseDto> getServiceRequestByUsername(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            Principal principal
    ){

        ServiceRequestResponseDto serviceRequestResponseDto =serviceRequestService.getRequestByUsername(principal.getName(),page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(serviceRequestResponseDto);
    }

    @GetMapping("/get-all")
    public ResponseEntity<ServiceRequestResponseDto> getAllServiceRequest(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                                           @RequestParam(value = "size",required = false,defaultValue = "5")int size
                                                                           ){
        ServiceRequestResponseDto serviceRequestResponseDto=serviceRequestService.getAll(page,size);
        return ResponseEntity
                .ok()
                .body(serviceRequestResponseDto);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ServiceRequestResponseDto> getByStatus(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                                 @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                                                 @PathVariable String status
    ){
        ServiceRequestResponseDto serviceRequestResponseDto=serviceRequestService.getAllByStatus(page,size,status);
        return ResponseEntity
                .ok()
                .body(serviceRequestResponseDto);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=serviceRequestService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }
    @GetMapping("/count-by-user")
    public ResponseEntity<Long> getByUser(Principal principal){
        long count=serviceRequestService.getCountByUser(principal.getName());
        return ResponseEntity
                .ok()
                .body(count);
    }

    @DeleteMapping("/delete/{serviceRequestId}")
    public ResponseEntity<Object> deleteRequest(@PathVariable long serviceRequestId,
                                                Principal principal){
        serviceRequestService.deleteRequest(serviceRequestId,principal);
        return ResponseEntity
                .noContent()
                .build();

    }

    @GetMapping("/user/status/{status}")
    public ResponseEntity<ServiceRequestPageResDto> getByUserStatus(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                  @RequestParam(value = "size",required = false,defaultValue = "5")int size,
                                                  @PathVariable String status,
                                                  Principal principal){
        ServiceRequestPageResDto serviceRequestPageResDto=serviceRequestService.getByUserStatus(status,page,size,principal.getName());
        return ResponseEntity
                .ok()
                .body(serviceRequestPageResDto);
    }

    @GetMapping("/service-status")
    public ResponseEntity<List<FilterResDto>> getUserStatusV2(){
        List<FilterResDto> list=new ArrayList<>();
        list.add(new FilterResDto("ALL","ALL"));
        for (ServiceStatus value : ServiceStatus.values()) {
            list.add(new FilterResDto(value.toString(),value.toString()));
        }
        return  ResponseEntity
                .ok()
                .body(list);

    }
}
