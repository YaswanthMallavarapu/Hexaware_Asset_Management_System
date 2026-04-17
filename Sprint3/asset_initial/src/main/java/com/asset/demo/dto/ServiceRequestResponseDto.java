package com.asset.demo.dto;

import java.util.List;

public record ServiceRequestResponseDto(
        List<ServiceRequestResDto>list,
        long totalPages,
        long totalRecords
) {
}
