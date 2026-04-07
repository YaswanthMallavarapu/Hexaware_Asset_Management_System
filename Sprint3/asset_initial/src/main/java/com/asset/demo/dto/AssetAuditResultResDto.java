package com.asset.demo.dto;

import com.asset.demo.enums.AuditStatus;

import java.time.LocalDate;

public record AssetAuditResultResDto(
        long id,
        long assetId,
        String assetName,
        long assetAllocationId,
        long auditId,
        LocalDate auditDate,
        AuditStatus status
) {
}
