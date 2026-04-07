package com.asset.demo.service;

import com.asset.demo.dto.AssetAllocationAuditResDto;
import com.asset.demo.enums.AuditStatus;
import com.asset.demo.model.AssetAllocation;
import com.asset.demo.model.AssetAudit;
import com.asset.demo.model.AssetAuditResult;
import com.asset.demo.repository.AssetAuditResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AssetAuditResultService {
    private final AssetAuditResultRepository assetAuditResultRepository;
    public void saveAll(List<AssetAllocation> auditList, AssetAudit assetAudit) {
        auditList.forEach(audit->{
            AssetAuditResult assetAuditResult=new AssetAuditResult();
            assetAuditResult.setAssetAllocation(audit);
            assetAuditResult.setStatus(AuditStatus.PENDING);
            assetAuditResult.setAudit(assetAudit);
            assetAuditResultRepository.save(assetAuditResult);
        });

    }

    public Page<AssetAuditResult> getByAuditId(long auditId, Pageable pageable) {
        return assetAuditResultRepository.getByAuditId(auditId,pageable);
    }
}
