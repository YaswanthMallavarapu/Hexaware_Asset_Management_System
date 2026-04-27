package com.asset.demo.service;

import com.asset.demo.dto.*;
import com.asset.demo.enums.AuditStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.AssetAuditResultMapper;
import com.asset.demo.model.*;
import com.asset.demo.repository.AssetAuditRepository;
import com.asset.demo.repository.AssetAuditResultRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AssetAuditService {

    private final AssetAuditRepository assetAuditRepository;
    private final AssetAuditResultService assetAuditResultService;
    private final AssetAuditResultRepository assetAuditResultRepository;
    private final AssetAllocationService assetAllocationService;
    private final UserService userService;
    private final AssetService assetService;
    private final ManagerService managerService;

    public void auditAsset(AssetAuditReqDto assetAuditReqDto, long assetAuditResultId) {

        log.atInfo().log("Auditing asset resultId {} with status {}",
                assetAuditResultId, assetAuditReqDto.status());

        AssetAuditResult result = assetAuditResultRepository.findById(assetAuditResultId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid id..."));

        result.setStatus(assetAuditReqDto.status());
        assetAuditResultRepository.save(result);

        log.atInfo().log("Audit updated for resultId {}", assetAuditResultId);
    }

    public void getAllAssetToBeAudited(String name) {

        log.atInfo().log("Starting audit for manager {}", name);

        AssetAudit audit = new AssetAudit();
        Manager manager = managerService.getManagerByUsername(name);

        audit.setManager(manager);
        assetAuditRepository.save(audit);

        List<AssetAllocation> list = assetAllocationService.getAllAllocatedAssets();

        log.atInfo().log("Found {} allocated assets for auditId {}", list.size(), audit.getId());

        assetAuditResultService.saveAll(list, audit);

        log.atInfo().log("Audit initialized successfully for auditId {}", audit.getId());
    }

    public List<AuditDateDto> getAllAuditDates() {

        log.atInfo().log("Fetching all audit dates");

        return assetAuditRepository.getAllAuditDates();
    }

    public List<AssetAuditResultResDto> getAllAuditResults(long auditId, int page, int size) {

        log.atInfo().log("Fetching audit results for auditId {} page={} size={}",
                auditId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AssetAuditResult> pageResult =
                assetAuditResultService.getByAuditId(auditId, pageable);

        return pageResult.getContent()
                .stream()
                .map(AssetAuditResultMapper::mapToDto)
                .toList();
    }

    public long getCount() {

        long count = assetAuditRepository.count();
        log.atInfo().log("Total audits count {}", count);

        return count;
    }

    public AssetAuditResStatusdto getAllAssetByStatus(int page, int size, long id, String status) {

        log.atInfo().log("Fetching auditId {} results with status {} page={} size={}",
                id, status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        AuditStatus auditStatus = AuditStatus.valueOf(status);

        Page<AssetAuditResult> pageResult =
                assetAuditResultRepository.getAllByAuditIdAndStatus(id, auditStatus, pageable);

        List<AssetAuditResultResDto> list = pageResult.getContent()
                .stream()
                .map(AssetAuditResultMapper::mapToDto)
                .toList();

        return new AssetAuditResStatusdto(
                list,
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
}