package com.asset.demo.controller;

import com.asset.demo.dto.ServiceRequestReqDto;
import com.asset.demo.dto.ServiceRequestResDto;
import com.asset.demo.model.ServiceRequest;
import com.asset.demo.service.ServiceRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/service-request")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    /* Access : EMPLOYEE */
    @PostMapping("/request-service/{assetAllocationId}")
    public ResponseEntity<?> requestService(@RequestBody ServiceRequestReqDto serviceRequestReqDto,
                                            Principal principal,
                                            @PathVariable long assetAllocationId){
        serviceRequestService.requestService(serviceRequestReqDto,principal.getName(),assetAllocationId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    /* Access : ADMIN */
    @PutMapping("/accept-service-request/{serviceRequestId}")
    public ResponseEntity<?> acceptServiceRequest(@PathVariable long serviceRequestId,
                                                  Principal principal){


        serviceRequestService.acceptRequest(principal.getName(),serviceRequestId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }
    /* Access : ADMIN */
    @PutMapping("/reject-service-request/{serviceRequestId}")
    public ResponseEntity<?> rejectServiceRequest(@PathVariable long serviceRequestId,
                                                  Principal principal){


        serviceRequestService.rejectRequest(serviceRequestId,principal.getName());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }
    /* Access : ADMIN */
    @PutMapping("/resolved-service-request/{serviceRequestId}")
    public ResponseEntity<?> resolvedServiceRequest(@PathVariable long serviceRequestId){


        serviceRequestService.resolvedRequest(serviceRequestId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    /* Access : EMPLOYEE */
    @GetMapping("/user/get-all")
    public ResponseEntity<?> getServiceRequestByUsername(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            Principal principal
    ){

        List<ServiceRequestResDto> list=serviceRequestService.getRequestByUsername(principal.getName(),page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=serviceRequestService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }
}
