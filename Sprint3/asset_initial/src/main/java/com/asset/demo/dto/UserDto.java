package com.asset.demo.dto;

import com.asset.demo.enums.Role;

public record UserDto(
        String username,
        Role role
) {
}
