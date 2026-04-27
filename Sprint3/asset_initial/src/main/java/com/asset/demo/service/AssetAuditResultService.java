package com.asset.demo.service;

import com.asset.demo.dto.AssetAllocationAuditResDto;
import com.asset.demo.enums.AuditStatus;
import com.asset.demo.model.AssetAllocation;
import com.asset.demo.model.AssetAudit;
import com.asset.demo.model.AssetAuditResult;
import com.asset.demo.repository.AssetAuditResultRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AssetAuditResultService {

    private final AssetAuditResultRepository assetAuditResultRepository;

    public void saveAll(List<AssetAllocation> auditList, AssetAudit assetAudit) {

        log.atInfo().log("Saving audit results for auditId {} with {} allocations",
                assetAudit.getId(), auditList.size());

        auditList.forEach(audit -> {
            AssetAuditResult result = new AssetAuditResult();
            result.setAssetAllocation(audit);
            result.setStatus(AuditStatus.PENDING);
            result.setAudit(assetAudit);

            assetAuditResultRepository.save(result);
        });

        log.atInfo().log("Audit results saved successfully for auditId {}", assetAudit.getId());
    }

    public Page<AssetAuditResult> getByAuditId(long auditId, Pageable pageable) {

        log.atInfo().log("Fetching audit results for auditId {} page={} size={}",
                auditId, pageable.getPageNumber(), pageable.getPageSize());

        return assetAuditResultRepository.getByAuditId(auditId, pageable);
    }
}