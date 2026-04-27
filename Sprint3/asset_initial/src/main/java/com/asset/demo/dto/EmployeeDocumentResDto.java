package com.asset.demo.dto;

import com.asset.demo.enums.Role;

public record EmployeeDocumentResDto(
        String fullName,
        String username,
        Role role,
        String profile
) {
}
