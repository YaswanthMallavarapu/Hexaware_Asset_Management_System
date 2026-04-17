package com.asset.demo.dto;

import java.time.Instant;

public record AuditDateDto(
        long id,
        long managerId,
        Instant auditDate
) {
}
