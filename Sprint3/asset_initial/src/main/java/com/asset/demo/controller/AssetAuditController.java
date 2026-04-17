package com.asset.demo.controller;

import com.asset.demo.dto.AssetAllocationAuditResDto;
import com.asset.demo.dto.AssetAuditReqDto;
import com.asset.demo.dto.AssetAuditResultResDto;
import com.asset.demo.dto.AuditDateDto;
import com.asset.demo.model.Asset;
import com.asset.demo.model.AssetAllocation;
import com.asset.demo.model.AssetAuditResult;
import com.asset.demo.service.AssetAuditService;
import jakarta.servlet.ServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/asset-audit")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AssetAuditController {
    private final AssetAuditService assetAuditService;
    /* Access : ADMIN */
    @PostMapping("/audit/{assetAuditResultId}")
    public ResponseEntity<?> auditAsset(
            @RequestBody AssetAuditReqDto assetAuditReqDto,
            @PathVariable long assetAuditResultId
    ){
        assetAuditService.auditAsset(assetAuditReqDto,assetAuditResultId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    /* ACCESS : ADMIN */
    @PostMapping("/create")
    public ResponseEntity<?> getAllAssetForAudit(
            Principal principal
            ){
        assetAuditService.getAllAssetToBeAudited(principal.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();


    }

    @GetMapping("/get-all-audit-dates")
    public ResponseEntity<List<AuditDateDto>> getAllAssetAuditDates(
    ){
       List<AuditDateDto>auditDates=assetAuditService.getAllAuditDates();
       return ResponseEntity
               .status(HttpStatus.OK)
               .body(auditDates);
    }

    @GetMapping("/get-all/{auditId}")
    public ResponseEntity<List<AssetAuditResultResDto>> getAllAssetAuditDates(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            @PathVariable long auditId
    ){
        List<AssetAuditResultResDto>audits=assetAuditService.getAllAuditResults(auditId,page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(audits);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=assetAuditService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }




}
