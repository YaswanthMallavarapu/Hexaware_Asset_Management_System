package com.asset.demo.dto;

import java.util.List;

public record AssetAuditResStatusdto(
        List<AssetAuditResultResDto>list,
        long totalRecords,
        long totalPages

) {
}
