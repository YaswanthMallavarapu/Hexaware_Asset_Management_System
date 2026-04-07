package com.asset.demo.dto;

import com.asset.demo.enums.AuditStatus;
import com.asset.demo.model.Asset;
import com.asset.demo.model.User;

import java.time.LocalDate;

public record AssetAllocationAuditResDto(
        long id,

        User employee,
        Asset asset,

        LocalDate allocationDate,
        AuditStatus status
) {
}
