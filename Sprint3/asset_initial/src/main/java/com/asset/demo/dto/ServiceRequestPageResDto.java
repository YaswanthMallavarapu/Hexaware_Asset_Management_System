package com.asset.demo.dto;

import java.util.List;

public record ServiceRequestPageResDto(
        List<ServiceRequestResDto> list,
        long totalElements,
        long totalPages
) {
}
