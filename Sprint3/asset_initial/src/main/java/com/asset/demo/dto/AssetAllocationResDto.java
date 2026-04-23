package com.asset.demo.dto;

import com.asset.demo.enums.AllocationStatus;

import java.time.LocalDate;

public record AssetAllocationResDto(
        long id,
        long assetId,
        long employeeId,
        String assetName,
        String employeeName,
        String managerName,
        LocalDate allocatedDate,
        AllocationStatus status
) {
}
