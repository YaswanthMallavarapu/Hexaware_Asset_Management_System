package com.asset.demo.service;

import com.asset.demo.dto.AssetAuditReqDto;
import com.asset.demo.dto.AssetAuditResultResDto;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.AssetAuditResultMapper;
import com.asset.demo.model.*;
import com.asset.demo.repository.AssetAuditRepository;
import com.asset.demo.repository.AssetAuditResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class AssetAuditService {
    private final AssetAuditRepository assetAuditRepository;
    private final AssetAuditResultService assetAuditResultService;
    private final AssetAuditResultRepository assetAuditResultRepository;
    private final AssetAllocationService assetAllocationService;
    private final UserService userService;
    private final AssetService assetService;
    private final ManagerService managerService;


    public void auditAsset(AssetAuditReqDto assetAuditReqDto,long assetAuditResultId) {
        AssetAuditResult assetAuditResult=assetAuditResultRepository.findById(assetAuditResultId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid id..."));
        assetAuditResult.setStatus(assetAuditReqDto.status());
        assetAuditResultRepository.save(assetAuditResult);
    }

    public void getAllAssetToBeAudited(String name) {

        AssetAudit audit=new AssetAudit();
        Manager manager=managerService.getManagerByUsername(name);
        audit.setManager(manager);
        assetAuditRepository.save(audit);
        List<AssetAllocation> list=assetAllocationService.getAllAllocatedAssets();
        assetAuditResultService.saveAll(list,audit);
    }

    public List<Instant> getAllAuditDates() {
        return assetAuditRepository.getAllAuditDates();
    }

    public List<AssetAuditResultResDto> getAllAuditResults(long auditId, int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<AssetAuditResult> pageResult= assetAuditResultService.getByAuditId(auditId,pageable);
        return pageResult
                .toList()
                .stream()
                .map(AssetAuditResultMapper::mapToDto)
                .toList();
    }
}
