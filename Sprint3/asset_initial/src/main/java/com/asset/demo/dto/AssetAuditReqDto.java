package com.asset.demo.dto;

import com.asset.demo.enums.AuditStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssetAuditReqDto(
        @NotNull
        @NotBlank
        AuditStatus status
) {
}
