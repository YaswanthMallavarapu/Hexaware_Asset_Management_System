package com.asset.demo.dto;

public record AdminReqDto(
        String firstName,
        String lastName,
        String username,
        String password
) {
}
