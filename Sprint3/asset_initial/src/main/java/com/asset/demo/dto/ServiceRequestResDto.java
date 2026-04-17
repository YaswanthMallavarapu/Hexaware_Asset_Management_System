package com.asset.demo.dto;

import com.asset.demo.enums.ServiceStatus;

import java.time.Instant;
import java.time.LocalDate;

public record ServiceRequestResDto(
        long id,
        long assetId,
        String assetName,
        String description,
        long employeeId,
        String employeeName,
        long managerId,
        Instant requestDate,
        ServiceStatus status
) {
}
