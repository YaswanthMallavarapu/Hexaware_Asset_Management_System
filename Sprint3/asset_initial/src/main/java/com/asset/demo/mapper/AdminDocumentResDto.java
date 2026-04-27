package com.asset.demo.mapper;

import com.asset.demo.enums.Role;

public record AdminDocumentResDto(
        String fullName,
        String username,
        Role role,
        String profile
) {
}
