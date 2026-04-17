package com.asset.demo.dto;

public record ManagerDocumentResDto(
        long id,
        String profileImage,
        long managerId,
        String managerName
        ) {
}
